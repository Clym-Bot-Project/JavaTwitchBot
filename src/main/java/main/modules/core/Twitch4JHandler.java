package main.modules.core;

import com.github.twitch4j.chat.events.channel.ChannelJoinEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.CheerEvent;
import com.github.twitch4j.chat.events.channel.FollowEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.common.events.domain.EventUser;
import main.system.commandSystem.CommandProcessor;
import main.system.commandSystem.repositories.Message;
import main.system.commandSystem.repositories.TwitchUser;
import main.system.commandSystem.repositories.TwitchUserPermissions;
import main.system.eventSystem.EventDispatcher;
import main.system.eventSystem.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static main.system.commandSystem.repositories.TwitchUserPermissions.*;

/**
 * Handelt alle Events, die der Core braucht und Twitch4J sind.
 * Eine Liste dazu findet sich in den Unterklassen vom TwitchEvent
 *
 * @see com.github.twitch4j.chat.events.TwitchEvent
 */
public class Twitch4JHandler {
    private static final Logger logger = LoggerFactory.getLogger(Twitch4JHandler.class);
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("DD-HH:mm:ss.SSS");

    @Subscribe(EventClass = ChannelMessageEvent.class)
    public static void MessageEvent(ChannelMessageEvent messageEvent) {
        if (messageEvent.getMessage().toCharArray()[0] == '!')
            System.out.println(simpleDateFormat.format(new Date()) + " |CHAT | " + messageEvent.getUser().getName() + ": " + messageEvent.getMessage());

        //Convert ChatMessage
        EventUser eUser = messageEvent.getUser();
        TwitchUser user = new TwitchUser(eUser.getId(), eUser.getName(), messageEvent.getSubscriberMonths(), messageEvent.getSubscriptionTier(), translatePerms(messageEvent.getPermissions()));
        String replyToID = null;
        if (messageEvent.getReplyInfo() != null)
            replyToID = messageEvent.getReplyInfo().getMessageId();
        Message message = new Message(
                messageEvent.getMessageEvent().getEventId(),
                messageEvent.getMessage(),
                user,
                messageEvent.isHighlightedMessage(),
                messageEvent.isHighlightedMessage(),
                messageEvent.isDesignatedFirstMessage(),
                messageEvent.isUserIntroduction(),
                messageEvent.getCustomRewardId().orElse(null),
                replyToID,
                messageEvent.getChannel().getId(),
                messageEvent.getFiredAtInstant());
        CommandProcessor.processMessage(message);
    }


    //Das sind nur schnelle Beispiele um zu testen, dass es funktioniert
    @Subscribe(EventClass = CheerEvent.class)
    @Deprecated(forRemoval = true)
    public static void CheerEvent(CheerEvent cheerEvent) {
        //Do Database Stuff
        HashSet<CommandPermission> permissions = new HashSet<>();
        permissions.add(CommandPermission.MODERATOR);
        permissions.add(CommandPermission.BROADCASTER);

        EventDispatcher.dispatch(new ChannelMessageEvent(cheerEvent.getChannel(), cheerEvent.getMessageEvent(), cheerEvent.getUser(), "!testCommand", permissions));
    }

    @Subscribe(EventClass = FollowEvent.class)
    @Deprecated(forRemoval = true)
    public static void FollowEvent(FollowEvent followEvent) {
        System.out.println("Twitch4JHandler.FollowEvent");
//        InputManager.receiveChatMessage(new ChannelMessageEvent(
//                followEvent.getChannel(),
//                null,
//                followEvent.getUser(),
//                "!testCommand",
//                null));
    }

    @Subscribe(EventClass = ChannelJoinEvent.class)
    @Deprecated(forRemoval = true)
    public static void ChannelJoinEvent(ChannelJoinEvent channelJoinEvent) {
        logger.debug("{} joined! ", channelJoinEvent.getUser());
    }

    private static HashSet<TwitchUserPermissions> translatePerms(Set<CommandPermission> permissions) {
        HashSet<TwitchUserPermissions> newPerm = new HashSet<>();
        for (CommandPermission cp:permissions) {
            switch (cp) {
                case EVERYONE -> newPerm.add(EVERYONE);
                case SUBSCRIBER -> newPerm.add(SUBSCRIBER);
                case FOUNDER -> newPerm.add(FOUNDER);
                case PREDICTIONS_BLUE -> newPerm.add(PREDICTIONS_BLUE);
                case PREDICTIONS_PINK -> newPerm.add(PREDICTIONS_PINK);
                case ARTIST -> newPerm.add(ARTIST);
                case VIP -> newPerm.add(VIP);
                case MODERATOR -> newPerm.add(MODERATOR);
                case BROADCASTER -> newPerm.add(BROADCASTER);
                case OWNER -> newPerm.add(OWNER);
                //PRIME_TURBO, NO_VIDEO, MOMENTS, NO_AUDIO, TWITCHSTAFF, SUBGIFTER, BITS_CHEERER, PARTNER, FORMER_HYPE_TRAIN_CONDUCTOR, CURRENT_HYPE_TRAIN_CONDUCTOR -> {}
                default -> {}
            }
        }
        return newPerm;
    }

}
