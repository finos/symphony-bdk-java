package bot;

import authentication.ISymBotAuth;
import authentication.SymBotAuth;
import authentication.SymBotRSAAuth;
import clients.SymBotClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import exceptions.SymClientException;
import model.InboundMessage;
import model.MessageStatus;
import model.OutboundMessage;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.NoContentException;

import static org.junit.Assert.assertNotNull;

public class RSAMessageCreateTest {

    private String configFilePath = "/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/sup-config.json";
    private SymConfig config;
    private ISymBotAuth botAuth;
    private SymBotClient botClient;

    @Before
    public void oneTimeSetUp() {
        SymConfigLoader configLoader = new SymConfigLoader();
        config = configLoader.loadFromFile(configFilePath);
        botAuth = new SymBotRSAAuth(config);
        botAuth.authenticate();
        botClient = SymBotClient.initBot(config, botAuth);
    }

    @Test
    public void messageCreateTest() {

        OutboundMessage message = new OutboundMessage();
        message.setMessage("test");
        try {
            InboundMessage inboundMessage = botClient.getMessagesClient().sendMessage(botClient.getStreamsClient().getUserIMStreamId(botClient.getUsersClient().getUserFromEmail("manuela.caicedo@example.com",true).getId()),message);
            assertNotNull(inboundMessage.getMessage());
            assertNotNull(inboundMessage.getMessageId());
            MessageStatus messageStatus = botClient.getMessagesClient().getMessageStatus(inboundMessage.getMessageId());
            assertNotNull(messageStatus);
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
    }
}
