package clients.symphony.api;

import authentication.SymOBOUserRSAAuth;
import clients.ISymClient;
import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.CommonConstants;
import clients.symphony.api.constants.PodConstants;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import model.*;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.*;

public final class MessagesClient extends APIClient {
    private ISymClient botClient;
    private boolean isKeyManTokenRequired;

    public MessagesClient(ISymClient client) {
        botClient = client;
        isKeyManTokenRequired = !(botClient.getSymAuth() instanceof SymOBOUserRSAAuth);
    }

    private InboundMessage sendMessage(String streamId,
                                       OutboundMessage message,
                                       boolean appendTags)
        throws SymClientException {
        Client httpClient = botClient.getAgentClient();
        httpClient.register(MultiPartFeature.class);
        httpClient.register(JacksonFeature.class);


        WebTarget target = httpClient.target(
            CommonConstants.HTTPS_PREFIX + botClient.getConfig()
                .getAgentHost()
                + ":" + botClient.getConfig().getAgentPort())
            .path(AgentConstants.CREATEMESSAGE
                .replace("{sid}", streamId));

        Invocation.Builder invocationBuilder = target.request().accept("application/json");
        invocationBuilder = invocationBuilder.header("sessionToken",
            botClient.getSymAuth().getSessionToken());
        if (isKeyManTokenRequired) {
            invocationBuilder = invocationBuilder.header("keyManagerToken", botClient.getSymAuth().getKmToken());
        }
        String messageContent;
        if (appendTags) {
            messageContent = String.format("<messageML>%s</messageML>", message.getMessage());
        } else {
            messageContent = message.getMessage();
        }

        FormDataMultiPart multiPart = new FormDataMultiPart();

        FormDataContentDisposition contentDispMessage =
            FormDataContentDisposition.name("message").build();
        multiPart.bodyPart(new FormDataBodyPart(contentDispMessage,
            messageContent));
        if (message.getData() != null) {
            FormDataContentDisposition contentDispData =
                FormDataContentDisposition.name("data").build();
            multiPart.bodyPart(new FormDataBodyPart(contentDispData,
                message.getData()));
        }
        if (message.getAttachment() != null
            && message.getAttachment().length > 0) {
            for (File file : message.getAttachment()) {
                FormDataContentDisposition contentDisp =
                    FormDataContentDisposition.name("attachment")
                        .fileName(file.getName()).size(file.length())
                        .build();
                multiPart.bodyPart(new FormDataBodyPart(contentDisp,
                    file, MediaType.APPLICATION_OCTET_STREAM_TYPE));
            }
        }
        Entity entity = Entity.entity(multiPart,
            MultiPartMediaTypes.createFormData());

        Response response = null;

        try {
            response = invocationBuilder.post(entity);

            if (response.getStatus()
                == Response.Status.NO_CONTENT.getStatusCode()) {
                return null;
            }

            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return sendMessage(streamId, message, appendTags);
                }
                return null;
            } else {
                return response.readEntity(InboundMessage.class);
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public InboundMessage forwardMessage(String streamId,
                                         InboundMessage message)
        throws SymClientException {
        OutboundMessage outboundMessage = new OutboundMessage();
        outboundMessage.setMessage(message.getMessage());
        outboundMessage.setData(message.getData());
        //outboundMessage.setAttachment(message.getAttachments());

        return sendMessage(streamId, outboundMessage, false);

    }

    public InboundMessage sendMessage(String streamId, OutboundMessage message)
        throws SymClientException {
        return sendMessage(streamId, message, true);
    }

    public InboundMessage sendTaggedMessage(String streamId, OutboundMessage message)
        throws SymClientException {
        return sendMessage(streamId, message, false);
    }

    public List<InboundMessage> getMessagesFromStream(String streamId,
                                                      int since, int skip,
                                                      int limit)
        throws SymClientException {
        List<InboundMessage> result = null;
        WebTarget builder
            = botClient.getAgentClient().target(
            CommonConstants.HTTPS_PREFIX
                + botClient.getConfig().getAgentHost()
                + ":" + botClient.getConfig().getAgentPort())
            .path(AgentConstants.GETMESSAGES.replace("{sid}", streamId))
            .queryParam("since", since);


        if (skip > 0) {
            builder = builder.queryParam("skip", skip);
        }
        if (limit > 0) {
            builder = builder.queryParam("limit", limit);
        }

        Response response = null;

        try {
            Invocation.Builder subBuilder = builder.request(MediaType.APPLICATION_JSON)
                .header("sessionToken", botClient.getSymAuth().getSessionToken());
            if (isKeyManTokenRequired) {
                subBuilder = subBuilder.header("keyManagerToken", botClient.getSymAuth().getKmToken());
            }
            response = subBuilder.get();

            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getMessagesFromStream(streamId, since, skip, limit);
                }
                return null;
            } else if (response.getStatus() == 204) {
                result = new ArrayList<>();
            } else {
                result = response.readEntity(InboundMessageList.class);
            }

            return result;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public byte[] getAttachment(String streamId, String attachmentId,
                                String messageId) throws SymClientException {

        Response response = null;

        try {
            Invocation.Builder subBuilder = botClient.getAgentClient().target(CommonConstants.HTTPS_PREFIX
                + botClient.getConfig().getAgentHost()
                + ":" + botClient.getConfig().getAgentPort())
                .path(AgentConstants.GETATTACHMENT
                    .replace("{sid}", streamId))
                .queryParam("fileId", attachmentId)
                .queryParam("messageId", messageId)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken());
            if (isKeyManTokenRequired) {
                subBuilder = subBuilder.header("keyManagerToken", botClient.getSymAuth().getKmToken());
            }
            response = subBuilder.get();

            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getAttachment(streamId, attachmentId, messageId);
                }
                return null;
            } else {
                return Base64.getDecoder()
                    .decode(response.readEntity(String.class));
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public List<FileAttachment> getMessageAttachments(InboundMessage message)
        throws SymClientException {
        List<FileAttachment> result = new ArrayList<>();
        if (message.getAttachments() != null) {
            for (Attachment attachment : message.getAttachments()) {
                FileAttachment fileAttachment = new FileAttachment();
                fileAttachment.setFileName(attachment.getName());
                fileAttachment.setSize(attachment.getSize());
                fileAttachment.setFileContent(getAttachment(message.getStream().getStreamId(),
                    attachment.getId(), message.getMessageId()));
                result.add(fileAttachment);
            }
        }
        return result;
    }

    public MessageStatus getMessageStatus(String messageId)
        throws SymClientException {
        Response response = null;

        try {
            response = botClient.getPodClient().target(CommonConstants.HTTPS_PREFIX
                + botClient.getConfig().getPodHost()
                + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETMESSAGESTATUS
                    .replace("{mid}", messageId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .get();
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getMessageStatus(messageId);
                }
                return null;
            }
            return response.readEntity(MessageStatus.class);
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }

    public InboundMessageList messageSearch(Map<String, String> query,
                                            int skip, int limit,
                                            boolean orderAscending)
        throws SymClientException, NoContentException {

        InboundMessageList result = null;
        WebTarget builder
            = botClient.getAgentClient().target(CommonConstants.HTTPS_PREFIX
            + botClient.getConfig().getAgentHost()
            + ":" + botClient.getConfig().getAgentPort())
            .path(AgentConstants.SEARCHMESSAGES);


        if (skip > 0) {
            builder = builder.queryParam("skip", skip);
        }
        if (limit > 0) {
            builder = builder.queryParam("limit", limit);
        }
        //default is DESC
        if (orderAscending) {
            builder = builder.queryParam("sortDir", "ASC");
        }

        Response response = null;

        try {
            Invocation.Builder subBuilder = builder.request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken());
            if (isKeyManTokenRequired) {
                subBuilder = subBuilder.header("keyManagerToken", botClient.getSymAuth().getKmToken());
            }
            response = subBuilder.post(Entity.entity(query, MediaType.APPLICATION_JSON));

            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return messageSearch(query, skip, limit, orderAscending);
                }
                return null;
            } else if (response.getStatus() == CommonConstants.NO_CONTENT) {
                throw new NoContentException("No messages found");
            } else {
                result = response.readEntity(InboundMessageList.class);
            }

            return result;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }


