package clients.symphony.api;

import clients.ISymClient;
import clients.symphony.api.constants.PodConstants;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.*;
import model.UserPresence;
import model.UserPresenceCategory;

@RequiredArgsConstructor
public class PresenceClient extends APIClient {

    private final ISymClient botClient;

    /**
     * Returns the online status of the specified user.
     *
     * @param userId The unique ID of the user.
     * @param local true: Perform a local query and set the presence to OFFLINE for users who are not local to the calling
     *              user’s pod.
     * @return a user presence status
     * @since 1.47
     */
    public UserPresence getUserPresence(@Nonnull Long userId, boolean local) throws SymClientException {
        final Invocation.Builder builder = this.getTarget()
            .path(PodConstants.GETUSERPRESENCE.replace("{uid}", Long.toString(userId)))
            .queryParam("local", local)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("Cache-Control", "no-cache");

        try (final Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserPresence(userId, local);
                }
                return null;
            }
            return response.readEntity(UserPresence.class);
        }
    }

    /**
     * Returns the online status of the calling user.
     *
     * @return the calling user presence status
     * @since 1.47
     */
    public UserPresence getUserPresence() throws SymClientException {
        final Invocation.Builder builder = this.getTarget()
            .path(PodConstants.GET_OR_SET_PRESENCE)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("Cache-Control", "no-cache");

        try (final Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserPresence();
                }
                return null;
            }
            return response.readEntity(UserPresence.class);
        }
    }

    /**
     * Returns the presence of all users in a pod
     * @param lastUserId Last user ID retrieved, used for paging. If provided, results skip users with IDs less than this
     *                   parameter.
     * @param limit Maximum number of records to return. The maximum supported value is 5000.
     * @return a list of user presence
     * @since 1.47
     */
    public List<UserPresence> getAllPresence(@Nullable Long lastUserId, @Nullable Integer limit) throws SymClientException {
        WebTarget target = this.getTarget();

        if (lastUserId != null) {
            target = target.queryParam("lastUserId", lastUserId);
        }

        if (limit != null) {

            if (limit > 5000) {
                throw new IllegalArgumentException("The maximum supported value for the limit is 5000.");
            }

            target = target.queryParam("limit", limit);
        }

        final Invocation.Builder builder = target.path(PodConstants.GET_ALL_PRESENCE)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("Cache-Control", "no-cache");

        try (final Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getAllPresence(lastUserId, limit);
                }
                return null;
            }
            return response.readEntity(new GenericType<List<UserPresence>>() {});
        }
    }

    /**
     * Sets the online status of the calling user.
     *
     * @param category he new presence state for the user. Possible values are AVAILABLE, BUSY, AWAY, ON_THE_PHONE, BE_RIGHT_BACK,
     *                 IN_A_MEETING, OUT_OF_OFFICE, OFF_WORK.
     * @return the online status of the calling user.
     * @since 1.47
     */
    public UserPresence setPresence(@Nonnull UserPresenceCategory category) throws SymClientException {
        final Invocation.Builder builder = this.getTarget()
            .path(PodConstants.GET_OR_SET_PRESENCE)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("Cache-Control", "no-cache");

        try (Response response = builder.post(Entity.entity(new Category(category), MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return setPresence(category);
                }
                return null;
            }
            return response.readEntity(UserPresence.class);
        }
    }

    /**
     * @deprecated Use {@link PresenceClient#setPresence(UserPresenceCategory)} instead.
     */
    @Deprecated
    public UserPresence setPresence(@Nonnull String status) throws SymClientException {
        return this.setPresence(UserPresenceCategory.valueOf(status));
    }

    /**
     * To get the presence state of external users, you must first register interest in those users using this endpoint.
     *
     * @param userIds A list of users whom you want to query the presence of, specified by their userId.
     * @since 1.44
     */
    public void registerInterestExtUser(@Nonnull List<Long> userIds) throws SymClientException {
        final Invocation.Builder builder = this.getTarget()
            .path(PodConstants.REGISTERPRESENCEINTEREST)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("Cache-Control", "no-cache");

        try (Response response = builder.post(Entity.entity(userIds, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    registerInterestExtUser(userIds);
                }
            }
        }
    }

    /**
     * Creates a new stream capturing online status changes ("presence feed") for the company (pod) and returns
     * the ID of the new feed. The feed will return the presence of users whose presence status has changed since it
     * was last read.
     *
     * @return a presence feed id
     * @since 1.48
     */
    public String createPresenceFeed() throws SymClientException {
        final Invocation.Builder builder = this.getTarget()
            .path(PodConstants.PRESENCE_FEED_CREATE)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("Cache-Control", "no-cache");

        try (Response response = builder.post(Entity.entity("{}", MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return this.createPresenceFeed();
                }
                return null;
            }

            return response.readEntity(PresenceFeedCreationResponse.class).getId();
        }
    }

    /**
     * Reads the specified presence feed that was created using the Create Presence feed endpoint.
     * The feed returned includes the user presence statuses that have changed since they were last read.
     *
     * @param feedId Presence feed ID, obtained from Create Presence Feed.
     * @return a list of user presence
     * @since 1.48
     */
    public List<UserPresence> readPresenceFeed(@Nonnull String feedId) throws SymClientException {
        final Invocation.Builder builder = this.getTarget()
            .path(PodConstants.PRESENCE_FEED_READ.replace("{feedId}", feedId))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("Cache-Control", "no-cache");

        try (Response response = builder.post(Entity.entity("{}", MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return this.readPresenceFeed(feedId);
                }
                return null;
            }

            return response.readEntity(new GenericType<List<UserPresence>>() {});
        }
    }

    /**
     * Deletes a presence status feed. This endpoint returns the ID of the deleted feed if the deletion is successful.
     *
     * @param feedId Presence feed ID, obtained from Create Presence Feed.
     * @since 1.48
     */
    public void deletePresenceFeed(@Nonnull String feedId) throws SymClientException {
        final Invocation.Builder builder = this.getTarget()
            .path(PodConstants.PRESENCE_FEED_DELETE.replace("{feedId}", feedId))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("Cache-Control", "no-cache");

        try (final Response response = builder.delete()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    this.deletePresenceFeed(feedId);
                }
            }
        }
    }

    /**
     * Sets the presence state of a another user.
     *
     * @param userId User ID as a decimal integer. Use this field to set the presence of a different user than the calling user.
     * @param category Presence state to set.
     * @return the user presence
     * @since 1.49
     */
    public UserPresence setOtherUserPresence(@Nonnull Long userId, @Nonnull UserPresenceCategory category)
        throws SymClientException {
        final Invocation.Builder builder = this.getTarget()
            .path(PodConstants.SET_OTHER_USER_PRESENCE)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", this.botClient.getSymAuth().getSessionToken())
            .header("Cache-Control", "no-cache");

        OtherUserPresenceRequest otherUserPresenceRequest = new OtherUserPresenceRequest(userId, category);
        try (final Response response = builder.post(Entity.entity(otherUserPresenceRequest, MediaType.APPLICATION_JSON_TYPE))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, this.botClient);
                } catch (UnauthorizedException ex) {
                    return this.setOtherUserPresence(userId, category);
                }
                return null;
            }
            return response.readEntity(UserPresence.class);
        }
    }

    private WebTarget getTarget() {
        return this.botClient.getPodClient().target(this.botClient.getConfig().getPodUrl());
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class OtherUserPresenceRequest {
        private Long userId;
        private UserPresenceCategory category;
    }

    @Getter
    @Setter
    private static class PresenceFeedCreationResponse {
        private String id;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Category {
        private UserPresenceCategory category;
    }
}
