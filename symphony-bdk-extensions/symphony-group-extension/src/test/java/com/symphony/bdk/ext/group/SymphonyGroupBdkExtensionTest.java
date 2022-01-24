package com.symphony.bdk.ext.group;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.ext.group.auth.OAuthSession;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import com.symphony.bdk.http.api.ApiResponse;

import org.junit.jupiter.api.Test;

import java.util.Collections;

public class SymphonyGroupBdkExtensionTest {

  @Test
  void testGroupServiceInitialization() throws ApiException {
    ApiClient loginClient = spy(TestApiClient.class);

    final OAuthSession.TokenResponse tokenResponse = new OAuthSession.TokenResponse();
    tokenResponse.setToken("1234");

    when(loginClient.invokeAPI(eq("/idm/tokens"), eq("POST"), any(), any(), any(), any(), any(), any(), any(), any(),
        any()))
        .thenReturn(new ApiResponse<>(200, Collections.emptyMap(), tokenResponse));

    ApiClientFactory clientFactory = mock(ApiClientFactory.class);
    when(clientFactory.getLoginClient()).thenReturn(loginClient);
    when(clientFactory.getPodClient(eq("/profile-manager"))).thenReturn(mock(ApiClient.class));

    SymphonyGroupBdkExtension groupExtension = new SymphonyGroupBdkExtension();
    groupExtension.setApiClientFactory(clientFactory);
    groupExtension.setAuthSession(mock(AuthSession.class));
    groupExtension.setRetryBuilder(new RetryWithRecoveryBuilder<>());

    final SymphonyGroupService service = groupExtension.getService();
    assertNotNull(service);
  }
}
