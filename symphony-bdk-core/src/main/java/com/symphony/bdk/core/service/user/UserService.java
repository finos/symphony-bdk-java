package com.symphony.bdk.core.service.user;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.user.constant.RoleId;
import com.symphony.bdk.core.service.user.mapper.UserDetailMapper;
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
import com.symphony.bdk.gen.api.model.UserStatus;
import com.symphony.bdk.gen.api.model.V2UserDetail;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apiguardian.api.API;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Service class for managing users.
 * <p>
 * This service is used for retrieving information about a particular user,
 * search users by ids, emails or usernames, perform some action related to
 * user like:
 * <p><ul>
 * <li>Add or remove roles from an user</li>
 * <li>Get or update avatar of an user</li>
 * <li>Get, assign or unassign disclaimer to an user</li>
 * <li>Get, update feature entitlements of an user</li>
 * <li>Get, update status of an user</li>
 * </ul></p>
 */
@Slf4j
@API(status = API.Status.STABLE)
public class UserService extends OboUserService {

  public UserService(UserApi userApi, UsersApi usersApi, AuthSession authSession, RetryWithRecoveryBuilder retryBuilder) {
    super(userApi, usersApi, authSession, retryBuilder);
  }

  /**
   * Retrieve user details of a particular user.
   *
   * @param uid User Id
   * @return Details of the user.
   * @see <a href="https://developers.symphony.com/restapi/reference#get-user-v2">Get User v2</a>
   */
  public V2UserDetail getUserDetailByUid(@NonNull Long uid) {
    return executeAndRetry("getUserDetailByUid", () -> userApi.v2AdminUserUidGet(authSession.getSessionToken(), uid));
  }

  /**
   * Retrieve all users in the company (pod).
   *
   * @return List of retrieved users
   * @see <a href="https://developers.symphony.com/restapi/reference#list-users-v2">List Users V2</a>
   */
  public List<V2UserDetail> listUsersDetail() {
    return executeAndRetry("listUsersDetail",
        () -> userApi.v2AdminUserListGet(authSession.getSessionToken(), null, null));
  }

  /**
   * Retrieve a list of users in the company (pod) by a filter.
   *
   * @param filter using to filter users by
   * @return List of retrieved users
   * @see <a href="https://developers.symphony.com/restapi/reference#find-users">Find Users V1</a>
   * @see com.symphony.bdk.core.service.user.constant.UserFeature
   */
  public List<V2UserDetail> listUsersDetail(@NonNull UserFilter filter) {
    List<UserDetail> userDetailList = executeAndRetry("listUsersDetail",
        () -> userApi.v1AdminUserFindPost(authSession.getSessionToken(), filter, null, null));
    return userDetailList.stream()
        .map(UserDetailMapper.INSTANCE::userDetailToV2UserDetail)
        .collect(Collectors.toList());
  }

  /**
   * Add a role to an user.
   *
   * @param uid    User Id
   * @param roleId Role Id
   * @see <a href="https://developers.symphony.com/restapi/reference#add-role">Add Role</a>
   */
  public void addRoleToUser(@NonNull Long uid, @NonNull RoleId roleId) {
    StringId stringId = new StringId().id(roleId.name());
    executeAndRetry("addRoleToUser",
        () -> userApi.v1AdminUserUidRolesAddPost(authSession.getSessionToken(), uid, stringId));
  }

  /**
   * Remove a role from an user.
   *
   * @param uid    User Id
   * @param roleId Role Id
   * @see <a href="https://developers.symphony.com/restapi/reference#remove-role">Remove Role</a>
   */
  public void removeRoleFromUser(@NonNull Long uid, @NonNull RoleId roleId) {
    StringId stringId = new StringId().id(roleId.name());
    executeAndRetry("removeRoleFromUser",
        () -> userApi.v1AdminUserUidRolesRemovePost(authSession.getSessionToken(), uid, stringId));
  }

  /**
   * Get the url of avatar of an user
   *
   * @param uid User Id
   * @return List of avatar urls of the user
   * @see <a href="https://developers.symphony.com/restapi/reference#user-avatar">User Avatar</a>
   */
  public List<Avatar> getAvatarFromUser(@NonNull Long uid) {
    return executeAndRetry("getAvatarFromUser",
        () -> userApi.v1AdminUserUidAvatarGet(authSession.getSessionToken(), uid));
  }

  /**
   * Update avatar of an user
   *
   * @param uid   User Id
   * @param image The avatar image for the user profile picture.The image must be a base64-encoded.
   * @see <a href="https://developers.symphony.com/restapi/reference#update-user-avatar">Update User Avatar</a>
   */
  public void updateAvatarOfUser(@NonNull Long uid, @NonNull String image) {
    AvatarUpdate avatarUpdate = new AvatarUpdate().image(image);
    executeAndRetry("updateAvatarOfUser",
        () -> userApi.v1AdminUserUidAvatarUpdatePost(authSession.getSessionToken(), uid, avatarUpdate));
  }

  /**
   * Update avatar of an user
   *
   * @param uid   User Id
   * @param image The avatar image in bytes array for the user profile picture.
   * @see <a href="https://developers.symphony.com/restapi/reference#update-user-avatar">Update User Avatar</a>
   */
  public void updateAvatarOfUser(@NonNull Long uid, @NonNull byte[] image) {
    String imageBase64 = Base64.getEncoder().encodeToString(image);
    this.updateAvatarOfUser(uid, imageBase64);
  }

