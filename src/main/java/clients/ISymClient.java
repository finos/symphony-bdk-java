package clients;

import authentication.ISymAuth;
import clients.symphony.api.MessagesClient;
import clients.symphony.api.PresenceClient;
import clients.symphony.api.StreamsClient;
import clients.symphony.api.UsersClient;
import clients.symphony.api.SignalsClient;
import clients.symphony.api.ConnectionsClient;

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
