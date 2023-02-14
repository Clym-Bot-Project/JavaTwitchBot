package main.Twitch4J;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import com.github.twitch4j.chat.events.TwitchEvent;
import com.github.twitch4j.graphql.TwitchGraphQL;
import com.github.twitch4j.helix.TwitchHelix;
import main.system.eventSystem.EventDispatcher;
import main.system.inputSystem.Input;
import main.system.inputSystem.TwitchBotInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Input
@Component
public class Twitch4JInput implements TwitchBotInput {

    //TODO Muss in Globale Config
//    @Value("panelURL") funktioniert nicht
//    private String panel_url;
    private static final String panel_url = "https://localhost/";
    private static final String channelName = "clym";

    //"CONFIG"
    private static String app_clientID = "";

    @Value("${twitchApp_ID}")
    public void setApp_clientID(String app_clientID) {
        Twitch4JInput.app_clientID = app_clientID;
    }
    private static String app_clientSecret = "";

    @Value("${twitchApp_Secret}")
    public void setApp_clientSecret(String app_clientSecret) {
        Twitch4JInput.app_clientSecret = app_clientSecret;
    }


    private static final Logger logger = LoggerFactory.getLogger(Twitch4JInput.class);

    private static TwitchIdentityProvider iProvider;
    private OAuth2Credential oAuth2Credential;
    private TwitchClient twitchClient;
    private boolean running = false;
    public static TwitchHelix broadCasterHelix;
    public static TwitchGraphQL broadCasterGraphQL;

    public Twitch4JInput() {
    }

    @Override
    public boolean checkConfiguration() {
        //TODO fehlen einige Config Tests
        if (TwitchAccount.repo == null) {
            logger.error("TwitchAccountRepository is null!");
            return false;
        }
        if (!TwitchAccount.repo.existsByRole("primary")) {
            logger.error("No OAuth Credentials found!");
            return false;
        }
        //TODO iProvider.isCredentialValid() is valid
        logger.info("Configuration and Credentials found and configured correctly");
        return true;
    }

    @Override
    public void run() {
        logger.debug("Starting... ");
        iProvider = new TwitchIdentityProvider(app_clientID, app_clientSecret, panel_url + "/auth");
        Optional<TwitchAccount> oldCreds = TwitchAccount.repo.getByRole("primary");

        if (oldCreds.isEmpty()) {
            //TODO
//            logger.error("Unable to start, because OAuth Credentials could not be found");
//            throw new RuntimeException("OAuth Credentials not found!");
            oldCreds = Optional.of(injectCred());
        }

        Optional<OAuth2Credential> refreshedCredential = iProvider.refreshCredential(new OAuth2Credential(
                "",
                "",
                oldCreds.get().refreshToken,
                "",
                "",
                0,
                null
        ));

        if (refreshedCredential.isEmpty()) {
            logger.error("Unable to start, because OAuth Credentials could not be refreshed");
            throw new RuntimeException("OAuth Credentials could not be refreshed!");
        }
        oAuth2Credential = refreshedCredential.get();

        TwitchClient twitchClient = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withEnableChat(true)
                .withEnableGraphQL(true)
                .withEnablePubSub(true)
                .withDefaultAuthToken(oAuth2Credential)
                .withChatAccount(oAuth2Credential)
                .withDefaultEventHandler(SimpleEventHandler.class)
                .build();
        twitchClient.getClientHelper().enableStreamEventListener(channelName);
        twitchClient.getChat().joinChannel(channelName);
//        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, EventDispatcher::dispatch);
        twitchClient.getEventManager().onEvent(TwitchEvent.class, EventDispatcher::dispatch);

        this.twitchClient = twitchClient;

        broadCasterGraphQL = twitchClient.getGraphQL();
        broadCasterHelix = twitchClient.getHelix();
        logger.debug("Start successful!");
        running = true;
    }

    @Override
    public boolean running() {
        return running;
    }

    @Override
    public boolean shutdown() {
        TwitchAccount account = new TwitchAccount(oAuth2Credential.getUserId(), oAuth2Credential.getRefreshToken(), "primary");
        TwitchAccount.repo.save(account);
        twitchClient.close();
        logger.debug("Shutdown successful!");
        running = false;
        return true;
    }

    private TwitchAccount injectCred() {
        return new TwitchAccount(
                //Deine Daten zur ersten Initialising einfügen, und die Config Checks deaktivieren, oder die Returns in
                //dieser checkConfiguration() auskommentieren
                "",
                "",
                "primary");
    }
}
