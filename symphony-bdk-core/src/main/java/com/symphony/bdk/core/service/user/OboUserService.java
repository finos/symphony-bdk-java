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
   * {@link UserService#searchUserByIds(List, Boolean, Boolean)}
   *
   * @param uidList     List of user ids
   * @param local       If true then a local DB search will be performed and only local pod users will be
   *                    returned. If absent or false then a directory search will be performed and users
   *                    from other pods who are visible to the calling user will also be returned.
   * @param active      If not set all user status will be returned,
   *                    if true all active users will be returned,
   *                    if false all inactive users will be returned
   * @return Users found by user ids
   * @see <a href="https://developers.symphony.com/restapi/reference#users-lookup-v3">Users Lookup V3</a>
   */
  List<UserV2> searchUserByIds(@Nonnull List<Long> uidList, Boolean local, @Nullable Boolean active);

  /**
   * {@link UserService#searchUserByIds(List)}
   *
   * @param uidList     List of user ids
   * @return Users found by user ids
   * @see <a href="https://developers.symphony.com/restapi/reference#users-lookup-v3">Users Lookup V3</a>
   */
  List<UserV2> searchUserByIds(@Nonnull List<Long> uidList);

  /**
   * {@link UserService#searchUserByEmails(List, Boolean, Boolean)}
   *
   * @param emailList   List of emails
   * @param local       If true then a local DB search will be performed and only local pod users will be
   *                    returned. If absent or false then a directory search will be performed and users
   *                    from other pods who are visible to the calling user will also be returned.
   * @param active      If not set all user status will be returned,
   *                    if true all active users will be returned,
   *                    if false all inactive users will be returned
   * @return Users found by emails.
   * @see <a href="https://developers.symphony.com/restapi/reference#users-lookup-v3">Users Lookup V3</a>
   */
  List<UserV2> searchUserByEmails(@Nonnull List<String> emailList, Boolean local, @Nullable Boolean active);

  /**
   * {@link UserService#searchUserByEmails(List)}
   *
   * @param emailList   List of emails
   * @return Users found by emails
   * @see <a href="https://developers.symphony.com/restapi/reference#users-lookup-v3">Users Lookup V3</a>
   */
  List<UserV2> searchUserByEmails(@Nonnull List<String> emailList);

  /**
   * {@link UserService#searchUserByUsernames(List, Boolean)}
   *
   * @param usernameList List of usernames
   * @param active      If not set all user status will be returned,
   *                    if true all active users will be returned,
   *                    if false all inactive users will be returned
   * @return Users found by usernames
   * @see <a href="https://developers.symphony.com/restapi/reference#users-lookup-v3">Users Lookup V3</a>
   */
  List<UserV2> searchUserByUsernames(@Nonnull List<String> usernameList, @Nullable Boolean active);

  /**
   * {@link UserService#searchUserByUsernames(List)}
   *
   * @param usernameList List of usernames
   * @return Users found by usernames
   * @see <a href="https://developers.symphony.com/restapi/reference#users-lookup-v3">Users Lookup V3</a>
   */
  List<UserV2> searchUserByUsernames(@Nonnull List<String> usernameList);

  /**
   * {@link UserService#searchUserBySearchQuery(UserSearchQuery, Boolean)}
   *
   * @param query       Searching query containing complicated information like title, location, company...
   * @param local       If true then a local DB search will be performed and only local pod users will be
   *                    returned. If absent or false then a directory search will be performed and users
   *                    from other pods who are visible to the calling user will also be returned.
   * @return List of users found by query
   * @see <a href="https://developers.symphony.com/restapi/reference#search-users">Search Users</a>
   */
  List<UserV2> searchUserBySearchQuery(@Nonnull UserSearchQuery query, @Nullable Boolean local);

  /**
   * {@link UserService#searchUserBySearchQuery(UserSearchQuery, Boolean, PaginationAttribute)}
   *
   * @param query       Searching query containing complicated information like title, location, company...
   * @param local       If true then a local DB search will be performed and only local pod users will be
   *                    returned. If absent or false then a directory search will be performed and users
   *                    from other pods who are visible to the calling user will also be returned.
   * @param pagination  The skip and limit for pagination.
   * @return List of users found by query
   * @see <a href="https://developers.symphony.com/restapi/reference#search-users">Search Users</a>
   */
  List<UserV2> searchUserBySearchQuery(@Nonnull UserSearchQuery query, @Nullable Boolean local, @Nonnull
      PaginationAttribute pagination);

  /**
   * {@link UserService#searchAllUsersBySearchQuery(UserSearchQuery, Boolean)}
   *
   * @param query       Searching query containing complicated information like title, location, company...
   * @param local       If true then a local DB search will be performed and only local pod users will be
   *                    returned. If absent or false then a directory search will be performed and users
   *                    from other pods who are visible to the calling user will also be returned.
   * @return a {@link Stream} of users found by query
   * @see <a href="https://developers.symphony.com/restapi/reference#search-users">Search Users</a>
   */
  Stream<UserV2> searchAllUsersBySearchQuery(@Nonnull UserSearchQuery query, @Nullable Boolean local);

  /**
   * {@link UserService#searchAllUsersBySearchQuery(UserSearchQuery, Boolean, StreamPaginationAttribute)}
   *
   * @param query       Searching query containing complicated information like title, location, company...
   * @param local       If true then a local DB search will be performed and only local pod users will be
   *                    returned. If absent or false then a directory search will be performed and users
   *                    from other pods who are visible to the calling user will also be returned.
   * @param pagination  The chunkSize and totalSize for pagination with default value equals 50.
   * @return a {@link Stream} of users found by query
   * @see <a href="https://developers.symphony.com/restapi/reference#search-users">Search Users</a>
   */
  Stream<UserV2> searchAllUsersBySearchQuery(@Nonnull UserSearchQuery query, @Nullable Boolean local, @Nonnull
      StreamPaginationAttribute pagination);
}
