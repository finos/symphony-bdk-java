package authentication;

import configuration.SymConfig;
import configuration.SymConfigLoader;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

public class SymBotAuthTest {

    SymConfig config;

    @Before
    public void setUp() {
        InputStream configFileStream = getClass().getResourceAsStream("/config.json");
        SymConfigLoader configLoader = new SymConfigLoader();
        config = configLoader.load(configFileStream);
    }

    @Test
    public void authenticationTest(){
        System.out.println("****** ALAN 2 ****");
        ISymAuth botAuth = new SymBotAuth(config);
        botAuth.authenticate();
        assertNotNull(botAuth.getSessionToken());
        assertNotNull(botAuth.getKmToken());
    }
}
