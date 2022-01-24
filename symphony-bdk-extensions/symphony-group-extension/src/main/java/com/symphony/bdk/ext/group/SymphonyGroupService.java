package com.symphony.bdk.ext.group;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.retry.function.SupplierWithApiException;
import com.symphony.bdk.core.util.UserIdUtil;
import com.symphony.bdk.ext.group.auth.OAuth;
import com.symphony.bdk.ext.group.auth.OAuthSession;
import com.symphony.bdk.ext.group.gen.api.GroupApi;
import com.symphony.bdk.ext.group.gen.api.TypeApi;
import com.symphony.bdk.ext.group.gen.api.model.AddMember;
import com.symphony.bdk.ext.group.gen.api.model.CreateGroup;
import com.symphony.bdk.ext.group.gen.api.model.GroupList;
import com.symphony.bdk.ext.group.gen.api.model.Member;
import com.symphony.bdk.ext.group.gen.api.model.ReadGroup;
import com.symphony.bdk.ext.group.gen.api.model.SortOrder;
import com.symphony.bdk.ext.group.gen.api.model.Status;
import com.symphony.bdk.ext.group.gen.api.model.Type;
import com.symphony.bdk.ext.group.gen.api.model.TypeList;
import com.symphony.bdk.ext.group.gen.api.model.UpdateGroup;
import com.symphony.bdk.ext.group.gen.api.model.UploadAvatar;
import com.symphony.bdk.extension.BdkExtensionService;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Service class for managing groups.
 *
 * @see <a href="https://developers.symphony.com/restapi/reference#groups-distribution-lists">Groups - Distribution Lists</a>
 */
public class SymphonyGroupService implements BdkExtensionService {

  private final RetryWithRecoveryBuilder<?> retryBuilder;
  private final TypeApi typeApi;
  private final GroupApi groupApi;

  public SymphonyGroupService(RetryWithRecoveryBuilder<?> retryBuilder, ApiClientFactory apiClientFactory, AuthSession session) {

    // oAuthSession does not need to be cached, it will be refreshed everytime an API call returns 401
    final OAuthSession oAuthSession = new OAuthSession(apiClientFactory.getLoginClient(), session, retryBuilder);
    oAuthSession.refresh();

    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder)
        .recoveryStrategy(ApiException::isUnauthorized, oAuthSession::refresh);

    final ApiClient client = apiClientFactory.getPodClient("/profile-manager");
    final OAuth auth = new OAuth(oAuthSession::getBearerToken);
    client.getAuthentications().put("bearerAuth", auth);

    this.groupApi = new GroupApi(client);
    this.typeApi = new TypeApi(client);
  }

  /**
   * Retrieve a specific type
   *
   * @param typeId the ID of the type to be retrieved
   * @return the type details
   */
  public Type getType(@Nonnull String typeId) {
    return this.executeAndRetry("groupExt.listTypes",
        () -> this.typeApi.getType("", typeId)
    );
  }

  /**
   * List all types
   *
   * @param status the status of the types to be returned (active or deleted)
   * @param before NOT SUPPORTED YET, currently ignored
   * @param after cursor that points to the end of the current page of data. If not present, the current page is the first page
   * @param limit maximum number of items to return
   * @param sortOrder items sorting direction (ordered by createdDate)
   * @return the list of all matching types
   */
  public TypeList listTypes(@Nullable Status status, @Nullable String before, @Nullable String after,
      @Nullable Integer limit, @Nullable SortOrder sortOrder) {
    return this.executeAndRetry("groupExt.listTypes",
        () -> this.typeApi.listTypes("", status, before, after, limit, sortOrder)
    );
  }

  /**
   * Create a new group
   * @see <a href="https://developers.symphony.com/restapi/reference#insertgroup">Insert a new group</a>
   *
   * @param group the details of the group to be created
   * @return the created group
   */
  public ReadGroup insertGroup(@Nonnull final CreateGroup group) {
    return this.executeAndRetry("groupExt.insertGroup",
        () -> this.groupApi.insertGroup("", group)
    );
  }

  /**
   * Update an existing group
   * @see <a href="https://developers.symphony.com/restapi/reference#updateavatar">Update a group</a>
   *
   * @param ifMatch eTag of the group to be updated
   * @param groupId the ID of the group
   * @param updateGroup the group fields to be updated
   * @return
   */
  public ReadGroup updateGroup(@Nonnull String ifMatch, @Nonnull String groupId, @Nonnull UpdateGroup updateGroup) {
    return this.executeAndRetry("groupExt.updateGroup",
        () -> this.groupApi.updateGroup("", ifMatch, groupId, updateGroup)
    );
  }

  /**
   * Update the group avatar
   * @see <a href="https://developers.symphony.com/restapi/reference#updateavatar">Update the group avatar</a>
   *
   * @param groupId the ID of the group
   * @param image the byte array of the avatar image for the user profile picture.
   *              The image must be a base64-encoded .jpg, .png, or .gif. Image size limit: 2 MB
   * @return the updated group
   */
  public ReadGroup updateAvatar(@Nonnull String groupId, @Nonnull byte[] image) {
    return this.executeAndRetry("groupExt.updateAvatar",
        () -> this.groupApi.updateAvatar("", groupId, new UploadAvatar().image(image))
    );
  }

  /**
   * Retrieve a specific group
   * @see <a href="https://developers.symphony.com/restapi/reference#getgroup">Retrieve a group</a>
   *
   * @param groupId the ID of the group to retrive
   * @return the group details
   */
  public ReadGroup getGroup(@Nonnull String groupId) {
    return this.executeAndRetry("groupExt.getGroup",
        () -> this.groupApi.getGroup("", groupId)
    );
  }

  /**
   * List all the groups of a specified type
   * @see <a href="https://developers.symphony.com/restapi/reference#listgroups">List all groups of specified type</a>
   *
   * @param typeId the group type ID
   * @param status filter by status, active or deleted. If not specified, both are returned
   * @param before NOT SUPPORTED YET, currently ignored.
   * @param after cursor that points to the end of the current page of data. If not present, the current page is the first page
   * @param limit maximum number of items to return
   * @param sortOrder sorting direction of items (ordered by creation date)
   * @return the list of matching groups
   */
  public GroupList listGroups(@Nonnull String typeId, @Nullable Status status, @Nullable String before,
      @Nullable String after, @Nullable Integer limit, @Nullable SortOrder sortOrder) {
    return this.executeAndRetry("groupExt.listGroups",
        () -> this.groupApi.listGroups("", typeId, status, before, after, limit, sortOrder)
    );
  }

  /**
   * Add a new user to an existing group.
   * @see <a href="https://developers.symphony.com/restapi/reference#addmembertogroup">Add a new user to a an existing group</a>
   *
   * @param groupId the ID of the group in which to add the user
   * @param userId The ID of the user to be added into the group
   * @return the updated group
   */
  public ReadGroup addMemberToGroup(@Nonnull String groupId, @Nonnull Long userId) {
    return this.executeAndRetry("groupExt.addMemberToGroup",
        () -> this.groupApi.addMemberToGroup("", groupId,
            new AddMember().member(new Member().memberId(userId).memberTenant(UserIdUtil.extractTenantId(userId))))
    );
  }

  private <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    return RetryWithRecovery.executeAndRetry(
        this.retryBuilder,
        name,
        this.groupApi.getApiClient().getBasePath(),
        supplier
    );
  }
}
