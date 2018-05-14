package bot;

import authentication.SymBotAuth;
import clients.SymBotClient;
import clients.symphony.api.DatafeedClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import exceptions.SymClientException;
import model.InboundMessage;
import model.MessageStatus;
import model.OutboundMessage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.NoContentException;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class MessagesTest {

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

    @Test
    public void getMessageTest() {
        try {
            List<InboundMessage> inboundMessages = botClient.getMessagesClient().getMessagesFromStream(botClient.getStreamsClient().getUserIMStreamId(botClient.getUsersClient().getUserFromEmail("mike.scannell@symphony.com",true).getId()),0,0,100);
            assertNotNull(!inboundMessages.isEmpty());
            assertNotNull(inboundMessages.get(0).getMessageText());
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
    }


}
