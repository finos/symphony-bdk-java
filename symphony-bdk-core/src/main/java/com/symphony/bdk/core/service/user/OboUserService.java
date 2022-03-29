package com.symphony.bdk.core.service.user;

import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.core.service.pagination.model.StreamPaginationAttribute;
import com.symphony.bdk.gen.api.model.UserSearchQuery;
import com.symphony.bdk.gen.api.model.UserV2;

import org.apiguardian.api.API;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Service interface exposing OBO-enabled endpoints to manage users.
 */
@API(status = API.Status.STABLE)
public interface OboUserService {

  /**
   * {@link UserService#listUsersByIds(List, Boolean, Boolean)}
   *
   * @param uidList List of user ids
   * @param local   If true then a local DB search will be performed and only local pod users will be
   *                returned. If absent or false then a directory search will be performed and users
   *                from other pods who are visible to the calling user will also be returned.
   * @param active  If not set all user status will be returned,
   *                if true all active users will be returned,
   *                if false all inactive users will be returned
   * @return Users found by user ids
   * @see <a href="https://developers.symphony.com/restapi/reference/users-lookup-v3">Users Lookup V3</a>
   */
  List<UserV2> listUsersByIds(@Nonnull List<Long> uidList, @Nullable Boolean local, @Nullable Boolean active);

  /**
   * {@link UserService#listUsersByIds(List)}
   *
   * @param uidList List of user ids
   * @return Users found by user ids
   * @see <a href="https://developers.symphony.com/restapi/reference/users-lookup-v3">Users Lookup V3</a>
   */
  List<UserV2> listUsersByIds(@Nonnull List<Long> uidList);

  /**
   * {@link UserService#listUsersByEmails(List, Boolean, Boolean)}
   *
   * @param emailList List of emails
   * @param local     If true then a local DB search will be performed and only local pod users will be
   *                  returned. If absent or false then a directory search will be performed and users
   *                  from other pods who are visible to the calling user will also be returned.
   * @param active    If not set all user status will be returned,
   *                  if true all active users will be returned,
   *                  if false all inactive users will be returned
   * @return Users found by emails.
   * @see <a href="https://developers.symphony.com/restapi/reference/users-lookup-v3">Users Lookup V3</a>
   */
  List<UserV2> listUsersByEmails(@Nonnull List<String> emailList, @Nullable Boolean local, @Nullable Boolean active);

  /**
   * {@link UserService#listUsersByEmails(List)}
   *
   * @param emailList List of emails
   * @return Users found by emails
   * @see <a href="https://developers.symphony.com/restapi/reference/users-lookup-v3">Users Lookup V3</a>
   */
  List<UserV2> listUsersByEmails(@Nonnull List<String> emailList);

  /**
   * {@link UserService#listUsersByUsernames(List, Boolean)}
   *
   * @param usernameList List of usernames
   * @param active       If not set all user status will be returned,
   *                     if true all active users will be returned,
   *                     if false all inactive users will be returned
   * @return Users found by usernames
   * @see <a href="https://developers.symphony.com/restapi/reference/users-lookup-v3">Users Lookup V3</a>
   */
  List<UserV2> listUsersByUsernames(@Nonnull List<String> usernameList, @Nullable Boolean active);

  /**
   * {@link UserService#listUsersByUsernames(List)}
   *
   * @param usernameList List of usernames
   * @return Users found by usernames
   * @see <a href="https://developers.symphony.com/restapi/reference#users-lookup-v3">Users Lookup V3</a>
   */
  List<UserV2> listUsersByUsernames(@Nonnull List<String> usernameList);

  /**
   * {@link UserService#searchUsers(UserSearchQuery, Boolean)}
   *
   * @param query Searching query containing complicated information like title, location, company...
   * @param local If true then a local DB search will be performed and only local pod users will be
   *              returned. If absent or false then a directory search will be performed and users
   *              from other pods who are visible to the calling user will also be returned.
   * @return List of users found by query
   * @see <a href="https://developers.symphony.com/restapi/reference#search-users">Search Users</a>
   */
  List<UserV2> searchUsers(@Nonnull UserSearchQuery query, @Nullable Boolean local);

  /**
   * {@link UserService#searchUsers(UserSearchQuery, Boolean, PaginationAttribute)}
   *
   * @param query      Searching query containing complicated information like title, location, company...
   * @param local      If true then a local DB search will be performed and only local pod users will be
   *                   returned. If absent or false then a directory search will be performed and users
   *                   from other pods who are visible to the calling user will also be returned.
   * @param pagination The skip and limit for pagination.
   * @return List of users found by query
   * @see <a href="https://developers.symphony.com/restapi/reference#search-users">Search Users</a>
   */
  List<UserV2> searchUsers(@Nonnull UserSearchQuery query, @Nullable Boolean local,
      @Nonnull PaginationAttribute pagination);

  /**
   * {@link UserService#searchAllUsers(UserSearchQuery, Boolean)}
   *
   * @param query Searching query containing complicated information like title, location, company...
   * @param local If true then a local DB search will be performed and only local pod users will be
   *              returned. If absent or false then a directory search will be performed and users
   *              from other pods who are visible to the calling user will also be returned.
   * @return a {@link Stream} of users found by query
   * @see <a href="https://developers.symphony.com/restapi/reference#search-users">Search Users</a>
   */
  @API(status = API.Status.EXPERIMENTAL)
  Stream<UserV2> searchAllUsers(@Nonnull UserSearchQuery query, @Nullable Boolean local);

  /**
   * {@link UserService#searchAllUsers(UserSearchQuery, Boolean, StreamPaginationAttribute)}
   *
   * @param query      Searching query containing complicated information like title, location, company...
   * @param local      If true then a local DB search will be performed and only local pod users will be
   *                   returned. If absent or false then a directory search will be performed and users
   *                   from other pods who are visible to the calling user will also be returned.
   * @param pagination The chunkSize and totalSize for pagination.
   * @return a {@link Stream} of users found by query
   * @see <a href="https://developers.symphony.com/restapi/reference#search-users">Search Users</a>
   */
  @API(status = API.Status.EXPERIMENTAL)
  Stream<UserV2> searchAllUsers(@Nonnull UserSearchQuery query, @Nullable Boolean local,
      @Nonnull StreamPaginationAttribute pagination);

  /**
   * Make a list of users to start following a specific user.
   * {@link UserService#followUser(List, Long)}
   *
   * @param followerIds List of ids of the followers.
   * @param userId      The id of the user to be followed.
   * @see <a href="https://developers.symphony.com/restapi/v20.9/reference#follow-user">Follow User</a>
   */
  void followUser(@Nonnull List<Long> followerIds, @Nonnull Long userId);

  /**
   * Make a list of users to stop following a specific user.
   * {@link UserService#unfollowUser(List, Long)}
   *
   * @param followerIds List of the ids of the followers.
   * @param userId      The id of the user to be unfollowed.
   * @see <a href="https://developers.symphony.com/restapi/v20.9/reference#unfollow-user">Unfollow User</a>
   */
  void unfollowUser(@Nonnull List<Long> followerIds, @Nonnull Long userId);
}
