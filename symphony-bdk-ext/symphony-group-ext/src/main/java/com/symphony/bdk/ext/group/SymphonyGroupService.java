package com.symphony.bdk.ext.group;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.retry.function.SupplierWithApiException;
import com.symphony.bdk.ext.group.auth.OAuth;
import com.symphony.bdk.ext.group.auth.OAuthClient;
import com.symphony.bdk.ext.group.gen.api.GroupApi;
import com.symphony.bdk.ext.group.gen.api.TypeApi;
import com.symphony.bdk.ext.group.gen.api.model.AddMember;
import com.symphony.bdk.ext.group.gen.api.model.CreateGroup;
import com.symphony.bdk.ext.group.gen.api.model.GroupList;
import com.symphony.bdk.ext.group.gen.api.model.ReadGroup;
import com.symphony.bdk.ext.group.gen.api.model.SortOrder;
import com.symphony.bdk.ext.group.gen.api.model.Status;
import com.symphony.bdk.ext.group.gen.api.model.Type;
import com.symphony.bdk.ext.group.gen.api.model.TypeList;
import com.symphony.bdk.ext.group.gen.api.model.UpdateGroup;
import com.symphony.bdk.ext.group.gen.api.model.UploadAvatar;
import com.symphony.bdk.http.api.ApiClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SymphonyGroupService {

  private final RetryWithRecoveryBuilder<?> retryBuilder;
  private final TypeApi typeApi;
  private final GroupApi groupApi;

  SymphonyGroupService(RetryWithRecoveryBuilder<?> retryBuilder, ApiClientFactory apiClientFactory, AuthSession session) {
    this.retryBuilder = retryBuilder;
    final OAuthClient oAuthClient = new OAuthClient(apiClientFactory.getLoginClient());
    this.groupApi = this.buildGroupApi(apiClientFactory, oAuthClient, session);
    this.typeApi = this.buildTypeApi(apiClientFactory, oAuthClient, session);
  }

  public Type getType(@Nonnull String typeId) {
    return this.executeAndRetry("groupExt.listTypes", this.getAddress(),
        () -> this.typeApi.getType("", typeId)
    );
  }

  public TypeList listTypes(@Nullable Status status, @Nullable String before, @Nullable String after,
      @Nullable Integer limit, @Nullable SortOrder sortOrder) {
    return this.executeAndRetry("groupExt.listTypes", this.getAddress(),
        () -> this.typeApi.listTypes("", status, before, after, limit, sortOrder)
    );
  }

  public ReadGroup insertGroup(@Nonnull final CreateGroup group) {
    return this.executeAndRetry("groupExt.insertGroup", this.getAddress(),
        () -> this.groupApi.insertGroup("", group)
    );
  }

  public ReadGroup updateGroup(@Nonnull String ifMatch, @Nonnull String groupId, @Nonnull UpdateGroup updateGroup) {
    return this.executeAndRetry("groupExt.updateGroup", this.getAddress(),
        () -> this.groupApi.updateGroup("", ifMatch, groupId, updateGroup)
    );
  }

  public ReadGroup updateAvatar(@Nonnull String groupId, @Nonnull UploadAvatar uploadAvatar) {
    return this.executeAndRetry("groupExt.updateAvatar", this.getAddress(),
        () -> this.groupApi.updateAvatar("", groupId, uploadAvatar)
    );
  }

  public ReadGroup getGroup(@Nonnull String groupId) {
    return this.executeAndRetry("groupExt.getGroup", this.getAddress(),
        () -> this.groupApi.getGroup("", groupId)
    );
  }

  public GroupList listGroups(@Nonnull String typeId, @Nullable Status status, @Nullable String before,
      @Nullable String after, @Nullable Integer limit, @Nullable SortOrder sortOrder) {
    return this.executeAndRetry("groupExt.listGroups", this.getAddress(),
        () -> this.groupApi.listGroups("", typeId, status, before, after, limit, sortOrder)
    );
  }

  public ReadGroup addMemberToGroup(@Nonnull String groupId, @Nonnull AddMember addMember) {
    return this.executeAndRetry("groupExt.addMemberToGroup", this.getAddress(),
        () -> this.groupApi.addMemberToGroup("", groupId, addMember)
    );
  }

  private <T> T executeAndRetry(String name, String address, SupplierWithApiException<T> supplier) {
    return RetryWithRecovery.executeAndRetry(this.retryBuilder, name, address, supplier);
  }

  private GroupApi buildGroupApi(ApiClientFactory apiClientFactory, OAuthClient oAuthClient, AuthSession session) {
    final ApiClient client = apiClientFactory.getPodClient("/profile-manager");
    final OAuth auth = new OAuth();
    auth.setBearerToken(executeAndRetry("groupExt.auth", "", () -> oAuthClient.retrieveBearerToken(session)));
    client.getAuthentications().put("bearerAuth", auth);
    return new GroupApi(client);
  }

  private TypeApi buildTypeApi(ApiClientFactory apiClientFactory, OAuthClient oAuthClient, AuthSession session) {
    final ApiClient client = apiClientFactory.getPodClient("/profile-manager");
    final OAuth auth = new OAuth();
    auth.setBearerToken(executeAndRetry("groupExt.auth", "", () -> oAuthClient.retrieveBearerToken(session)));
    client.getAuthentications().put("bearerAuth", auth);
    return new TypeApi(client);
  }

  private String getAddress() {
    return this.groupApi.getApiClient().getBasePath();
  }
}
