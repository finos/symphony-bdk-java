package clients;

import authentication.ISymAuth;
import authentication.SymOBOUserAuth;
import clients.symphony.api.*;
import configuration.SymConfig;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import utils.HttpClientBuilderHelper;
import javax.ws.rs.client.Client;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public final class SymOBOClient implements ISymClient {
    private static SymOBOClient oboClient;
    private SymConfig config;
    private SymOBOUserAuth symAuth;
    private MessagesClient messagesClient;
    private PresenceClient presenceClient;
    private StreamsClient streamsClient;
    private UsersClient usersClient;
    private ConnectionsClient connectionsClient;
    private SignalsClient signalsClient;
    private Client podClient;
    private Client agentClient;

    public static SymOBOClient initOBOClient(SymConfig config,
                                             SymOBOUserAuth auth) {
        if (oboClient == null) {
            oboClient = new SymOBOClient(config, auth);
            return oboClient;
        }
        return oboClient;
    }

    private SymOBOClient(SymConfig config,
                         SymOBOUserAuth symAuth,
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

    public SymOBOClient(SymConfig config, SymOBOUserAuth symAuth) {
        this.config = config;
        this.symAuth = symAuth;

        String proxyURL = !isEmpty(config.getPodProxyURL()) ?
            config.getPodProxyURL() : config.getProxyURL();
        String proxyUser = !isEmpty(config.getPodProxyUsername()) ?
            config.getPodProxyUsername() : config.getProxyUsername();
        String proxyPass = !isEmpty(config.getPodProxyPassword()) ?
            config.getPodProxyPassword() : config.getProxyPassword();

        ClientConfig proxyConfig = new ClientConfig();
        proxyConfig.connectorProvider(new ApacheConnectorProvider());
        proxyConfig.property(ClientProperties.PROXY_URI, proxyURL);
        if (!isEmpty(proxyUser) && !isEmpty(proxyPass)) {
            proxyConfig.property(ClientProperties.PROXY_USERNAME, proxyUser);
            proxyConfig.property(ClientProperties.PROXY_PASSWORD, proxyPass);
        }

        this.agentClient = isEmpty(config.getProxyURL()) ?
            HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config).build() :
            HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config).withConfig(proxyConfig).build();

        this.podClient = (isEmpty(config.getPodProxyURL()) && isEmpty(config.getProxyURL())) ?
            HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config).build() :
            HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config).withConfig(proxyConfig).build();
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
        if (messagesClient == null) {
            messagesClient = new MessagesClient(this);
        }
        return messagesClient;
    }

    public PresenceClient getPresenceClient() {
        if (presenceClient == null) {
            presenceClient = new PresenceClient(this);
        }
        return presenceClient;
    }

    public StreamsClient getStreamsClient() {
        if (streamsClient == null) {
            streamsClient = new StreamsClient(this);
        }
        return streamsClient;
    }

    public UsersClient getUsersClient() {
        if (usersClient == null) {
            usersClient = new UsersClient(this);
        }
        return usersClient;
    }

    public ConnectionsClient getConnectionsClient() {
        if (connectionsClient == null) {
            connectionsClient = new ConnectionsClient(this);
        }
        return connectionsClient;
    }

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
