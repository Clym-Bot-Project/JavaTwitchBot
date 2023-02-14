package main.system.commandSystem;

import main.system.commandSystem.repositories.*;
import main.system.hookSystem.HookParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.regex.Pattern;

public class CommandProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CommandProcessor.class);
    private static final Logger chat = LoggerFactory.getLogger(CommandProcessor.class.getName() + ".Chat");
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("DD-HH:mm:ss.SSS");

    private static HashSet<CachedCommand> cachedCommands = cacheCommands();

    private record CachedCommand(
            Pattern regex,
            String commandID
    ) {}

    public CommandProcessor() {}

    private static HashSet<CachedCommand> cacheCommands() {
        HashSet<CachedCommand> cached = new HashSet<>();
        //For better performance, simple matcher could be matched without regex,
        //by finding the String till the first " " and comparing that with the matcher
        int lenght = 0;
        for (Command command : Command.repo.findAll()) {
            lenght++;
            Pattern pattern;
            if (command.regexMatcher())
                pattern = Pattern.compile(command.matcherString());
            else
                pattern = Pattern.compile("^" + command.matcherString() + "( |$).*", Pattern.CASE_INSENSITIVE);
            cached.add(new CachedCommand(pattern, command.uniqueName()));
        }
        logger.debug("Number of Commands: {}", lenght);
        logger.debug(cached.toString());
        return cached;
    }

    public static void refreshCache() {
        new Thread(() -> cachedCommands = cacheCommands(), "CommandCacheRefresher").start();
    }

    public static void processMessage(Message message) {
        //TODO auch wieder, Thread Pool wäre vermutlich effizienter und schneller
        new Thread(() -> {
            logger.debug(message.toString());
            for (CachedCommand cached : cachedCommands) {
                match(message, cached);
            }
        }, "CommandProcessor").start();
    }

    private static void match(Message message, CachedCommand cached) {
        //TODO DEBUG statements ausschmücken
        if (!cached.regex().matcher(message.message()).matches()) {
            //If Command does not match to this message, then do nothing
            logger.debug("Does not match");
            return;
        }

        Optional<Command> commandOptional = Command.repo.findById(cached.commandID);
        //If commandOptional with that ID is still in the database and not deleted
        if (commandOptional.isEmpty()) {
            logger.debug("Nothing found!");
            return;
        }
        Command command = commandOptional.get();

        if (!command.active()) {
            logger.debug("is not active");
            return;
        }

        //TODO check Cooldown
//        if (!command.)

        if (!userHasPermission(message.user(), command)) {
            logger.debug("Has not permission");
            return;
        }
        //Check stuff, like permissions
        String response = HookParser.parseCommand(message, command.commandText());

        if (chat.isInfoEnabled())
            System.out.println(simpleDateFormat.format(new Date()) + " |CHAT=| " + message.getUser().name() + ": " + message.message());
        chat.debug("Response Latency: {} Millis", message.sendAT().until(Instant.now(), ChronoUnit.MILLIS));
        if (chat.isInfoEnabled())
            System.out.println(simpleDateFormat.format(new Date()) + " |CHAT=| KMAB: " + response);
    }

    protected static boolean userHasPermission(TwitchUser user, Command command) {
        //Returns true if the user and the command share at least one permission
        logger.debug("user: {}", user.permissions());
        logger.debug("cmd: {}", command.permissions());
        return user.permissions().stream().anyMatch(userPerm -> command.permissions().stream().anyMatch(commandPerm -> commandPerm.equals(userPerm)));
    }

    public static void generateJunkCommands() {
        System.out.println("CommandProcessor.generateJunkCommands");
        HashSet<TwitchUserPermissions> perm = new HashSet<>();
        perm.add(TwitchUserPermissions.MODERATOR);
        Command.repo.save(new Command("junk1", "", "!NB-Junk1", false, perm,
                "Test erfolgreich", true, true, 0, CooldownType.MESSAGES, ""));
        perm.add(TwitchUserPermissions.VIP);
        Command.repo.save(new Command("junk2", "", "!NB-Junk2", false, perm,
                "Time: {currentTime}", true, true, 0, CooldownType.MESSAGES, ""));
        perm.add(TwitchUserPermissions.SUBSCRIBER);
        Command.repo.save(new Command("junk3", "", "!NB-Junk3", false, perm,
                "Sender {sender}", true, true, 0, CooldownType.MESSAGES, ""));
        Command.repo.save(new Command("junk4", "", "!NB-Junk4", false, perm,
                "Test erfolgreich", true, true, 0, CooldownType.MESSAGES, ""));
    }
}
