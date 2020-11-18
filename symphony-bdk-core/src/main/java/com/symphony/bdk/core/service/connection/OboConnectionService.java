package com.symphony.bdk.core.service.connection;

import com.symphony.bdk.core.service.connection.constant.ConnectionStatus;
import com.symphony.bdk.gen.api.model.UserConnection;

import org.apiguardian.api.API;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Service interface exposing OBO-enabled endpoints to manage user connection status.
 */
@API(status = API.Status.STABLE)
public interface OboConnectionService {

  /**
   * Get connection status, i.e. check if the calling user is connected to the specified user.
   * {@link ConnectionService#getConnection(Long)}
   *
   * @param userId The id of the user with whom the caller want to check.
   * @return Connection status with the specified user.
   * @see <a href="https://developers.symphony.com/restapi/reference#get-connection">Get Connection</a>
   */
  UserConnection getConnection(@Nonnull Long userId);

  /**
   * List all current connection statuses with external or specified users.
   * {@link ConnectionService#listConnections(ConnectionStatus, List)}
   *
   * @param status  Filter the connection list based on the connection status.
   *                The connection status can only be pending_incoming, pending_outgoing, accepted, rejected, or all.
   *                If you do not specify a status, all connections will be returned.
   * @param userIds List of user ids which are used to restrict the list of results.
   *                This can be used to return connections with internal users;
   *                although, by default, this endpoint does not list implicit connections with internal users.
   * @return List of connection statuses with the specified users and status.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-connections">List Connections</a>
   */
  List<UserConnection> listConnections(@Nonnull ConnectionStatus status, @Nonnull List<Long> userIds);

  /**
   * Sends a connection request to another user.
   * {@link ConnectionService#createConnection(Long)}
   *
   * @param userId The id of the user with whom the caller want to connect.
   * @return Connection status with the specified user.
   * @see <a href="https://developers.symphony.com/restapi/reference#create-connection">Create Connection</a>
   */
  UserConnection createConnection(@Nonnull Long userId);

  /**
   * Accept the connection request from a requesting user.
   * {@link ConnectionService#acceptConnection(Long)}
   *
   * @param userId The id of the user who requested to connect with the caller.
   * @return Connection status with the requesting user.
   * @see <a href="https://developers.symphony.com/restapi/reference#accepted-connection">Accept Connection</a>
   */
  UserConnection acceptConnection(@Nonnull Long userId);

  /**
   * Reject the connection request from a requesting user.
   * {@link ConnectionService#rejectConnection(Long)}
   *
   * @param userId The id of the user who requested to connect with the caller.
   * @return Connection status with the requesting user.
   * @see <a href="https://developers.symphony.com/restapi/reference#reject-connection">Reject Connection</a>
   */
  UserConnection rejectConnection(@Nonnull Long userId);

  /**
   * Removes a connection with a user.
   * {@link ConnectionService#removeConnection(Long)}
   *
   * @param userId The id of the user with whom we want to remove the connection.
   * @see <a href="https://developers.symphony.com/restapi/reference#remove-connection">Remove Connection</a>
   */
  void removeConnection(@Nonnull Long userId);
}
