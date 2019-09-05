package clients.symphony.api;

import clients.ISymClient;
import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.CommonConstants;
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
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import model.*;
import model.events.AdminStreamInfoList;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AdminClient extends APIClient {
    private final Logger logger = LoggerFactory.getLogger(AdminClient.class);
    private ISymClient botClient;

    public AdminClient(ISymClient client) {
        botClient = client;
    }

    public InboundImportMessageList importMessages(
        OutboundImportMessageList messageList)
        throws SymClientException {
        Response response = null;

        try {
            response = botClient.getAgentClient().target(
                CommonConstants.HTTPS_PREFIX
                    + botClient.getConfig().getAgentHost()
                    + ":" + botClient.getConfig().getAgentPort())
                .path(AgentConstants.MESSAGEIMPORT)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken", botClient.getSymAuth()
                    .getSessionToken())
                .header("keyManagerToken", botClient.getSymAuth().getKmToken())
                .post(Entity.entity(messageList, MediaType.APPLICATION_JSON));
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return importMessages(messageList);
                }
                return null;
            } else {
                return response.readEntity(InboundImportMessageList.class);
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public SuppressionResult suppressMessage(String id)
        throws SymClientException {
        Response response = null;

        try {
            response = botClient.getPodClient().target(
                CommonConstants.HTTPS_PREFIX
                    + botClient.getConfig().getPodHost()
                    + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.MESSAGESUPPRESS.replace("{id}", id))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .post(null);
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return suppressMessage(id);
                }
                return null;
            } else {
                return response.readEntity(SuppressionResult.class);
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public AdminStreamInfoList listEnterpriseStreams(AdminStreamFilter filter,
                                                     int skip, int limit)
        throws SymClientException {
        AdminStreamInfoList result = null;
        WebTarget builder
            = botClient.getPodClient().target(
            CommonConstants.HTTPS_PREFIX
                + botClient.getConfig().getPodHost()
                + ":" + botClient.getConfig().getPodPort())
            .path(PodConstants.ENTERPRISESTREAMS);

        if (skip > 0) {
            builder = builder.queryParam("skip", skip);
        }
        if (limit > 0) {
            builder = builder.queryParam("limit", limit);
        }
        Response response = null;

        try {
            response = builder.request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .post(Entity.entity(filter, MediaType.APPLICATION_JSON));

            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return listEnterpriseStreams(filter, skip, limit);
                }
                return null;
            }
            return response.readEntity(AdminStreamInfoList.class);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public String createIM(List<Long> userIdList) throws SymClientException {
        Response response = null;

        try {
            response = botClient.getPodClient().target(
                CommonConstants.HTTPS_PREFIX
                    + botClient.getConfig().getPodHost()
                    + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.ADMINCREATEIM)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .post(Entity.entity(userIdList, MediaType.APPLICATION_JSON));

            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return createIM(userIdList);
                }
                return null;
            }
            return response.readEntity(StringId.class).getId();
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public AdminUserInfo getUser(Long uid) throws NoContentException,
        SymClientException {
        AdminUserInfo info;
        Response response = null;

        try {
            response = botClient.getPodClient().target(CommonConstants.HTTPS_PREFIX
                + botClient.getConfig().getPodHost()
                + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETUSERADMIN.replace("{uid}",
                    Long.toString(uid)))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .get();
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUser(uid);
                }
                return null;
            } else if (response.getStatus() == CommonConstants.NO_CONTENT) {
                throw new NoContentException("No user found.");
            } else {
                info = response.readEntity(AdminUserInfo.class);
            }
            return info;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public List<AdminUserInfo> listUsers(int skip, int limit) throws SymClientException {
        AdminUserInfoList result = null;
        WebTarget builder
            = botClient.getPodClient()
            .target(CommonConstants.HTTPS_PREFIX
                + botClient.getConfig().getPodHost() + ":"
                + botClient.getConfig().getPodPort())
            .path(PodConstants.LISTUSERSADMIN);

        if (skip > 0) {
            builder = builder.queryParam("skip", skip);
        }
        if (limit > 0) {
            builder = builder.queryParam("limit", limit);
        }
        Response response = null;

        try {
            response = builder.request(MediaType.APPLICATION_JSON)
                .header("sessionToken", botClient.getSymAuth().getSessionToken())
                .get();

            if (response.getStatus() == 200) {
                result = response.readEntity(AdminUserInfoList.class);
            }
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return listUsers(skip, limit);
                }
                return null;
            }
            return result;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public AdminUserInfo createUser(AdminNewUser newUser)
        throws SymClientException {
        Response response = null;

        try {
            response = botClient.getPodClient().target(
                CommonConstants.HTTPS_PREFIX
                    + botClient.getConfig().getPodHost()
                    + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.ADMINCREATEUSER)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .post(Entity.entity(newUser, MediaType.APPLICATION_JSON));

            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return createUser(newUser);
                }
                return null;
            }
            return response.readEntity(AdminUserInfo.class);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public AdminUserInfo updateUser(Long userId,
                                    AdminUserAttributes userAttributes)
        throws SymClientException {
        Response response = null;

        try {
            response = botClient.getPodClient().target(
                CommonConstants.HTTPS_PREFIX
                    + botClient.getConfig().getPodHost()
                    + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.ADMINUPDATEUSER.replace("{uid}",
                    Long.toString(userId)))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .post(Entity.entity(userAttributes,
                    MediaType.APPLICATION_JSON));

            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return updateUser(userId, userAttributes);
                }
                return null;
            }
            return response.readEntity(AdminUserInfo.class);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public List<Avatar> getAvatar(Long uid)
        throws SymClientException {
        AvatarList avatar = null;
        Response response = null;

        try {
            response = botClient.getPodClient().target(
                CommonConstants.HTTPS_PREFIX
                    + botClient.getConfig().getPodHost()
                    + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETAVATARADMIN.replace("{uid}",
                    Long.toString(uid)))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .get();
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getAvatar(uid);
                }
                return null;
            } else if (response.getStatus() == CommonConstants.NO_CONTENT) {
                throw new SymClientException("No user found for userid:= " + uid);
            } else {
                avatar = response.readEntity(AvatarList.class);
            }
            return avatar;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void updateAvatar(Long userId, String filePath)
        throws IOException, SymClientException {
        File f = new File(filePath);
        FileInputStream fis = new FileInputStream(f);
        byte[] byteArray = new byte[(int) f.length()];
        fis.read(byteArray);
        String imageString = Base64.encodeBase64String(byteArray);
        Map<String, String> input = new HashMap<>();
        input.put("image", imageString);
        Response response = null;

        try {
            response = botClient.getPodClient().target(
                CommonConstants.HTTPS_PREFIX
                    + botClient.getConfig().getPodHost()
                    + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.ADMINUPDATEAVATAR
                    .replace("{uid}", Long.toString(userId)))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .post(Entity.entity(input, MediaType.APPLICATION_JSON));

            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    updateAvatar(userId, filePath);
                }
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public String getUserStatus(Long uid) throws SymClientException {
        String statusString = null;
        Response response = null;

        try {
            response = botClient.getPodClient().target(
                CommonConstants.HTTPS_PREFIX
                    + botClient.getConfig().getPodHost()
                    + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETUSERSTATUSADMIN.replace("{uid}",
                    Long.toString(uid)))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .get();
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserStatus(uid);
                }
                return null;
            } else if (response.getStatus() == CommonConstants.NO_CONTENT) {
                throw new SymClientException("No user found for userid:= " + uid);
            } else {
                Status status = response.readEntity(Status.class);
                statusString = status.getStatus();
            }

            return statusString;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void updateUserStatus(Long uid, String status)
        throws SymClientException {
        Status statusObj = new Status();
        statusObj.setStatus(status);
        Response response = null;

        try {
            response = botClient.getPodClient().target(
                CommonConstants.HTTPS_PREFIX
                    + botClient.getConfig().getPodHost()
                    + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.UPDATEUSERSTATUSADMIN.replace("{uid}",
                    Long.toString(uid)))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .post(Entity.entity(statusObj, MediaType.APPLICATION_JSON));
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    updateUserStatus(uid, status);
                }
            } else if (response.getStatus() == CommonConstants.NO_CONTENT) {
                throw new SymClientException("No user found for userid:= " + uid);
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public List<String> listPodFeatures() throws SymClientException {
        Response response = null;

        try {
            response = botClient.getPodClient().target(
                CommonConstants.HTTPS_PREFIX
                    + botClient.getConfig().getPodHost()
                    + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.PODFEATURESADMIN)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .get();
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return listPodFeatures();
                }
                return null;
            }
            return response.readEntity(new GenericType<List<String>>() {
            });
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public List<FeatureEntitlement> getUserFeatures(Long uid)
        throws SymClientException {
        List<FeatureEntitlement> featureEntitlements;
        Response response = null;

        try {
            response = botClient.getPodClient().target(
                CommonConstants.HTTPS_PREFIX
                    + botClient.getConfig().getPodHost()
                    + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETUSERFEATURESADMIN
                    .replace("{uid}", Long.toString(uid)))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .get();
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserFeatures(uid);
                }
                return null;
            }
            featureEntitlements = response
                .readEntity(FeatureEntitlementList.class);
            return featureEntitlements;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void updateUserFeatures(Long uid,
                                   List<FeatureEntitlement> entitlements)
        throws SymClientException {
        Response response = null;

        try {
            response = botClient.getPodClient().target(
                CommonConstants.HTTPS_PREFIX
                    + botClient.getConfig().getPodHost()
                    + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.UPDATEUSERFEATURESADMIN
                    .replace("{uid}", Long.toString(uid)))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .post(Entity.entity(entitlements, MediaType.APPLICATION_JSON));
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    updateUserFeatures(uid, entitlements);
                }
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public List<ApplicationEntitlement> getUserApplicationEntitlements(Long uid)
        throws SymClientException {
        List<ApplicationEntitlement> entitlements;
        Response response = null;

        try {
            response = botClient.getPodClient().target(
                CommonConstants.HTTPS_PREFIX
                    + botClient.getConfig().getPodHost()
                    + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETUSERAPPLICATIONSADMIN.replace("{uid}",
                    Long.toString(uid)))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .get();
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserApplicationEntitlements(uid);
                }
                return null;
            }
            entitlements = response.readEntity(ApplicationEntitlementList.class);
            return entitlements;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public List<ApplicationEntitlement> updateUserApplicationEntitlements(
        Long uid, List<ApplicationEntitlement> entitlementsUpdate)
        throws SymClientException {
        List<ApplicationEntitlement> entitlements;
        Response response = null;

        try {
            response = botClient.getPodClient().target(
                CommonConstants.HTTPS_PREFIX
                    + botClient.getConfig().getPodHost()
                    + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.UPDATEUSERAPPLICATIONSADMIN.replace("{uid}",
                    Long.toString(uid)))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .post(Entity.entity(entitlementsUpdate,
                    MediaType.APPLICATION_JSON));
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return updateUserApplicationEntitlements(uid,
                        entitlementsUpdate);
                }
                return null;
            }
            entitlements = response.readEntity(ApplicationEntitlementList.class);
            return entitlements;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
