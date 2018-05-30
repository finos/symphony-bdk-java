package bot;

import authentication.SymBotAuth;
import clients.SymBotClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import static org.junit.Assert.*;

public class SymBotClientTest {

    String configFilePath = "/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/config.json";
    SymConfigLoader configLoader = new SymConfigLoader();
    SymConfig config = configLoader.loadFromFile(configFilePath);
    SymBotAuth botAuth = new SymBotAuth(config);


    @Test
    public void botClientInitTest(){
        botAuth.authenticate();
        SymBotClient botClient = SymBotClient.initBot(config,botAuth);
        assertNotNull(botClient);
        assertNotNull(botClient.getSymAuth());
        SymBotClient botClient1 = SymBotClient.initBot(config,botAuth);
        assertEquals(botClient,botClient1);
        botClient.clearBotClient();
        SymBotClient botClient2 = SymBotClient.initBot(config,botAuth);
        assertNotEquals(botClient,botClient2);
    }

    public void proxyClientInitTest(){
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ClientProperties.PROXY_URI, config.getProxyURL());
        SymBotAuth botAuth = new SymBotAuth(config,clientConfig,clientConfig);
        SymBotClient botClient = SymBotClient.initBot(config,botAuth,clientConfig,clientConfig);
    }
}
