package com.symphony.bdk.core.service.user;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.api.invoker.ApiRuntimeException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.gen.api.UserApi;
import com.symphony.bdk.gen.api.UsersApi;
import com.symphony.bdk.gen.api.model.Avatar;
import com.symphony.bdk.gen.api.model.AvatarUpdate;
import com.symphony.bdk.gen.api.model.DelegateAction;
import com.symphony.bdk.gen.api.model.Disclaimer;
import com.symphony.bdk.gen.api.model.Feature;
import com.symphony.bdk.gen.api.model.StringId;
import com.symphony.bdk.gen.api.model.UserDetail;
import com.symphony.bdk.gen.api.model.UserFilter;
import com.symphony.bdk.gen.api.model.UserSearchQuery;
import com.symphony.bdk.gen.api.model.UserSearchResults;
import com.symphony.bdk.gen.api.model.UserStatus;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V2UserDetail;
import com.symphony.bdk.gen.api.model.V2UserList;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 * A class for implementing the user service.
 *
 * This service is used for retrieving information about a particular user,
 * search users by ids, emails or usernames, perform some action related to
 * user like:
 * <p><ul>
 *   <li>Add or remove roles from an user</li>
 *   <li>Get or update avatar of an user</li>
 *   <li>Get, assign or unassign disclaimer to an user</li>
 *   <li>Get, update feature entitlements of an user</li>
 *   <li>Get, update status of an user</li>
 * </ul></p>
 *
 */
@Slf4j
public class UserService {

  private final UserApi userApi;
  private final UsersApi usersApi;
  private final AuthSession authSession;

  public UserService(UserApi userApi, UsersApi usersApi, AuthSession authSession) {
    this.userApi = userApi;
    this.usersApi = usersApi;
    this.authSession = authSession;
  }

