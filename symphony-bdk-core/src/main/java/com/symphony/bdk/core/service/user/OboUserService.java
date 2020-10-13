package com.symphony.bdk.core.service.user;

import com.symphony.bdk.gen.api.model.UserSearchQuery;
import com.symphony.bdk.gen.api.model.UserV2;

import lombok.NonNull;
import org.apiguardian.api.API;

import java.util.List;

import javax.annotation.Nullable;

/**
 * Service interface exposing OBO-enabled endpoints to manage users.
 */
@API(status = API.Status.STABLE)
public interface OboUserService {

  /**
   * {@link UserService#searchUserByIds(List, Boolean)}
   *
   * @param uidList     List of user ids
   * @param local       If true then a local DB search will be performed and only local pod users will be
   *                    returned. If absent or false then a directory search will be performed and users
   *                    from other pods who are visible to the calling user will also be returned.
   * @return Users found by user ids
   * @see <a href="https://developers.symphony.com/restapi/reference#users-lookup-v3">Users Lookup V3</a>
   */
  List<UserV2> searchUserByIds(@NonNull List<Long> uidList, @NonNull Boolean local);

  /**
   * {@link UserService#searchUserByIds(List)}
   *
   * @param uidList     List of user ids
   * @return Users found by user ids
   * @see <a href="https://developers.symphony.com/restapi/reference#users-lookup-v3">Users Lookup V3</a>
   */
  List<UserV2> searchUserByIds(@NonNull List<Long> uidList);

  /**
   * {@link UserService#searchUserByEmails(List, Boolean)}
   *
   * @param emailList   List of emails
   * @param local       If true then a local DB search will be performed and only local pod users will be
   *                    returned. If absent or false then a directory search will be performed and users
   *                    from other pods who are visible to the calling user will also be returned.
   * @return Users found by emails.
   * @see <a href="https://developers.symphony.com/restapi/reference#users-lookup-v3">Users Lookup V3</a>
   */
  List<UserV2> searchUserByEmails(@NonNull List<String> emailList, @NonNull Boolean local);

  /**
   * {@link UserService#searchUserByEmails(List)}
   *
   * @param emailList   List of emails
   * @return Users found by emails
   * @see <a href="https://developers.symphony.com/restapi/reference#users-lookup-v3">Users Lookup V3</a>
   */
  List<UserV2> searchUserByEmails(@NonNull List<String> emailList);

  /**
   * {@link UserService#searchUserByUsernames(List)}
   *
   * @param usernameList List of usernames
   * @return Users found by usernames
   * @see <a href="https://developers.symphony.com/restapi/reference#users-lookup-v3">Users Lookup V3</a>
   */
  List<UserV2> searchUserByUsernames(@NonNull List<String> usernameList);

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
  List<UserV2> searchUserBySearchQuery(@NonNull UserSearchQuery query, @Nullable Boolean local);
}
