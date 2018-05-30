package clients;

import authentication.ISymAuth;
import authentication.SymOBOUserAuth;
import clients.symphony.api.*;
import configuration.SymConfig;
import exceptions.SymClientException;
import model.UserInfo;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import services.DatafeedEventsService;
import utils.HttpClientBuilderHelper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.NoContentException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class SymOBOClient implements ISymClient {
    private static SymOBOClient oboClient;
    private SymConfig config;
    private SymOBOUserAuth symAuth;
    private MessagesClient messagesClient;
    private PresenceClient presenceClient;
    private StreamsClient streamsClient;
    private UsersClient usersClient;
    private ConnectionsClient connectionsClient;
    private Client podClient;
    private Client agentClient;

    public static SymOBOClient initOBOClient(SymConfig config, SymOBOUserAuth auth) {
        if(oboClient==null){
            oboClient = new SymOBOClient(config, auth);
            return oboClient;
        }
        return oboClient;
    }

    private SymOBOClient(SymConfig config, SymOBOUserAuth symAuth, ClientConfig podClientConfig, ClientConfig agentClientConfig) {
        this.config = config;
        this.symAuth = symAuth;
        this.podClient = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config).withConfig(podClientConfig).build();
        this.agentClient = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config).withConfig(agentClientConfig).build();
    }

    public SymOBOClient(SymConfig config, SymOBOUserAuth symAuth) {
        this.config = config;
        this.symAuth = symAuth;



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
    }

    @Override
    public SymConfig getConfig() {
        return config;
    }

    @Override
    public ISymAuth getSymAuth() {
        return symAuth;
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

    @Override
    public Client getPodClient() {
        return podClient;
    }

    @Override
    public Client getAgentClient() {
        return agentClient;
    }

    @Override
    public void setPodClient(Client podClient) {
        this.podClient = podClient;
    }

    @Override
    public void setAgentClient(Client agentClient) {
        this.agentClient = agentClient;
    }


}
