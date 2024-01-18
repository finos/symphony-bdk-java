package com.symphony.bdk.ext.group;

import com.symphony.bdk.core.auth.BotAuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.extension.BdkApiClientFactoryAware;
import com.symphony.bdk.core.extension.BdkAuthenticationAware;
import com.symphony.bdk.core.extension.BdkRetryBuilderAware;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.extension.BdkExtension;
import com.symphony.bdk.extension.BdkExtensionServiceProvider;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

@Slf4j
@API(status = API.Status.EXPERIMENTAL, since = "20.13")
public class SymphonyGroupBdkExtension implements
    BdkExtension,
    BdkExtensionServiceProvider<SymphonyGroupService>,
    BdkApiClientFactoryAware,
    BdkAuthenticationAware,
    BdkRetryBuilderAware
{

  private RetryWithRecoveryBuilder<?> retryBuilder;
  private ApiClientFactory apiClientFactory;
  private BotAuthSession session;

  private SymphonyGroupService groupService;

  @Override
  public void setApiClientFactory(ApiClientFactory apiClientFactory) {
    this.apiClientFactory = apiClientFactory;
  }

  @Override
  public void setAuthSession(BotAuthSession session) {
    this.session = session;
  }

  @Override
  public void setRetryBuilder(RetryWithRecoveryBuilder<?> retryBuilder) {
    this.retryBuilder = retryBuilder;
  }

  @Override
  public SymphonyGroupService getService() {

    if (this.groupService == null) {
      this.groupService = new SymphonyGroupService(this.retryBuilder, this.apiClientFactory, this.session);
    }

    return this.groupService;
  }
}
