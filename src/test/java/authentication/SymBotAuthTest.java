package authentication;

import configuration.SymConfig;
import configuration.SymConfigLoader;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SymBotAuthTest {

    String configFilePath = "/Users/manuela.caicedo/Documents/Bots/symphonyapiclient/src/main/resources/config.json";
    SymConfigLoader configLoader = new SymConfigLoader();
    SymConfig config = configLoader.loadFromFile(configFilePath);

    @Test
    public void authenticationTest(){
        SymBotAuth botAuth = new SymBotAuth(config);
        botAuth.authenticate();
        assertNotNull(botAuth.getSessionToken());
        assertNotNull(botAuth.getKmToken());
    }
}
