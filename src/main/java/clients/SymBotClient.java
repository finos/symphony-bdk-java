package clients;

import authentication.ISymBotAuth;
import authentication.SymBotAuth;
import clients.symphony.api.*;
import configuration.SymConfig;
import exceptions.*;
import model.UserInfo;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import services.DatafeedEventsService;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
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
    private Client podClient;
    private Client agentClient;


    public static SymBotClient initBot(SymConfig config, ISymBotAuth symBotAuth){
        if(botClient==null){
            botClient = new SymBotClient(config, symBotAuth);
            return botClient;
        }
        return botClient;
    }

    public static SymBotClient initBot(SymConfig config, ISymBotAuth symBotAuth, Client sessionAuthClient, Client podClient, Client agentClient, Client kmAuthClient) {
        if(botClient==null){
            botClient = new SymBotClient(config, symBotAuth,sessionAuthClient,podClient,agentClient,kmAuthClient);
            return botClient;
        }
        return botClient;
    }

    private SymBotClient(SymConfig config, ISymBotAuth symBotAuth, Client sessionAuthClient, Client podClient, Client agentClient, Client kmAuthClient) {
        this.config = config;
        this.symBotAuth = symBotAuth;
        this.podClient = podClient;
        this.agentClient = agentClient;
        try {
            botUserInfo = this.getUsersClient().getUserFromEmail(config.getBotEmailAddress(), true);
        } catch (NoContentException e) {
            e.printStackTrace();
        }  catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    private SymBotClient(SymConfig config, ISymBotAuth symBotAuth){
        this.config = config;
        this.symBotAuth = symBotAuth;

        if(config.getProxyURL()==null){
            Client client = ClientBuilder.newClient();
            this.podClient = client;
            this.agentClient = client;
        }
        else {
            Client client = ClientBuilder.newClient();
            this.agentClient = client;
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.property(ClientProperties.PROXY_URI, config.getProxyURL());
            if(config.getProxyUsername()!=null && config.getProxyPassword()!=null) {
                clientConfig.property(ClientProperties.PROXY_USERNAME,config.getProxyUsername());
                clientConfig.property(ClientProperties.PROXY_PASSWORD,config.getProxyPassword());
            }
            Client proxyClient =  ClientBuilder.newClient(clientConfig);
            this.podClient = proxyClient;
        }
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

    public static SymBotClient getBotClient() {
        return botClient;
    }


    public Client getPodClient() {
        return podClient;
    }

    public Client getAgentClient() {
        return agentClient;
    }

    public void setPodClient(Client podClient) {
        this.podClient = podClient;
    }

    public void setAgentClient(Client agentClient) {
        this.agentClient = agentClient;
    }

}
