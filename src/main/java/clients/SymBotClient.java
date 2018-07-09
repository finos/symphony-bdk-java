package clients;

import authentication.ISymAuth;
import authentication.SymBotAuth;
import clients.symphony.api.*;
import configuration.SymConfig;
import exceptions.*;
import model.Signal;
import model.UserInfo;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import services.DatafeedEventsService;
import utils.HttpClientBuilderHelper;
import utils.SymMessageParser;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.NoContentException;

public class SymBotClient implements ISymClient {

    private static SymBotClient botClient;
    private SymConfig config;
    private ISymAuth symBotAuth;
    private DatafeedClient datafeedClient;
    private MessagesClient messagesClient;
    private PresenceClient presenceClient;
    private StreamsClient streamsClient;
    private UsersClient usersClient;
    private ConnectionsClient connectionsClient;
    private DatafeedEventsService datafeedEventsService;
    private SignalsClient signalsClient;
    private UserInfo botUserInfo;
    private Client podClient;
    private Client agentClient;
    private AdminClient adminClient;


    public static SymBotClient initBot(SymConfig config, ISymAuth symBotAuth){
        if(botClient==null){
            botClient = new SymBotClient(config, symBotAuth);
            return botClient;
        }
        return botClient;
    }

    private SymBotClient(SymConfig config, ISymAuth symBotAuth, ClientConfig podClientConfig, ClientConfig agentClientConfig) {
        this.config = config;
        this.symBotAuth = symBotAuth;
        this.podClient = ClientBuilder.newClient(podClientConfig);
        this.agentClient = ClientBuilder.newClient(agentClientConfig);
        try {
            botUserInfo = this.getUsersClient().getUserFromEmail(config.getBotEmailAddress(), true);
        } catch (NoContentException e) {
            e.printStackTrace();
        }  catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    private SymBotClient(SymConfig config, ISymAuth symBotAuth){
        this.config = config;
        this.symBotAuth = symBotAuth;

        if(config.getProxyURL()==null){
            this.podClient = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config).build();
            this.agentClient = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config).build();
        }
        else {
            this.agentClient = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config).build();
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.property(ClientProperties.PROXY_URI, config.getProxyURL());
            if(config.getProxyUsername()!=null && config.getProxyPassword()!=null) {
                clientConfig.property(ClientProperties.PROXY_USERNAME,config.getProxyUsername());
                clientConfig.property(ClientProperties.PROXY_PASSWORD,config.getProxyPassword());
            }
            this.podClient =  HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config).withConfig(clientConfig).build();
        }
        try {
            botUserInfo = this.getUsersClient().getUserFromEmail(config.getBotEmailAddress(), true);
        } catch (NoContentException e) {
            e.printStackTrace();
        }  catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    public static SymBotClient initBot(SymConfig config, SymBotAuth botAuth, ClientConfig podClientConfig, ClientConfig agentClientConfig) {
        if(botClient==null){
            botClient = new SymBotClient(config, botAuth,podClientConfig,agentClientConfig);
            return botClient;
        }
        return botClient;
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

    public ISymAuth getSymAuth() {
        return symBotAuth;
    }

    public MessagesClient getMessagesClient() {
        if (messagesClient == null){
            SymMessageParser.createInstance(this);
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
            SymMessageParser.createInstance(this);
            datafeedEventsService = new DatafeedEventsService(this);
        }
        return datafeedEventsService;
    }

    public SignalsClient getSignalsClient() {
        if (signalsClient == null){
            signalsClient = new SignalsClient(this);
        }
        return signalsClient;
    }

    public AdminClient getAdminClient() {
        if (adminClient == null){
            adminClient = new AdminClient(this);
        }
        return adminClient;
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
