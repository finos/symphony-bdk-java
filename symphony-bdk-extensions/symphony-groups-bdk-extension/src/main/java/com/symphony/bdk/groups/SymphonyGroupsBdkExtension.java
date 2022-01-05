package com.symphony.bdk.groups;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.extension.BdkApiClientFactoryAware;
import com.symphony.bdk.core.extension.BdkAuthenticationAware;
import com.symphony.bdk.core.extension.BdkExtension;
import com.symphony.bdk.gen.api.GroupApi;
import com.symphony.bdk.gen.api.model.CreateGroup;
import com.symphony.bdk.gen.api.model.ReadGroup;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;
import com.symphony.bdk.http.api.auth.OAuth;

import javax.annotation.Nonnull;

public class SymphonyGroupsBdkExtension implements BdkExtension, BdkApiClientFactoryAware, BdkAuthenticationAware {

  private ApiClientFactory apiClientFactory;
  private GroupApi groupApi;
  private AuthSession session;

  /**
   * Inserts a new group.
   *
   * @param group New group information.
   * @return inserted group information.
   */
  public ReadGroup insertGroup(@Nonnull final CreateGroup group) {
    try {
      return this.getGroupApi().insertGroup("", group);
    } catch (ApiException e) {
      throw new ApiRuntimeException(e);
    }
  }

  @Override
  public void setApiClientFactory(ApiClientFactory apiClientFactory) {
    this.apiClientFactory = apiClientFactory;
  }

  @Override
  public void setAuthSession(AuthSession session) {
    this.session = session;
  }

  /**
   * @return lazy-loaded {@link GroupApi}
   */
  private GroupApi getGroupApi() throws ApiException {

    if (this.groupApi == null) {
      final ApiClient client = this.apiClientFactory.getBaseClient("/profile-manager");
      final OAuth auth = new OAuth();
      auth.setBearerToken(OAuthHelper.retrieveBearerToken(this.apiClientFactory.getLoginClient(), this.session));
      client.getAuthentications().put("bearerAuth", auth);
      this.groupApi = new GroupApi(client);
    }

    return this.groupApi;
  }
}
