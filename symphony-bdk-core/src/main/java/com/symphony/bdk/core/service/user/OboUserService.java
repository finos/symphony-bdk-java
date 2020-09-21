package com.symphony.bdk.core.service.user;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.util.function.RetryWithRecovery;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.UserApi;
import com.symphony.bdk.gen.api.UsersApi;
import com.symphony.bdk.gen.api.model.UserSearchQuery;
import com.symphony.bdk.gen.api.model.UserSearchResults;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V2UserList;

import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

class OboUserService {

  protected final UserApi userApi;
  protected final UsersApi usersApi;
  protected final BdkRetryConfig retryConfig;

  protected OboUserService(UserApi userApi, UsersApi usersApi, BdkRetryConfig retryConfig) {
    this.userApi = userApi;
    this.usersApi = usersApi;
    this.retryConfig = retryConfig;
  }

  /**
   * {@link UserService#searchUserByIds(List, Boolean)}
   *
   * @param authSession Bot session or Obo Session
   * @param uidList     List of user ids
   * @param local       If true then a local DB search will be performed and only local pod users will be
   *                    returned. If absent or false then a directory search will be performed and users
   *                    from other pods who are visible to the calling user will also be returned.
   * @return Users found by user ids
   * @see <a href="https://developers.symphony.com/restapi/reference#users-lookup-v3">Users Lookup V3</a>
   */
  public List<UserV2> searchUserByIds(@NonNull AuthSession authSession, @NonNull List<Long> uidList,
      @NonNull Boolean local) {
    String uids = uidList.stream().map(String::valueOf).collect(Collectors.joining(","));
    V2UserList v2UserList = executeAndRetry("searchUserByIds",
        () -> usersApi.v3UsersGet(authSession.getSessionToken(), uids, null, null, local), authSession);
    return v2UserList.getUsers();
  }

  /**
   * {@link UserService#searchUserByIds(List)}
   *
   * @param authSession Bot Session or Obo Session
   * @param uidList     List of user ids
   * @return Users found by user ids
   * @see <a href="https://developers.symphony.com/restapi/reference#users-lookup-v3">Users Lookup V3</a>
   */
  public List<UserV2> searchUserByIds(@NonNull AuthSession authSession, @NonNull List<Long> uidList) {
    String uids = uidList.stream().map(String::valueOf).collect(Collectors.joining(","));
    V2UserList v2UserList = executeAndRetry("searchUserByIds",
        () -> usersApi.v3UsersGet(authSession.getSessionToken(), uids, null, null, false), authSession);
    return v2UserList.getUsers();
  }

  /**
   * {@link UserService#searchUserByEmails(List, Boolean)}
   *
   * @param authSession Bot Session or Obo Session
   * @param emailList   List of emails
   * @param local       If true then a local DB search will be performed and only local pod users will be
   *                    returned. If absent or false then a directory search will be performed and users
   *                    from other pods who are visible to the calling user will also be returned.
   * @return Users found by emails.
   * @see <a href="https://developers.symphony.com/restapi/reference#users-lookup-v3">Users Lookup V3</a>
   */
  public List<UserV2> searchUserByEmails(@NonNull AuthSession authSession, @NonNull List<String> emailList,
      @NonNull Boolean local) {
    String emails = String.join(",", emailList);
    V2UserList v2UserList = executeAndRetry("searchUserByEmails",
        () -> usersApi.v3UsersGet(authSession.getSessionToken(), null, emails, null, local), authSession);
    return v2UserList.getUsers();
  }

  /**
   * {@link UserService#searchUserByEmails(List)}
   *
   * @param authSession Bot Session or Obo Session
   * @param emailList   List of emails
   * @return Users found by emails
   * @see <a href="https://developers.symphony.com/restapi/reference#users-lookup-v3">Users Lookup V3</a>
   */
  public List<UserV2> searchUserByEmails(@NonNull AuthSession authSession, @NonNull List<String> emailList) {
    String emails = String.join(",", emailList);
    V2UserList v2UserList = executeAndRetry("searchUserByEmails",
        () -> usersApi.v3UsersGet(authSession.getSessionToken(), null, emails, null, false), authSession);
    return v2UserList.getUsers();
  }

  /**
   * {@link UserService#searchUserByUsernames(List)}
   *
   * @param authSession  Bot Session or Obo Session
   * @param usernameList List of usernames
   * @return Users found by usernames
   * @see <a href="https://developers.symphony.com/restapi/reference#users-lookup-v3">Users Lookup V3</a>
   */
  public List<UserV2> searchUserByUsernames(@NonNull AuthSession authSession, @NonNull List<String> usernameList) {
    String usernames = String.join(",", usernameList);
    V2UserList v2UserList = executeAndRetry("searchUserByUsernames",
        () -> usersApi.v3UsersGet(authSession.getSessionToken(), null, null, usernames, true), authSession);
    return v2UserList.getUsers();
  }

  /**
   * {@link UserService#searchUserBySearchQuery(UserSearchQuery, Boolean)}
   *
   * @param authSession Bot Session or  Obo Session
   * @param query       Searching query containing complicated information like title, location, company...
   * @param local       If true then a local DB search will be performed and only local pod users will be
   *                    returned. If absent or false then a directory search will be performed and users
   *                    from other pods who are visible to the calling user will also be returned.
   * @return List of users found by query
   * @see <a href="https://developers.symphony.com/restapi/reference#search-users">Search Users</a>
   */
  public List<UserV2> searchUserBySearchQuery(@NonNull AuthSession authSession, @NonNull UserSearchQuery query,
      @Nullable Boolean local) {
    UserSearchResults results = executeAndRetry("searchUserBySearchQuery",
        () -> usersApi.v1UserSearchPost(authSession.getSessionToken(), query, null, null, local), authSession);
    return results.getUsers();
  }

  protected <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier, AuthSession authSession) {
    return RetryWithRecovery.executeAndRetry(name, supplier, retryConfig, authSession);
  }
}
