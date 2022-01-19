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

  public Type getType(@Nonnull String typeId) {
    return this.executeAndRetry("groupExt.listTypes",
        () -> this.typeApi.getType("", typeId)
    );
  }

  public TypeList listTypes(@Nullable Status status, @Nullable String before, @Nullable String after,
      @Nullable Integer limit, @Nullable SortOrder sortOrder) {
    return this.executeAndRetry("groupExt.listTypes",
        () -> this.typeApi.listTypes("", status, before, after, limit, sortOrder)
    );
  }

  public ReadGroup insertGroup(@Nonnull final CreateGroup group) {
    return this.executeAndRetry("groupExt.insertGroup",
        () -> this.groupApi.insertGroup("", group)
    );
  }

  public ReadGroup updateGroup(@Nonnull String ifMatch, @Nonnull String groupId, @Nonnull UpdateGroup updateGroup) {
    return this.executeAndRetry("groupExt.updateGroup",
        () -> this.groupApi.updateGroup("", ifMatch, groupId, updateGroup)
    );
  }

  public ReadGroup updateAvatar(@Nonnull String groupId, @Nonnull byte[] image) {
    return this.executeAndRetry("groupExt.updateAvatar",
        () -> this.groupApi.updateAvatar("", groupId, new UploadAvatar().image(image))
    );
  }

  public ReadGroup getGroup(@Nonnull String groupId) {
    return this.executeAndRetry("groupExt.getGroup",
        () -> this.groupApi.getGroup("", groupId)
    );
  }

  public GroupList listGroups(@Nonnull String typeId, @Nullable Status status, @Nullable String before,
      @Nullable String after, @Nullable Integer limit, @Nullable SortOrder sortOrder) {
    return this.executeAndRetry("groupExt.listGroups",
        () -> this.groupApi.listGroups("", typeId, status, before, after, limit, sortOrder)
    );
  }

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
