package clients;

import authentication.ISymAuth;
import clients.symphony.api.*;
import configuration.SymConfig;
import javax.ws.rs.client.Client;

public interface ISymClient {
    SymConfig getConfig();
    ISymAuth getSymAuth();
    MessagesClient getMessagesClient();
    PresenceClient getPresenceClient();
    StreamsClient getStreamsClient();
    UsersClient getUsersClient();
    SignalsClient getSignalsClient();
    ConnectionsClient getConnectionsClient();
    Client getPodClient();
    Client getAgentClient();
    void setPodClient(Client podClient);
    void setAgentClient(Client agentClient);
}
