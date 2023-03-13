package main.system.inputSystem;

import main.system.ASCIIProgressbar;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class InputManager {

    private static final Logger logger = LoggerFactory.getLogger(InputManager.class);
    private static HashSet<TwitchBotInput> inputSet = scanForInputs();
    private static volatile boolean running = false;

    InputManager() {
    }

    public static void startAllInputs() {
        logger.info("Starting up Inputs...");
        int iSize = inputSet.size();
        checkConfigs();
        HashSet<String> failing = new HashSet<>();
        HashSet<TwitchBotInput> workSet = inputSet;

        Iterator<TwitchBotInput> iterator = workSet.iterator();
        while (iterator.hasNext()) {
            TwitchBotInput input = iterator.next();
            try {
                new Thread(input, "INPUT-" + input.threadName()).start();
                onSpinWaitTimeout(input, 10000);
                if (!input.running())
                    throw new RuntimeException("Input did not start or keep running after trying to start it!");
            } catch (RuntimeException e) {
                failing.add(input.getClass().getName());
                iterator.remove();
                workSet.remove(input);
                logger.error("Unable to Start Input: {} because:", input.getClass().getName(), e);
            }
        }
        if (workSet.size() != iSize) {
            String differenceString = failing.toString();
            logger.error("Failed to start all Configured Inputs: {}", ASCIIProgressbar.consoleBar(workSet.size(), iSize));
            if (failing.size() > 0)
                logger.error("Failing: {}", differenceString.substring(1, differenceString.length() - 1));
        } else
            logger.info("Checked Configs and Started Inputs successfully: {}", ASCIIProgressbar.consoleBar(iSize, iSize));
        InputManager.inputSet = workSet;
        running = true;
    }

    private static void onSpinWaitTimeout(TwitchBotInput input, int seconds) {
        Instant end = Instant.now().plusSeconds(seconds);
        while (!input.running() && Instant.now().isBefore(end))
            Thread.onSpinWait();
    }

    private static HashSet<String> checkConfigs() {
        int iSize = inputSet.size();
        HashSet<TwitchBotInput> workSet = inputSet;
        HashSet<String> failing = new HashSet<>();

        Iterator<TwitchBotInput> iterator = workSet.iterator();
        while (iterator.hasNext()) {
            TwitchBotInput input = iterator.next();

            if (!input.checkConfiguration()) {
                iterator.remove();
                workSet.remove(input);
                failing.add(input.getClass().getName());
            }
        }
        if (failing.size() > 0) {
            String differenceString = failing.toString();
            logger.error("Error in Configs from Inputs: {}", ASCIIProgressbar.consoleBar(iSize - failing.size(), iSize));
            logger.error("Failing: {}", differenceString.substring(1, differenceString.length() - 1));
        } else
            logger.info("Checked Configs successfully: {}", ASCIIProgressbar.consoleBar(iSize, iSize));
        InputManager.inputSet = workSet;
        return failing;
    }

    public static void shutDownAllInputs() {
        //TODO kill any Inputs if they do not shut down after a certain time
        waitIfStillStarting();
        logger.info("Starting to shutdown the Inputs...");
        HashSet<TwitchBotInput> workSet = inputSet;
        HashSet<String> notProperly = new HashSet<>();
        int iSize = inputSet.size();

        Iterator<TwitchBotInput> iterator = workSet.iterator();
        while (iterator.hasNext()) {
            TwitchBotInput input = iterator.next();
            try {
                if (!input.shutdown()) {
                    throw new RuntimeException("Shutdown routine unsuccessful!");
                }
            } catch (Exception e) {
                notProperly.add(input.getClass().getName());
                logger.error("Unable to properly shutdown Input: {} because: {}", input.getClass().getName(), e);
            }
            iterator.remove();
            workSet.remove(input);
        }

        if (notProperly.size() != 0) {
            String notPString = notProperly.toString();
            logger.error("Failed to shutdown all Inputs: {}", ASCIIProgressbar.consoleBar(iSize - notProperly.size(), iSize));
            logger.error("Failing: {}", notPString.substring(1, notPString.length() - 1));
        } else
            logger.info("Shutdown Inputs successfully: {}", ASCIIProgressbar.consoleBar(iSize, iSize));
        inputSet = workSet;
        running = false;
    }

    private static void waitIfStillStarting() {
        while (!running) {
            Thread.onSpinWait();
        }
    }

    private static HashSet<TwitchBotInput> scanForInputs() {
//        Reflections reflections = new Reflections("main.inputs", Scanners.TypesAnnotated);
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages("main.inputs").addScanners(Scanners.TypesAnnotated) );
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Input.class);
        HashSet<TwitchBotInput> inputSet = new HashSet<>();

        //get all Classes Annotated with Input.class, and if they implement the TwitchBotInput interface,
        //then a new instance will be created, and get added to the Set of Inputs
        for (Class<?> aClass : typesAnnotatedWith) {
            if (Arrays.stream(aClass.getInterfaces()).toList().contains(TwitchBotInput.class)) {
                //Try to create a new instance of that class
                try {
                    inputSet.add((TwitchBotInput) (aClass.getConstructor().newInstance()));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        logger.info("Scanned for Inputs, found: {}, invoked: {}", typesAnnotatedWith.size(), ASCIIProgressbar.consoleBar(inputSet.size(), typesAnnotatedWith.size()));
        return inputSet;
    }

}
