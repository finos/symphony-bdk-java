package com.symphony.bdk.ext.group;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.retry.function.SupplierWithApiException;
import com.symphony.bdk.core.service.pagination.CursorBasedPaginatedApi;
import com.symphony.bdk.core.service.pagination.CursorBasedPaginatedService;
import com.symphony.bdk.core.service.pagination.PaginatedService;
import com.symphony.bdk.core.service.pagination.model.CursorPaginatedPayload;
import com.symphony.bdk.core.util.UserIdUtil;
import com.symphony.bdk.ext.group.auth.OAuth;
import com.symphony.bdk.ext.group.auth.OAuthSession;
import com.symphony.bdk.ext.group.gen.api.GroupApi;
import com.symphony.bdk.ext.group.gen.api.model.AddMember;
import com.symphony.bdk.ext.group.gen.api.model.CreateGroup;
import com.symphony.bdk.ext.group.gen.api.model.GroupList;
import com.symphony.bdk.ext.group.gen.api.model.Member;
import com.symphony.bdk.ext.group.gen.api.model.ReadGroup;
import com.symphony.bdk.ext.group.gen.api.model.SortOrder;
import com.symphony.bdk.ext.group.gen.api.model.Status;
import com.symphony.bdk.ext.group.gen.api.model.UpdateGroup;
import com.symphony.bdk.ext.group.gen.api.model.UploadAvatar;
import com.symphony.bdk.extension.BdkExtensionService;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Service class for managing groups.
 *
 * @see <a href="https://developers.symphony.com/restapi/reference#groups-distribution-lists">Groups - Distribution Lists</a>
 */
@API(status = API.Status.EXPERIMENTAL, since = "20.13")
public class SymphonyGroupService implements BdkExtensionService {

  private final RetryWithRecoveryBuilder<?> retryBuilder;
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
   * @see <a href="https://developers.symphony.com/restapi/reference#updategroup">Update a group</a>
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
   * List the groups of type "SDL"
   * @see <a href="https://developers.symphony.com/restapi/reference#listgroups">List all groups of specified type</a>
   *
   * @param status filter by status, active or deleted. If not specified, both are returned
   * @param before NOT SUPPORTED YET, currently ignored.
   * @param after cursor that points to the end of the current page of data. If not present, the current page is the first page
   * @param limit maximum number of items to return
   * @param sortOrder sorting direction of items (ordered by creation date)
   * @return the list of matching groups
   */
  public GroupList listGroups(@Nullable Status status, @Nullable String before,
      @Nullable String after, @Nullable Integer limit, @Nullable SortOrder sortOrder) {
    return this.executeAndRetry("groupExt.listGroups",
        () -> this.groupApi.listGroups("", "SDL", status, before, after, limit, sortOrder)
    );
  }

  /**
   * List all the groups of type "SDL" with automatic pagination
   * @see <a href="https://developers.symphony.com/restapi/reference#listgroups">List all groups of specified type</a>
   *
   * @param status filter by status, active or deleted. If not specified, both are returned
   * @param sortOrder sorting direction of items (ordered by creation date)
   * @param chunkSize the max number of groups to fetch in one api call (default value: 100)
   * @param maxItems the max number of groups to fetch in total (default value: 100)
   * @return a stream of {@link ReadGroup} with lazy fetching
   */
  @API(status = API.Status.EXPERIMENTAL)
  public Stream<ReadGroup> listAllGroups(@Nullable Status status, @Nullable SortOrder sortOrder,
      @Nullable Integer chunkSize, @Nullable Integer maxItems) {
    final CursorBasedPaginatedApi<ReadGroup> paginatedApi = (after, limit) -> new PayloadAdapter(listGroups(status, null, after, limit, sortOrder));
    final int actualChunkSize = chunkSize == null ? PaginatedService.DEFAULT_PAGINATION_CHUNK_SIZE : chunkSize;
    final int actualMaxItems = maxItems == null ? PaginatedService.DEFAULT_PAGINATION_TOTAL_SIZE : maxItems;

    return new CursorBasedPaginatedService<>(paginatedApi, actualChunkSize, actualMaxItems).stream();
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

  private static class PayloadAdapter implements CursorPaginatedPayload<ReadGroup> {
    private GroupList payload;

    public PayloadAdapter(GroupList payload) {
      this.payload = payload;
    }

    @Override
    public String getNext() {
      if (payload.getPagination() != null && payload.getPagination().getCursors() != null) {
        return payload.getPagination().getCursors().getAfter();
      }
      return null;
    }

    @Override
    public List<ReadGroup> getData() {
      return payload.getData();
    }
  }
}