  /**
   * Retrieve user details of a particular user.
   * @param uid User Id
   * @return Details of the user.
   */
  public V2UserDetail getUserDetailByUid(@NonNull Long uid) {
    try {
      return userApi.v2AdminUserUidGet(authSession.getSessionToken(), uid);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Retrieve a list of users in the company (pod) by a filter.
   * If no filter is given, retrieving all users in the company (pod).
   *
   * @param filter using to filter users by
   * @return List of retrieved users
   */
  public List<V2UserDetail> listUsersDetailByFilter(@Nullable UserFilter filter) {
    try {
      if (filter == null) {
        return userApi.v2AdminUserListGet(authSession.getSessionToken(), null, null);
      }
      List<UserDetail> userDetailList = userApi.v1AdminUserFindPost(authSession.getSessionToken(), filter, null, null);
      return userDetailList.stream().map(UserDetailMapper.INSTANCE::userDetailToV2UserDetail).collect(Collectors.toList());
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Add a role to an user.
   *
   * @param uid User Id
   * @param roleId Role Id
   */
  public void addRoleToUser(@NonNull Long uid, @NonNull String roleId) {
    try {
      StringId stringId = new StringId().id(roleId);
      userApi.v1AdminUserUidRolesAddPost(authSession.getSessionToken(), uid, stringId);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Remove a role from an user.
   * @param uid User Id
   * @param roleId Role Id
   */
  public void removeRoleFromUser(@NonNull Long uid, @NonNull String roleId) {
    try {
      StringId stringId = new StringId().id(roleId);
      userApi.v1AdminUserUidRolesRemovePost(authSession.getSessionToken(), uid, stringId);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Get the url of avatar of an user
   * @param uid User Id
   * @return List of avatar urls of the user
   */
  public List<Avatar> getAvatarFromUser(@NonNull Long uid) {
    try {
      return userApi.v1AdminUserUidAvatarGet(authSession.getSessionToken(), uid);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Update avatar of an user
   * @param uid User Id
   * @param image The avatar image for the user profile picture.The image must be a base64-encoded.
   */
  public void updateAvatarOfUser(@NonNull Long uid, @NonNull String image) {
    try {
      AvatarUpdate avatarUpdate = new AvatarUpdate().image(image);
      userApi.v1AdminUserUidAvatarUpdatePost(authSession.getSessionToken(), uid, avatarUpdate);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Get disclaimer assigned to an user.
   *
   * @param uid User Id
   * @return Disclaimer assigned to the user.
   */
  public Disclaimer getDisclaimerAssignedToUser(@NonNull Long uid) {
    try {
      return userApi.v1AdminUserUidDisclaimerGet(authSession.getSessionToken(), uid);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Unassign disclaimer from an user.
   *
   * @param uid User Id
   */
  public void unAssignDisclaimerFromUser(@NonNull Long uid) {
    try {
      userApi.v1AdminUserUidDisclaimerDelete(authSession.getSessionToken(), uid);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Assign disclaimer to an user.
   *
   * @param uid User Id
   * @param disclaimerId Disclaimer to be assigned
   */
  public void assignDisclaimerToUser(@NonNull Long uid, @NonNull String disclaimerId) {
    try {
      StringId stringId = new StringId().id(disclaimerId);
      userApi.v1AdminUserUidDisclaimerUpdatePost(authSession.getSessionToken(), uid, stringId);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Get delegates assigned to an user.
   *
   * @param uid User Id
   * @return List of delegates assigned to an user.
   */
  public List<Long> getDelegatesAssignedToUser(@NonNull Long uid) {
    try {
      return userApi.v1AdminUserUidDelegatesGet(authSession.getSessionToken(), uid);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Update delegates assigned to an user.
   *
   * @param uid User Id
   * @param delegatedUserId Delegated user Id to be assigned
   * @param actionEnum Action to be performed
   */
  public void updateDelegatesAssignedToUser(@NonNull Long uid, @NonNull Long delegatedUserId, @NonNull DelegateAction.ActionEnum actionEnum) {
    try {
      DelegateAction delegateAction = new DelegateAction().action(actionEnum).userId(delegatedUserId);
      userApi.v1AdminUserUidDelegatesUpdatePost(authSession.getSessionToken(), uid, delegateAction);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Get feature entitlements of an user.
   *
   * @param uid User Id
   * @return List of feature entitlements of the user.
   */
  public List<Feature> getFeatureEntitlementsOfUser(@NonNull Long uid) {
    try {
      return userApi.v1AdminUserUidFeaturesGet(authSession.getSessionToken(), uid);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Update feature entitlements of an user.
   *
   * @param uid User Id
   * @param features List of feature entitlements to be updated
   */
  public void updateFeatureEntitlementsOfUser(@NonNull Long uid, @NonNull List<Feature> features) {
    try {
      userApi.v1AdminUserUidFeaturesUpdatePost(authSession.getSessionToken(), uid, features);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Get status of an user.
   *
   * @param uid User Id
   * @return Status of the user.
   */
  public UserStatus getStatusOfUser(@NonNull Long uid) {
    try {
      return userApi.v1AdminUserUidStatusGet(authSession.getSessionToken(), uid);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Update the status of an user
   *
   * @param uid User Id
   * @param status Status to be updated to the user
   */
  public void updateStatusOfUser(@NonNull Long uid, @NonNull UserStatus status) {
    try {
      userApi.v1AdminUserUidStatusUpdatePost(authSession.getSessionToken(), uid, status);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Get user by id
   *
   * @param uid User Id
   * @param local If true then a local DB search will be performed and only local pod users will be
   *             returned. If absent or false then a directory search will be performed and users
   *             from other pods who are visible to the calling user will also be returned.
   * @return User found by uid
   */
  public UserV2 getUserById(@NonNull Long uid, @Nullable Boolean local) {
    try {
      return usersApi.v2UserGet(authSession.getSessionToken(), uid, null, null, local);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Get user by email
   *
   * @param email Email address of the user
   * @param local If true then a local DB search will be performed and only local pod users will be
   *             returned. If absent or false then a directory search will be performed and users
   *             from other pods who are visible to the calling user will also be returned.
   * @return User found by email
   */
  public UserV2 getUserByEmail(@NonNull String email, @Nullable Boolean local) {
    try {
      return usersApi.v2UserGet(authSession.getSessionToken(), null, email, null, local);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Get user by email
   *
   * @param username Username of the user
   * @return User found by username
   */
  public UserV2 getUserByUsername(@NonNull String username) {
    try {
      return usersApi.v2UserGet(authSession.getSessionToken(), null, null, username, true);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Search user by list of user ids
   *
   * @param uidList List of user ids
   * @param local If true then a local DB search will be performed and only local pod users will be
   *             returned. If absent or false then a directory search will be performed and users
   *             from other pods who are visible to the calling user will also be returned.
   * @return Users found by user ids
   */
  public List<UserV2> searchUserByIds(@NonNull List<Long> uidList, @Nullable Boolean local) {
    try {
      String uids = uidList.stream().map(String::valueOf).collect(Collectors.joining(","));
      V2UserList v2UserList = usersApi.v3UsersGet(authSession.getSessionToken(), uids, null, null, local);
      return v2UserList.getUsers();
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Search user by list of email addresses.
   *
   * @param emailList List of email addresses
   * @param local If true then a local DB search will be performed and only local pod users will be
   *             returned. If absent or false then a directory search will be performed and users
   *             from other pods who are visible to the calling user will also be returned.
   * @return Users found by emails.
   */
  public List<UserV2> searchUserByEmails(@NonNull List<String> emailList, @Nullable Boolean local) {
    try {
      String emails = String.join(",", emailList);
      V2UserList v2UserList = usersApi.v3UsersGet(authSession.getSessionToken(), null, emails, null, local);
      return v2UserList.getUsers();
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Search user by list of usernames.
   *
   * @param usernameList List of usernames
   * @return Users found by usernames
   */
  public List<UserV2> searchUserByUsernames(@NonNull List<String> usernameList) {
    try {
      String usernames = String.join(",", usernameList);
      V2UserList v2UserList = usersApi.v3UsersGet(authSession.getSessionToken(), null, null, usernames, true);
      return v2UserList.getUsers();
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Search user by a complicated search query.
   *
   * @param query Searching query containing complicated information like title, location, company...
   * @param local If true then a local DB search will be performed and only local pod users will be
   *              returned. If absent or false then a directory search will be performed and users
   *              from other pods who are visible to the calling user will also be returned.
   * @return List of users found by query
   */
  public List<UserV2> searchUserBySearchQuery(@NonNull UserSearchQuery query, @Nullable Boolean local) {
    try {
      UserSearchResults results = usersApi.v1UserSearchPost(authSession.getSessionToken(), query, null, null, local);
      return results.getUsers();
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }
}
