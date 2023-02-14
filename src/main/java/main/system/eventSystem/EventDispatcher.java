package main.system.eventSystem;

import com.github.twitch4j.chat.events.channel.FollowEvent;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class EventDispatcher {

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
//        for (Method method:eventHandler) {
//            System.out.println(method.getName());
//        }
        dispatch(new FollowEvent(null, null));
    }

    private static final Logger logger = LoggerFactory.getLogger(EventDispatcher.class);

    private static final Set<Method> eventHandler = scanForSubscriber();

    private static Set<Method> scanForSubscriber() {
//        Reflections reflections = new Reflections("main.main.modules", Scanners.MethodsAnnotated);
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages("main.main.modules").addScanners(Scanners.MethodsAnnotated));
        Set<Method> methods = reflections.getMethodsAnnotatedWith(Subscribe.class);
        //Die Methoden müssen static sein und dürfen nur einen Parameter haben,
        // und dieser Parameter muss gleich mit der Klasse in der Annotation sein
        return methods.stream()
                .filter(method -> Modifier.isStatic(method.getModifiers()))
                .filter(method -> method.getParameterCount() == 1)
                .filter(method -> method.getParameterTypes()[0].equals(method.getAnnotation(Subscribe.class).EventClass()))
                .collect(Collectors.toSet());
    }

    public static void dispatch(Object event) {
        //TODO Maybe Thread pool, damit die innit zeiten wegfallen, und der bot schneller antwortet
        new Thread(() -> {
            StringJoiner invokedMethods = new StringJoiner(",");
            for (Method method : eventHandler) {
                if (method.getAnnotation(Subscribe.class).EventClass().equals(event.getClass())) {
                    //Die Methoden müssen static sein und dürfen nur einen Parameter haben,
                    //und dieser Parameter muss gleich mit der Klasse in der Annotation sein
                    //Das ist aber durch scanForSubscriber() gesichert
//                    new Thread(() -> {
                        try {
                            method.invoke(null, event);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            logger.error("Could not invoke Method: {} with Event {}", method, event.getClass(), e);
                            throw new RuntimeException(e);
                        }
//                    }, method.getName()).start();
                    invokedMethods.add(method.getName());
                }
            }
            if (invokedMethods.toString().length() == 0)
                logger.debug("Dispatched Event: {} to No Methods", event.getClass());
            else
                logger.debug("Dispatched Event: {} to Methods: {}", event.getClass(), invokedMethods);
        }, "EventDispatcher").start();
    }
}
