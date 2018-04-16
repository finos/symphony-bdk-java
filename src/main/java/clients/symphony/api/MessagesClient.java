package clients.symphony.api;

import authentication.AuthEndpointConstants;
import clients.SymBotClient;
import clients.symphony.api.constants.AgentConstants;
import model.InboundMessage;
import model.OutboundMessage;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.*;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class MessagesClient extends APIClient{
    private SymBotClient botClient;

    public MessagesClient(SymBotClient client) {
        botClient = client;
    }

    public InboundMessage sendMessage(String streamId, OutboundMessage message) throws Exception {

                ClientConfig clientConfig = new ClientConfig();
                clientConfig.register(MultiPartFeature.class);
                clientConfig.register(JacksonFeature.class);

                Client httpClient =  ClientBuilder.newClient(clientConfig);
                WebTarget target = httpClient.target(AuthEndpointConstants.HTTPSPREFIX + botClient.getConfig().getAgentHost() + ":" + botClient.getConfig().getAgentPort())
                        .path(AgentConstants.CREATEMESSAGE.replace("{sid}", streamId));

                Invocation.Builder invocationBuilder = target.request().accept(new String[]{"application/json"});

                invocationBuilder = invocationBuilder.header("sessionToken",botClient.getSymBotAuth().getSessionToken());
                invocationBuilder = invocationBuilder.header("keyManagerToken", botClient.getSymBotAuth().getKmToken());

                FormDataMultiPart multiPart = new FormDataMultiPart();

                FormDataContentDisposition contentDisp = FormDataContentDisposition.name("message").build();
                multiPart.bodyPart(new FormDataBodyPart(contentDisp, message.getMessage()));
                Entity entity = Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA_TYPE);
                Response response = invocationBuilder.post(entity);


                int statusCode = response.getStatusInfo().getStatusCode();
                if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
                    return null;
                }

                if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                    handleError(response,botClient);
                    return null;
                }
                else {
                    return response.readEntity(InboundMessage.class);
                }

    }

    public List<InboundMessage> getMessagesFromStream(String streamId, int since, int offset, int limit){
        return null;
    }

}
