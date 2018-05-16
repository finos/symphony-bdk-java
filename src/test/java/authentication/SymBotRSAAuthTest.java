package authentication;

import configuration.SymConfig;
import configuration.SymConfigLoader;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SymBotRSAAuthTest {

    String configFilePath = "/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/dev-config.json";
    SymConfigLoader configLoader = new SymConfigLoader();
    SymConfig config = configLoader.loadFromFile(configFilePath);

    @Test
    public void authenticationTest(){
        ISymBotAuth botAuth = new SymBotRSAAuth(config);
        botAuth.authenticate();
        assertNotNull(botAuth.getSessionToken());
        assertNotNull(botAuth.getKmToken());
    }
}
