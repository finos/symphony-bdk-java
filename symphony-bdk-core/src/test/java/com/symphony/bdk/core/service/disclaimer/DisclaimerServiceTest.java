package com.symphony.bdk.core.service.disclaimer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.BotAuthSession;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.test.JsonHelper;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.gen.api.DisclaimerApi;

import com.symphony.bdk.gen.api.model.Disclaimer;
import com.symphony.bdk.http.api.ApiClient;

import com.symphony.bdk.http.api.ApiRuntimeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class DisclaimerServiceTest {

  private static final String V1_DISCLAIMER_BY_ID = "/pod/v1/admin/disclaimer/%s";
  private static final String V1_LIST_DISCLAIMERS = "/pod/v1/admin/disclaimer/list";
  private static final String V1_DISCLAIMER_USERS = "/pod/v1/admin/disclaimer/%s/users";
  private static final String MISSING_REQUIRED_PARAMETER_EXCEPTION_MESSAGE = "Missing the required parameter '%s' when calling %s";

  private DisclaimerService disclaimerService;
  private MockApiClient mockApiClient;
  private DisclaimerApi spyDisclaimerApi;
  private BotAuthSession authSession;

  @BeforeEach
  void init() {
    this.mockApiClient = new MockApiClient();
    this.authSession = mock(BotAuthSession.class);

    ApiClient podClient = this.mockApiClient.getApiClient("/pod");
    this.spyDisclaimerApi = spy(new DisclaimerApi(podClient));
    this.disclaimerService = new DisclaimerService(this.spyDisclaimerApi, this.authSession, new RetryWithRecoveryBuilder<>());

    when(authSession.getSessionToken()).thenReturn("1234");
  }

  @Test
  void getDisclaimerById() throws IOException {
    String response = JsonHelper.readFromClasspath("/disclaimer/disclaimer.json");
    this.mockApiClient.onGet(String.format(V1_DISCLAIMER_BY_ID, "666"), response);
    Disclaimer disclaimer = this.disclaimerService.getDisclaimer("666");

    assertEquals("New Enterprise Disclaimer", disclaimer.getName());
  }

  @Test
  void getDisclaimerMissingId() {
    Exception exception = assertThrows(ApiRuntimeException.class,
        () -> this.disclaimerService.getDisclaimer(null));
    assertTrue(exception.getMessage().contains(
        String.format(MISSING_REQUIRED_PARAMETER_EXCEPTION_MESSAGE, "did", "v1AdminDisclaimerDidGet")));
  }

  @Test
  void listDisclaimers() throws IOException {
    String response = JsonHelper.readFromClasspath("/disclaimer/list_disclaimers.json");
    this.mockApiClient.onGet(V1_LIST_DISCLAIMERS, response);
    List<Disclaimer> disclaimers = this.disclaimerService.listDisclaimers();

    assertEquals(2, disclaimers.size());
    assertEquals("571d2052e4b042aaf06d2e7a", disclaimers.get(0).getId());
    assertEquals("571d20dae4b042aaf06d2e7c", disclaimers.get(1).getId());
  }

  @Test
  void listDisclaimerUsers() {
    List<Long> response = Arrays.asList(7215545078541L, 3015566078276L);
    String disclaimerId = "777";
    this.mockApiClient.onGet(String.format(V1_DISCLAIMER_USERS, disclaimerId), response.toString());
    List<Long> disclaimerUsers = this.disclaimerService.listDisclaimerUsers(disclaimerId);

    assertEquals(2, disclaimerUsers.size());
    assertEquals(response, disclaimerUsers);
  }

  @Test
  void listDisclaimerUsersMissingId() {
    Exception exception = assertThrows(ApiRuntimeException.class,
        () -> this.disclaimerService.listDisclaimerUsers(null));

    assertTrue(exception.getMessage().contains(
        String.format(MISSING_REQUIRED_PARAMETER_EXCEPTION_MESSAGE, "did", "v1AdminDisclaimerDidUsersGet")));
  }
}
