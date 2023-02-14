package main.system.inputSystem;


public interface TwitchBotInput extends Runnable {
    boolean checkConfiguration();

    boolean shutdown();

    @Override
    void run();

    boolean running();

}
