package clients;

import authentication.ISymAuth;
import clients.symphony.api.*;
import configuration.SymConfig;
import javax.ws.rs.client.Client;
import org.glassfish.jersey.client.ClientConfig;
import utils.HttpClientBuilderHelper;

public final class SymOBOClient implements ISymClient {
    private static SymOBOClient oboClient;
    private SymConfig config;
    private ISymAuth symAuth;
    private MessagesClient messagesClient;
    private PresenceClient presenceClient;
    private StreamsClient streamsClient;
    private UsersClient usersClient;
    private ConnectionsClient connectionsClient;
    private SignalsClient signalsClient;
    private Client podClient;
    private Client agentClient;

    public static SymOBOClient initOBOClient(SymConfig config, ISymAuth auth) {
        if (oboClient == null) {
            oboClient = new SymOBOClient(config, auth);
            return oboClient;
        }
        return oboClient;
    }

    private SymOBOClient(SymConfig config,
                         ISymAuth symAuth,
                         ClientConfig podClientConfig,
                         ClientConfig agentClientConfig) {
        this.config = config;
        this.symAuth = symAuth;
        this.podClient = HttpClientBuilderHelper
                .getHttpClientBuilderWithTruststore(config)
                .withConfig(podClientConfig).build();
        this.agentClient = HttpClientBuilderHelper
                .getHttpClientBuilderWithTruststore(config)
                .withConfig(agentClientConfig).build();
    }

    public SymOBOClient(SymConfig config, ISymAuth symAuth) {
        this.config = config;
        this.symAuth = symAuth;

        ClientConfig agentConfig = HttpClientBuilderHelper.getAgentClientConfig(config);
        ClientConfig podConfig = HttpClientBuilderHelper.getPodClientConfig(config);

        this.agentClient = (agentConfig == null) ?
            HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config).build() :
            HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config).withConfig(agentConfig).build();

        this.podClient = (podConfig == null) ?
            HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config).build() :
            HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config).withConfig(podConfig).build();
    }

    @Override
    public SymConfig getConfig() {
        return config;
    }

    @Override
    public ISymAuth getSymAuth() {
        return symAuth;
    }

    @Override
    public MessagesClient getMessagesClient() {
        if (messagesClient == null) {
            messagesClient = new MessagesClient(this);
        }
        return messagesClient;
    }

    @Override
    public PresenceClient getPresenceClient() {
        if (presenceClient == null) {
            presenceClient = new PresenceClient(this);
        }
        return presenceClient;
    }

    @Override
    public StreamsClient getStreamsClient() {
        if (streamsClient == null) {
            streamsClient = new StreamsClient(this);
        }
        return streamsClient;
    }

    @Override
    public UsersClient getUsersClient() {
        if (usersClient == null) {
            usersClient = new UsersClient(this);
        }
        return usersClient;
    }

    @Override
    public ConnectionsClient getConnectionsClient() {
        if (connectionsClient == null) {
            connectionsClient = new ConnectionsClient(this);
        }
        return connectionsClient;
    }

    @Override
    public SignalsClient getSignalsClient() {
        if (signalsClient == null) {
            signalsClient = new SignalsClient(this);
        }
        return signalsClient;
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
