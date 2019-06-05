package authentication;

import configuration.SymConfig;
import configuration.SymConfigLoader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class SymBotRSAAuthTest {

    private SymConfig config;

    @Before
    public void initialize() {
        this.config = SymConfigLoader.load(this.getClass().getResourceAsStream("/config.json"));
    }

    @Test
    public void authenticationTest(){
        ISymAuth botAuth = new SymBotRSAAuth(config);
        botAuth.authenticate();
        assertNotNull(botAuth.getSessionToken());
        assertNotNull(botAuth.getKmToken());
    }

    @Test
    public void shouldGetRSAPrivateKeyFileSuccessfully() throws FileNotFoundException {
        final SymBotRSAAuth botAuth = new SymBotRSAAuth(config);
        final InputStream rsaPrivateKeyFile = botAuth.getRSAPrivateKeyFile(this.config);
        assertNotNull(rsaPrivateKeyFile);
    }
}