    //Included in release 1.52
//    public List<String> getSupportedAttachmentTypes(){
//        Response response
//                = botClient.getAgentClient().target(CommonConstants.HTTPS_PREFIX
// + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
//                .path(PodConstants.GETATTACHMENTTYPES)
//                .request(MediaType.APPLICATION_JSON)
//                .header("sessionToken",botClient.getSymAuth().getSessionToken())
//                .get();
//        if (response.getStatusInfo().getFamily()
// != Response.Status.Family.SUCCESSFUL) {
//            try {
//                handleError(response, botClient);
//            } catch (UnauthorizedException ex){
//                return getSupportedAttachmentTypes();
//            } catch (SymClientException e) {
//                e.printStackTrace();
//            }
//            return null;
//        } else {
//            return response.readEntity(StringList.class);
//        }
//    }

    public InboundShare shareContent(String streamId,
                                     OutboundShare shareContent)
        throws SymClientException {
        Map<String, Object> map = new HashMap<>();
        map.put("content", shareContent);

        Response response = null;

        try {
            Invocation.Builder subBuilder = botClient.getAgentClient().target(CommonConstants.HTTPS_PREFIX
                + botClient.getConfig().getAgentHost()
                + ":" + botClient.getConfig().getAgentPort())
                .path(AgentConstants.SHARE.replace("{sid}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken", botClient.getSymAuth().getSessionToken());
            if (isKeyManTokenRequired) {
                subBuilder = subBuilder.header("keyManagerToken", botClient.getSymAuth().getKmToken());
            }
            response = subBuilder.post(Entity.entity(map, MediaType.APPLICATION_JSON));
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return shareContent(streamId, shareContent);
                }
                return null;
            }
            return response.readEntity(InboundShare.class);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

}