  /**
   * Update avatar of an user
   *
   * @param uid         User Id
   * @param imageStream The avatar image input stream for the user profile picture.
   * @see <a href="https://developers.symphony.com/restapi/reference#update-user-avatar">Update User Avatar</a>
   */
  public void updateAvatarOfUser(@NonNull Long uid, @NonNull InputStream imageStream) throws IOException {
    byte[] bytes = IOUtils.toByteArray(imageStream);
    this.updateAvatarOfUser(uid, bytes);
  }

  /**
   * Get disclaimer assigned to an user.
   *
   * @param uid User Id
   * @return Disclaimer assigned to the user.
   * @see <a href="https://developers.symphony.com/restapi/reference#user-disclaimer">User Disclaimer</a>
   */
  public Disclaimer getDisclaimerAssignedToUser(@NonNull Long uid) {
    return executeAndRetry("getDisclaimerAssignedToUser",
        () -> userApi.v1AdminUserUidDisclaimerGet(authSession.getSessionToken(), uid));
  }

  /**
   * Unassign disclaimer from an user.
   *
   * @param uid User Id
   * @see <a href="https://developers.symphony.com/restapi/reference#unassign-user-disclaimer">Unassign User Disclaimer</a>
   */
  public void unAssignDisclaimerFromUser(@NonNull Long uid) {
    executeAndRetry("unAssignDisclaimerFromUser",
        () -> userApi.v1AdminUserUidDisclaimerDelete(authSession.getSessionToken(), uid));
  }

  /**
   * Assign disclaimer to an user.
   *
   * @param uid          User Id
   * @param disclaimerId Disclaimer to be assigned
   * @see <a href="https://developers.symphony.com/restapi/reference#update-disclaimer">Update User Disclaimer</a>
   */
  public void assignDisclaimerToUser(@NonNull Long uid, @NonNull String disclaimerId) {
    StringId stringId = new StringId().id(disclaimerId);
    executeAndRetry("assignDisclaimerToUser",
        () -> userApi.v1AdminUserUidDisclaimerUpdatePost(authSession.getSessionToken(), uid, stringId));

  }

  /**
   * Get delegates assigned to an user.
   *
   * @param uid User Id
   * @return List of delegates assigned to an user.
   * @see <a href="https://developers.symphony.com/restapi/reference#delegates">User Delegates</a>
   */
  public List<Long> getDelegatesAssignedToUser(@NonNull Long uid) {
    return executeAndRetry("getDelegatesAssignedToUser",
        () -> userApi.v1AdminUserUidDelegatesGet(authSession.getSessionToken(), uid));
  }

  /**
   * Update delegates assigned to an user.
   *
   * @param uid             User Id
   * @param delegatedUserId Delegated user Id to be assigned
   * @param actionEnum      Action to be performed
   * @see <a href="https://developers.symphony.com/restapi/reference#update-delegates">Update User Delegates</a>
   */
  public void updateDelegatesAssignedToUser(@NonNull Long uid, @NonNull Long delegatedUserId,
      @NonNull DelegateAction.ActionEnum actionEnum) {
    DelegateAction delegateAction = new DelegateAction().action(actionEnum).userId(delegatedUserId);
    executeAndRetry("updateDelegatesAssignedToUser",
        () -> userApi.v1AdminUserUidDelegatesUpdatePost(authSession.getSessionToken(), uid, delegateAction));
  }

  /**
   * Get feature entitlements of an user.
   *
   * @param uid User Id
   * @return List of feature entitlements of the user.
   * @see <a href="https://developers.symphony.com/restapi/reference#features">User Features</a>
   */
  public List<Feature> getFeatureEntitlementsOfUser(@NonNull Long uid) {
    return executeAndRetry("getFeatureEntitlementsOfUser",
        () -> userApi.v1AdminUserUidFeaturesGet(authSession.getSessionToken(), uid));
  }

  /**
   * Update feature entitlements of an user.
   *
   * @param uid      User Id
   * @param features List of feature entitlements to be updated
   * @see <a href="https://developers.symphony.com/restapi/reference#update-features">Update User Features</a>
   */
  public void updateFeatureEntitlementsOfUser(@NonNull Long uid, @NonNull List<Feature> features) {
    executeAndRetry("updateFeatureEntitlementsOfUser",
        () -> userApi.v1AdminUserUidFeaturesUpdatePost(authSession.getSessionToken(), uid, features));
  }

  /**
   * Get status of an user.
   *
   * @param uid User Id
   * @return Status of the user.
   * @see <a href="https://developers.symphony.com/restapi/reference#user-status">User Status</a>
   */
  public UserStatus getStatusOfUser(@NonNull Long uid) {
    return executeAndRetry("getStatusOfUser",
        () -> userApi.v1AdminUserUidStatusGet(authSession.getSessionToken(), uid));
  }

  /**
   * Update the status of an user
   *
   * @param uid    User Id
   * @param status Status to be updated to the user
   * @see <a href="https://developers.symphony.com/restapi/reference#update-user-status">Update User Status</a>
   */
  public void updateStatusOfUser(@NonNull Long uid, @NonNull UserStatus status) {
    executeAndRetry("updateStatusOfUser",
        () -> userApi.v1AdminUserUidStatusUpdatePost(authSession.getSessionToken(), uid, status));
  }
}
