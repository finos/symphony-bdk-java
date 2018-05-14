package clients;

import authentication.ISymBotAuth;
import authentication.SymBotAuth;
import clients.symphony.api.*;
import configuration.SymConfig;
import exceptions.*;
import model.UserInfo;
import services.DatafeedEventsService;

import javax.ws.rs.core.NoContentException;

public class SymBotClient {

    private static SymBotClient botClient;
    private SymConfig config;
    private ISymBotAuth symBotAuth;
    private DatafeedClient datafeedClient;
    private MessagesClient messagesClient;
    private PresenceClient presenceClient;
    private StreamsClient streamsClient;
    private UsersClient usersClient;
    private ConnectionsClient connectionsClient;
    private DatafeedEventsService datafeedEventsService;
    private UserInfo botUserInfo;


    public static SymBotClient initBot(SymConfig config, SymBotAuth symBotAuth){
        if(botClient==null){
            botClient = new SymBotClient(config, symBotAuth);
            return botClient;
        }
        return botClient;
    }

    private SymBotClient(SymConfig config, ISymBotAuth symBotAuth){
        this.config = config;
        this.symBotAuth = symBotAuth;
        try {
            botUserInfo = this.getUsersClient().getUserFromEmail(config.getBotEmailAddress(), true);
        } catch (NoContentException e) {
            e.printStackTrace();
        }  catch (SymClientException e) {
            e.printStackTrace();
        }
    }


    public UserInfo getBotUserInfo() {
        return botUserInfo;
    }

    public DatafeedClient getDatafeedClient() {
        if (datafeedClient == null){
            datafeedClient= new DatafeedClient(this);
        }
        return datafeedClient;
    }

    public SymConfig getConfig() {
        return config;
    }

    public ISymBotAuth getSymBotAuth() {
        return symBotAuth;
    }

    public MessagesClient getMessagesClient() {
        if (messagesClient == null){
            messagesClient = new MessagesClient(this);
        }
        return messagesClient;
    }

    public PresenceClient getPresenceClient() {
        if (presenceClient == null){
            presenceClient = new PresenceClient(this);
        }
        return presenceClient;
    }

    public StreamsClient getStreamsClient() {
        if (streamsClient ==null){
            streamsClient = new StreamsClient(this);
        }
        return streamsClient;
    }

    public UsersClient getUsersClient() {
        if (usersClient == null){
            usersClient = new UsersClient(this);
        }
        return usersClient;
    }

    public ConnectionsClient getConnectionsClient() {
        if (connectionsClient == null){
            connectionsClient = new ConnectionsClient(this);
        }
        return connectionsClient;
    }

    public DatafeedEventsService getDatafeedEventsService() {
        if (datafeedEventsService == null){
            datafeedEventsService = new DatafeedEventsService(this);
        }
        return datafeedEventsService;
    }

    public void clearBotClient(){
        botClient = null;
    }
}
