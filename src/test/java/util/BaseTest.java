package util;

import authentication.SymBotAuth;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import org.junit.BeforeClass;

import java.io.InputStream;

public class BaseTest {

    public static SymConfig config;
    public static SymBotAuth botAuth;

    @BeforeClass
    public static void setUp() {
        InputStream configFileStream = BaseTest.class.getResourceAsStream("/config.json");
        SymConfigLoader configLoader = new SymConfigLoader();
        config = configLoader.load(configFileStream);
        botAuth = new SymBotAuth(config);
    }
}
