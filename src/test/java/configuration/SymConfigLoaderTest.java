package configuration;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SymConfigLoaderTest {

    String configFilePath = "/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/config.json";
    SymConfigLoader configLoader = new SymConfigLoader();

    @Test
    public void loadConfig() {
        SymConfig config = configLoader.loadFromFile(configFilePath);
        assertNotNull(config);
        assertEquals("preview.symphony.com", config.getSessionAuthHost());
    }
}
