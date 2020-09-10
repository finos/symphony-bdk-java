package com.symphony.bdk.core.service.user;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.api.invoker.ApiRuntimeException;
import com.symphony.bdk.core.auth.AuthSession;
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

  protected OboUserService(UserApi userApi, UsersApi usersApi) {
    this.userApi = userApi;
    this.usersApi = usersApi;
  }

  public List<UserV2> searchUserByIds(@NonNull AuthSession oboSession, @NonNull List<Long> uidList, @NonNull Boolean local) {
    try {
      String uids = uidList.stream().map(String::valueOf).collect(Collectors.joining(","));
      V2UserList v2UserList = usersApi.v3UsersGet(oboSession.getSessionToken(), uids, null, null, local);
      return v2UserList.getUsers();
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public List<UserV2> searchUserByIds(@NonNull AuthSession oboSession, @NonNull List<Long> uidList) {
    try {
      String uids = uidList.stream().map(String::valueOf).collect(Collectors.joining(","));
      V2UserList v2UserList = usersApi.v3UsersGet(oboSession.getSessionToken(), uids, null, null, false);
      return v2UserList.getUsers();
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public List<UserV2> searchUserByEmails(@NonNull AuthSession oboSession, @NonNull List<String> emailList, @NonNull Boolean local) {
    try {
      String emails = String.join(",", emailList);
      V2UserList v2UserList = usersApi.v3UsersGet(oboSession.getSessionToken(), null, emails, null, local);
      return v2UserList.getUsers();
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public List<UserV2> searchUserByEmails(@NonNull AuthSession oboSession, @NonNull List<String> emailList) {
    try {
      String emails = String.join(",", emailList);
      V2UserList v2UserList = usersApi.v3UsersGet(oboSession.getSessionToken(), null, emails, null, false);
      return v2UserList.getUsers();
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public List<UserV2> searchUserByUsernames(@NonNull AuthSession oboSession, @NonNull List<String> usernameList) {
    try {
      String usernames = String.join(",", usernameList);
      V2UserList v2UserList = usersApi.v3UsersGet(oboSession.getSessionToken(), null, null, usernames, true);
      return v2UserList.getUsers();
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public List<UserV2> searchUserBySearchQuery(@NonNull AuthSession oboSession, @NonNull UserSearchQuery query, @Nullable Boolean local) {
    try {
      UserSearchResults results = usersApi.v1UserSearchPost(oboSession.getSessionToken(), query, null, null, local);
      return results.getUsers();
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }
}
