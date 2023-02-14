package main.system.commandSystem.repositories;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

import static main.system.commandSystem.repositories.TwitchUserPermissions.*;

/**
 * TwitchUser, lediglich dazu gedacht, um einen TwitchUser in einer Chatnachricht zu repräsentieren,
 * und ist nicht dazu gedacht in der Datenbank gespeichert zu werden, denn die Twitch Permissions und Badges könnten
 * sich während der Laufzeit ändern
 * <p>
 * Da wenn eine Nachricht Systemintern entsteht/simuliert wird, einige Informationen fehlen, werden beim Erstellen
 * standard werte eingefügt:
 * name = -SYSTEM-
 * id = -SYSTEM-
 * permissions = OWNER
 * subscriberMonths = Integer.MAX_VALUE
 * subscriptionTier = Integer.MAX_VALUE
 */
@Component
@Table(name = "Core-Twitch_ChatUser")
@Entity
public class TwitchUser {
    @Id private String id;
    private String name;
    private HashSet<TwitchUserPermissions> permissions;
    private int subscriberMonths;
    private int subscriptionTier;

    private static TwitchUserRepo repo;

    @Autowired
    public void setRepo(TwitchUserRepo twitchUserRepo) {
        repo = twitchUserRepo;
    }

    TwitchUser() {
    }

    public TwitchUser(String id, String name, int subscriberMonths, int subscriptionTier, HashSet<TwitchUserPermissions> permissions) {
        this.id = id;
        this.name = name;
        this.subscriberMonths = subscriberMonths;
        this.subscriptionTier = subscriptionTier;
        this.permissions = permissions;
    }

    public TwitchUser(String id, String name, int subscriberMonths, int subscriptionTier, TwitchUserPermissions... permissions) {
        this.id = id;
        this.name = name;
        this.subscriberMonths = subscriberMonths;
        this.subscriptionTier = subscriptionTier;
        this.permissions = new HashSet<>(Arrays.asList(permissions));
    }

    public static TwitchUser getSystemUser() {
        return new TwitchUser("-SYSTEM-", "-SYSTEM-",Integer.MAX_VALUE, Integer.MAX_VALUE, VIP,SUBSCRIBER, FOUNDER, ARTIST, MODERATOR, BROADCASTER, OWNER);
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public HashSet<TwitchUserPermissions> permissions() {
        return permissions;
    }

    public int subscriberMonths() {
        return subscriberMonths;
    }

    public int subscriptionTier() {
        return subscriptionTier;
    }
}
