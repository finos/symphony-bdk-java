package clients.symphony.api;

import clients.ISymClient;
import clients.SymBotClient;
import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.CommonConstants;
import clients.symphony.api.constants.PodConstants;
import exceptions.*;
import model.*;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.*;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class MessagesClient extends APIClient{
    private ISymClient botClient;

    public MessagesClient(ISymClient client) {
        botClient = client;

    }

    private InboundMessage sendMessage(String streamId, OutboundMessage message, boolean appendTags) throws SymClientException {
        //TODO: Add file support
                Client httpClient =  botClient.getAgentClient();
                httpClient.register(MultiPartFeature.class);
                httpClient.register(JacksonFeature.class);


                WebTarget target = httpClient.target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getAgentHost() + ":" + botClient.getConfig().getAgentPort())
                        .path(AgentConstants.CREATEMESSAGE.replace("{sid}", streamId));

                Invocation.Builder invocationBuilder = target.request().accept(new String[]{"application/json"});

                invocationBuilder = invocationBuilder.header("sessionToken",botClient.getSymAuth().getSessionToken());
                invocationBuilder = invocationBuilder.header("keyManagerToken", botClient.getSymAuth().getKmToken());

                String messageContent = null;
                if(appendTags){
                    messageContent = "<messageML>"+message.getMessage()+"</messageML>";
                }

                FormDataMultiPart multiPart = new FormDataMultiPart();

                FormDataContentDisposition contentDispMessage = FormDataContentDisposition.name("message").build();
                multiPart.bodyPart(new FormDataBodyPart(contentDispMessage, messageContent));
                if(message.getData()!=null){
                    FormDataContentDisposition contentDispData = FormDataContentDisposition.name("data").build();
                    multiPart.bodyPart(new FormDataBodyPart(contentDispData, message.getData()));
                }
                Entity entity = Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA_TYPE);
                Response response = invocationBuilder.post(entity);

                if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
                    return null;
                }

                if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                    try {
                        handleError(response, botClient);
                    } catch (UnauthorizedException ex){
                        return sendMessage(streamId,message,appendTags);
                    }
                    return null;
                }
                else {
                    return response.readEntity(InboundMessage.class);
                }

    }

    public InboundMessage forwardMessage(String streamId, InboundMessage message) throws SymClientException {
        OutboundMessage outboundMessage = new OutboundMessage();
        outboundMessage.setMessage(message.getMessage());
        outboundMessage.setData(message.getData());
        //outboundMessage.setAttachment(message.getAttachments());

        return sendMessage(streamId, outboundMessage, false);

    }

    public InboundMessage sendMessage(String streamId, OutboundMessage message) throws SymClientException {
        return sendMessage(streamId, message, true);
    }

    public List<InboundMessage> getMessagesFromStream(String streamId, int since, int skip, int limit) throws SymClientException {
        List<InboundMessage> result = null;
        WebTarget builder
                = botClient.getAgentClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getAgentHost() + ":" + botClient.getConfig().getPodPort())
                .path(AgentConstants.GETMESSAGES.replace("{sid}", streamId))
                .queryParam("since", since);


        if(skip>0){
            builder.queryParam("skip", skip);
        }
        if(limit>0){
            builder.queryParam("limit", limit);
        }
        Response response = builder.request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymAuth().getKmToken())
                .get();

        if(response.getStatus() == 204){
            result = new ArrayList<>();
        } else if (response.getStatus() == 200) {
            result = response.readEntity(InboundMessageList.class);
        }
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
                return getMessagesFromStream(streamId,since,skip,limit);
            }
            return null;
        }
        return result;
    }

    public byte[] getAttachment(String streamId, String attachmentId, String messageId) throws SymClientException {

        Response response
                = botClient.getAgentClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getAgentHost() + ":" + botClient.getConfig().getAgentHost())
                .path(AgentConstants.GETATTACHMENT.replace("{sid}", streamId))
                .queryParam("fileId", attachmentId)
                .queryParam("messageId", messageId)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymAuth().getKmToken())
                .get();
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
                return getAttachment(streamId,attachmentId,messageId);
            }
            return null;
        } else {
            return Base64.getDecoder().decode(response.readEntity(String.class));
        }
    }

    public List<FileAttachment> getMessageAttachments(InboundMessage message) throws SymClientException {
        List<FileAttachment> result = new ArrayList<>();
        for (Attachment attachment : message.getAttachments()) {
            FileAttachment fileAttachment = new FileAttachment();
            fileAttachment.setFileName(attachment.getName());
            fileAttachment.setSize(attachment.getSize());
            fileAttachment.setFileContent(getAttachment(message.getStream().getStreamId(), attachment.getId(), message.getMessageId()));
            result.add(fileAttachment);
        }
        return result;
    }

    public MessageStatus getMessageStatus(String messageId) throws SymClientException {
        Response response
                = botClient.getAgentClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETMESSAGESTATUS.replace("{mid}", messageId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .get();
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
                return getMessageStatus(messageId);
            }
            return null;
        }
        return response.readEntity(MessageStatus.class);

    }

}
