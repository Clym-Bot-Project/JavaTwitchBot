package main.Twitch4J;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "Twitch4J-TwitchAccounts")
@Component
public class TwitchAccount {
    @Id
    public String twitchUserID;
    public String refreshToken;
    public String role;

    public TwitchAccount() {
    }

    public TwitchAccount(String twitchUserID, String refreshToken, String role) {
        this.twitchUserID = twitchUserID;
        this.refreshToken = refreshToken;
        this.role = role;
    }

    public static TwitchAccountRepo repo;

    @Autowired
    public void setTwitchAccountRepo(TwitchAccountRepo twitchAccountRepo) {
        repo = twitchAccountRepo;
    }

}


