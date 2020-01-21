package clients.symphony.api;

import clients.ISymClient;
import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.PodConstants;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import model.*;
import model.events.AdminStreamInfoList;
import org.apache.commons.codec.binary.Base64;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

public final class AdminClient extends APIClient {
    private ISymClient botClient;

    public AdminClient(ISymClient client) {
        botClient = client;
    }

    public InboundImportMessageList importMessages(OutboundImportMessageList messageList)
        throws SymClientException {
        Invocation.Builder builder = botClient.getAgentClient()
            .target(botClient.getConfig().getAgentUrl())
            .path(AgentConstants.MESSAGEIMPORT)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("keyManagerToken", botClient.getSymAuth().getKmToken());

        try (Response response = builder.post(Entity.entity(messageList, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return importMessages(messageList);
                }
                return null;
            } else {
                return response.readEntity(InboundImportMessageList.class);
            }
        }
    }

    public SuppressionResult suppressMessage(String id) throws SymClientException {
        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.MESSAGESUPPRESS.replace("{id}", id))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(null)) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return suppressMessage(id);
                }
                return null;
            } else {
                return response.readEntity(SuppressionResult.class);
            }
        }
    }

    public AdminStreamInfoList listEnterpriseStreams(AdminStreamFilter filter, int skip, int limit)
        throws SymClientException {
        WebTarget webTarget = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.ENTERPRISESTREAMS);

        if (skip > 0) {
            webTarget = webTarget.queryParam("skip", skip);
        }
        if (limit > 0) {
            webTarget = webTarget.queryParam("limit", limit);
        }

        Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(filter, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return listEnterpriseStreams(filter, skip, limit);
                }
                return null;
            }
            return response.readEntity(AdminStreamInfoList.class);
        }
    }

    public String createIM(List<Long> userIdList) throws SymClientException {
        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.ADMINCREATEIM)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(userIdList, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return createIM(userIdList);
                }
                return null;
            }
            return response.readEntity(StringId.class).getId();
        }
    }

    public AdminUserInfo getUser(Long uid) throws NoContentException, SymClientException {
        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.GETUSERADMIN.replace("{uid}", Long.toString(uid)))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUser(uid);
                }
                return null;
            } else if (response.getStatus() == NO_CONTENT.getStatusCode()) {
                throw new NoContentException("No user found.");
            } else {
                return response.readEntity(AdminUserInfo.class);
            }
        }
    }

    public List<AdminUserInfo> listUsers(int skip, int limit) throws SymClientException {
        WebTarget webTarget = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.LISTUSERSADMIN);

        if (skip > 0) {
            webTarget = webTarget.queryParam("skip", skip);
        }
        if (limit > 0) {
            webTarget = webTarget.queryParam("limit", limit);
        }

        Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatus() == 200) {
                return response.readEntity(AdminUserInfoList.class);
            }
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return listUsers(skip, limit);
                }
            }
        }
        return null;
    }

    public AdminUserInfo createUser(AdminNewUser newUser) throws SymClientException {
        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.ADMINCREATEUSER)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(newUser, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return createUser(newUser);
                }
                return null;
            }
            return response.readEntity(AdminUserInfo.class);
        }
    }

    public AdminUserInfo updateUser(Long userId, AdminUserAttributes userAttributes)
        throws SymClientException {
        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.ADMINUPDATEUSER.replace("{uid}", Long.toString(userId)))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(userAttributes, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return updateUser(userId, userAttributes);
                }
                return null;
            }
            return response.readEntity(AdminUserInfo.class);
        }
    }

    public List<Avatar> getAvatar(Long uid) throws SymClientException {
        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.GETAVATARADMIN.replace("{uid}", Long.toString(uid)))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getAvatar(uid);
                }
                return null;
            } else if (response.getStatus() == NO_CONTENT.getStatusCode()) {
                throw new SymClientException("No user found for userid:= " + uid);
            } else {
                return response.readEntity(AvatarList.class);
            }
        }
    }

    public void updateAvatar(Long userId, String filePath)
        throws IOException, SymClientException {
        Map<String, String> input = new HashMap<>();
        File f = new File(filePath);

        try (FileInputStream fis = new FileInputStream(f)) {
            byte[] byteArray = new byte[(int) f.length()];
            fis.read(byteArray);
            String imageString = Base64.encodeBase64String(byteArray);
            input.put("image", imageString);
        }

        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.ADMINUPDATEAVATAR.replace("{uid}", Long.toString(userId)))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(input, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    updateAvatar(userId, filePath);
                }
            }
        }
    }

    public String getUserStatus(Long uid) throws SymClientException {
        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.GETUSERSTATUSADMIN.replace("{uid}", Long.toString(uid)))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserStatus(uid);
                }
                return null;
            } else if (response.getStatus() == NO_CONTENT.getStatusCode()) {
                throw new SymClientException("No user found for userid:= " + uid);
            } else {
                Status status = response.readEntity(Status.class);
                return status.getStatus();
            }
        }
    }

    public void updateUserStatus(Long uid, UserStatus status) throws SymClientException {
        updateUserStatus(uid, status.toString());
    }

    public void updateUserStatus(Long uid, String status) throws SymClientException {
        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.UPDATEUSERSTATUSADMIN.replace("{uid}", Long.toString(uid)))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        Entity entity = Entity.entity(new Status(status), MediaType.APPLICATION_JSON);

        try (Response response = builder.post(entity)) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    updateUserStatus(uid, status);
                }
            } else if (response.getStatus() == NO_CONTENT.getStatusCode()) {
                throw new SymClientException("No user found for userId: " + uid);
            }
        }
    }

    public List<String> listPodFeatures() throws SymClientException {
        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.PODFEATURESADMIN)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return listPodFeatures();
                }
                return null;
            }
            return response.readEntity(new GenericType<List<String>>() {});
        }
    }

    public List<FeatureEntitlement> getUserFeatures(Long uid) throws SymClientException {
        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.GETUSERFEATURESADMIN.replace("{uid}", Long.toString(uid)))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserFeatures(uid);
                }
                return null;
            }
            return response.readEntity(FeatureEntitlementList.class);
        }
    }

    public void updateUserFeatures(Long uid, List<FeatureEntitlement> entitlements)
        throws SymClientException {
        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.UPDATEUSERFEATURESADMIN.replace("{uid}", Long.toString(uid)))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(entitlements, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    updateUserFeatures(uid, entitlements);
                }
            }
        }
    }

    public List<ApplicationEntitlement> getUserApplicationEntitlements(Long uid)
        throws SymClientException {
        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.GETUSERAPPLICATIONSADMIN.replace("{uid}", Long.toString(uid)))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserApplicationEntitlements(uid);
                }
                return null;
            }
            return response.readEntity(ApplicationEntitlementList.class);
        }
    }

    public List<ApplicationEntitlement> updateUserApplicationEntitlements(
        Long uid, List<ApplicationEntitlement> entitlementsUpdate)
        throws SymClientException {
        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.UPDATEUSERAPPLICATIONSADMIN.replace("{uid}", Long.toString(uid)))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(entitlementsUpdate, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return updateUserApplicationEntitlements(uid,
                        entitlementsUpdate);
                }
                return null;
            }
            return response.readEntity(ApplicationEntitlementList.class);
        }
    }
}
