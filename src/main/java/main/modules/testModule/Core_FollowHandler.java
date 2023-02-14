package main.modules.testModule;

import com.github.twitch4j.chat.events.channel.FollowEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import main.system.eventSystem.Subscribe;

@SuppressWarnings("unused")
public class Core_FollowHandler {

    @Subscribe(EventClass = FollowEvent.class)
    public static void handleFollow(FollowEvent followEvent) {
        System.out.println("Test follow Event");
    }

    @Subscribe(EventClass = SubscriptionEvent.class)
    public static void handleSub(SubscriptionEvent subscriptionEvent) {
        System.out.println("Sub follow Event");
    }
}
