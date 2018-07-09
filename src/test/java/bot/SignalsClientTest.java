package bot;

import authentication.*;
import clients.SymBotClient;
import clients.SymOBOClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import exceptions.SymClientException;
import model.InboundMessage;
import model.OutboundMessage;
import model.Signal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.NoContentException;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class SignalsClientTest {

//    String configFilePath = "/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/sup-config.json";
//    SymConfigLoader configLoader = new SymConfigLoader();
//    SymConfig config = configLoader.loadFromFile(configFilePath);
//    private SymOBOClient client=null;
//    private SymBotClient botClient;

//    @Before
//    public void authenticationTest(){
//        SymOBOAuth oboAuth = new SymOBOAuth(config);
//        oboAuth.sessionAppAuthenticate();
//        SymOBOUserAuth auth = oboAuth.getUserAuth("bot.user1");
//        client = SymOBOClient.initOBOClient(config,auth);
//        ISymAuth botAuth = new SymBotRSAAuth(config);
//        botAuth.authenticate();
//        botClient = SymBotClient.initBot(config,botAuth);
//
//    }

    private String configFilePath = "/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/config.json";
    private SymConfig config;
    private SymBotAuth botAuth;
    private SymBotClient botClient;

    @Before
    public void oneTimeSetUp() {
        SymConfigLoader configLoader = new SymConfigLoader();
        config = configLoader.loadFromFile(configFilePath);
        botAuth = new SymBotAuth(config);
        botAuth.authenticate();
        botClient = SymBotClient.initBot(config, botAuth);
    }

    @Test
    public void listSignalsTest(){
        try {
            List<Signal> signals = botClient.getSignalsClient().listSignals(0,0);
            Assert.assertTrue(!signals.isEmpty());
        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSignalTest(){
        try {
            Signal signal = botClient.getSignalsClient().getSignal("59764cabe4b027c686f2d050");
            Assert.assertEquals("59764cabe4b027c686f2d050", signal.getId());
        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createUpdateDeleteSignalTest(){
        Signal signal = new Signal();
        signal.setName("api test signal java");
        signal.setVisibleOnProfile(true);
        signal.setQuery("HASHTAG:apitest");
        Signal createdSignal= null;
        try {
            createdSignal = botClient.getSignalsClient().createSignal(signal);
            Assert.assertEquals(createdSignal.getName(), signal.getName());
        } catch (SymClientException e) {
            e.printStackTrace();
        }
        createdSignal.setName("api signal test java");
        try {
            Signal updatedSignal = botClient.getSignalsClient().updateSignal(createdSignal);
            Assert.assertEquals(updatedSignal.getName(), createdSignal.getName());
        } catch (SymClientException e) {
            e.printStackTrace();
        }
        try {
            botClient.getSignalsClient().deleteSignal(createdSignal.getId());
        } catch (SymClientException e) {
            e.printStackTrace();
        }


    }
}
