package main.system.commandSystem;

import main.Application;
import main.system.commandSystem.repositories.Command;
import main.system.commandSystem.repositories.TwitchUser;
import main.system.commandSystem.repositories.TwitchUserPermissions;
import org.junit.jupiter.api.*;

import java.util.HashSet;

@Disabled
class CommandProcessorTest {


    @Test
    void hasMod() {
        TwitchUser user = TwitchUser.getSystemUser();
        HashSet<TwitchUserPermissions> perms = new HashSet<>();
        perms.add(TwitchUserPermissions.MODERATOR);
        Command command = new Command().setPermissions(perms);
        boolean b = CommandProcessor.userHasPermission(user, command);
        assert b;
    }
    @Test
    void hasNotPink() {
        TwitchUser user = TwitchUser.getSystemUser();
        HashSet<TwitchUserPermissions> perms = new HashSet<>();
        perms.add(TwitchUserPermissions.PREDICTIONS_PINK);
        Command command = new Command().setPermissions(perms);
        boolean b = CommandProcessor.userHasPermission(user, command);
        assert !b;
    }
    @Test
    void hasModIsNotBlue() {
        TwitchUser user = TwitchUser.getSystemUser();
        HashSet<TwitchUserPermissions> perms = new HashSet<>();
        perms.add(TwitchUserPermissions.MODERATOR);
        perms.add(TwitchUserPermissions.PREDICTIONS_BLUE);
        Command command = new Command().setPermissions(perms);
        boolean b = CommandProcessor.userHasPermission(user, command);
        assert b;
    }

    @BeforeAll
    static void setUp() {
        Application.startup();
    }

    @AfterAll
    static void tearDown() {
    }

}