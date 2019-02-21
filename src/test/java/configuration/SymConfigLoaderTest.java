package configuration;

import org.junit.Test;
import util.BaseTest;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SymConfigLoaderTest {

    @Test
    public void loadConfig() {
        InputStream configFileStream = BaseTest.class.getResourceAsStream("/config.json");
        SymConfigLoader configLoader = new SymConfigLoader();
        SymConfig config = configLoader.load(configFileStream);
        assertNotNull(config);
        assertEquals("develop-api.symphony.com", config.getSessionAuthHost());
    }
}
