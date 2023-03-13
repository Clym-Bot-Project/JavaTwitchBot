package main;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.PreRemove;
import main.system.StopWatch;
import main.system.commandSystem.CommandProcessor;
import main.system.inputSystem.InputManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.Instant;

@SpringBootApplication
@EnableJpaRepositories
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        startup();
    }

    public static void startup() {
        StopWatch time = new StopWatch(StopWatch.TYPE.STARTUP);
        SpringApplication.run(Application.class);
        System.out.println();
        System.out.println("DateFormat: DayNumber-Hour:Minute:Second:Millis");
        System.out.println("DD-HH:mm:ss.SSS |LEVEL| [THREAD]        LOGGER (Source Class)               - MSG");
        System.out.println("----------------|-----|-[-------------]---------------------------------------------------------------------------------------------------------------------------------------------");
        InputManager.startAllInputs();
        new CommandProcessor();
        time.close();
//        CommandProcessor.generateJunkCommands();
    }

    @PreDestroy
    @PreRemove
    public static void shutdown() {
        try {

            Instant start = Instant.now();
            StopWatch time = new StopWatch(StopWatch.TYPE.SHUTDOWN);
            InputManager.shutDownAllInputs();
            time.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
