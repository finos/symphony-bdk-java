package com.symphony.bdk.core.service.app;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.ExtAppAuthSession;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.application.ApplicationService;
import com.symphony.bdk.core.test.JsonHelper;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.gen.api.AppEntitlementApi;
import com.symphony.bdk.gen.api.ApplicationApi;
import com.symphony.bdk.gen.api.AppsApi;
import com.symphony.bdk.gen.api.model.AppUsersResponse;
import com.symphony.bdk.http.api.ApiClient;

import com.symphony.bdk.http.api.ApiException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AppUsersServiceTest {

  private AppsApi appsApi;
  private MockApiClient mockApiClient;
  private AppUsersService service;

  private static final String SESSION_TOKEN = "app-session-token";
  private static final String APP_ID = "my_app-id";
  private static final String LIST_APP_USERS_PATH = "/v5/users/apps/{appId}";

  @BeforeEach
  void init() {
    this.mockApiClient = new MockApiClient();
    ExtAppAuthSession authSession = mock(ExtAppAuthSession.class);
    ApiClient podClient = mockApiClient.getApiClient("");
    AppsApi applicationApi = new AppsApi(podClient);
    this.appsApi = spy(applicationApi);
    this.service = new AppUsersService(APP_ID, this.appsApi, authSession,
        new RetryWithRecoveryBuilder<>());

    when(authSession.getAppSession()).thenReturn(SESSION_TOKEN);
  }

  @Test
  void listAppUsers() throws IOException {
    mockApiClient.onGet(LIST_APP_USERS_PATH.replace("{appId}", APP_ID),
        JsonHelper.readFromClasspath("/app/list_app_users.json"));
    AppUsersResponse response = this.service.listAppUsers();
    assertEquals(9, response.getUsers().size());
    assertEquals("premium", response.getUsers().get(0).getProduct().getType());
    assertEquals("default", response.getUsers().get(1).getProduct().getType());
    assertEquals(9, response.getPage().getTotalElements());
    assertEquals(1, response.getPage().getTotalPages());
    assertEquals(100, response.getPage().getSize());
    assertEquals(0, response.getPage().getNumber());
  }

  @Test
  void listAppUsersWithPage() throws IOException, ApiException {
    mockApiClient.onGet(LIST_APP_USERS_PATH.replace("{appId}", APP_ID),
        JsonHelper.readFromClasspath("/app/list_app_users.json"));
    Integer PAGE = 2;

    this.service.listAppUsers(PAGE);

    verify(this.appsApi).findAppUsers(eq(SESSION_TOKEN), eq(APP_ID), eq(true), eq(PAGE), eq(100), eq(null));
  }

  @Test
  void listAppUsersWithPageAndSize() throws IOException, ApiException {
    mockApiClient.onGet(LIST_APP_USERS_PATH.replace("{appId}", APP_ID),
        JsonHelper.readFromClasspath("/app/list_app_users.json"));
    Integer PAGE = 2;
    Integer PAGE_SIZE = 10;

    this.service.listAppUsers(PAGE, PAGE_SIZE);

    verify(this.appsApi).findAppUsers(eq(SESSION_TOKEN), eq(APP_ID), eq(true), eq(PAGE), eq(PAGE_SIZE), eq(null));
  }

  @Test
  void listAppUsersWithPageAndSizeAndSort() throws IOException, ApiException {
    mockApiClient.onGet(LIST_APP_USERS_PATH.replace("{appId}", APP_ID),
        JsonHelper.readFromClasspath("/app/list_app_users.json"));
    Integer PAGE = 2;
    Integer PAGE_SIZE = 10;
    List<String> SORT_PARAMETERS = List.of("userId");

    this.service.listAppUsers(PAGE, PAGE_SIZE, SORT_PARAMETERS);

    verify(this.appsApi).findAppUsers(eq(SESSION_TOKEN), eq(APP_ID), eq(true), eq(PAGE), eq(PAGE_SIZE), eq(SORT_PARAMETERS));
  }
}
