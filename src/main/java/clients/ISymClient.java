package clients;

import authentication.ISymAuth;
import clients.symphony.api.*;
import configuration.SymConfig;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;

public interface ISymClient {


    public SymConfig getConfig();

    public ISymAuth getSymAuth();

    public MessagesClient getMessagesClient() ;

    public PresenceClient getPresenceClient();

    public StreamsClient getStreamsClient();

    public UsersClient getUsersClient();

    public ConnectionsClient getConnectionsClient();

    public Client getPodClient();

    public Client getAgentClient();

    public void setPodClient(Client podClient);

    public void setAgentClient(Client agentClient);
}
