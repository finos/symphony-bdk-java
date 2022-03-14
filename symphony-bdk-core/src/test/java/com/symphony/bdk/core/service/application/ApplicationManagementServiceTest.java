package com.symphony.bdk.core.service.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.test.JsonHelper;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.gen.api.AppEntitlementApi;
import com.symphony.bdk.gen.api.ApplicationApi;
import com.symphony.bdk.gen.api.model.ApplicationDetail;
import com.symphony.bdk.gen.api.model.PodAppEntitlement;
import com.symphony.bdk.gen.api.model.UserAppEntitlement;
import com.symphony.bdk.gen.api.model.UserAppEntitlementPatch;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ApplicationManagementServiceTest {

  private static final String V1_APP_CREATE = "/pod/v1/admin/app/create";
  private static final String V1_APP_UPDATE = "/pod/v1/admin/app/{appId}/update";
  private static final String V1_APP_DELETE = "/pod/v1/admin/app/{appId}/delete";
  private static final String V1_APP_GET = "/pod/v1/admin/app/{appId}/get";
  private static final String V1_LIST_APP_ENTITLEMENTS = "/pod/v1/admin/app/entitlement/list";
  private static final String V1_UPDATE_APP_ENTITLEMENTS = "/pod/v1/admin/app/entitlement/list";
  private static final String V1_USER_APPS = "/pod/v1/admin/user/{uid}/app/entitlement/list";
  private static final String V1_UPDATE_USER_APPS = "/pod/v1/admin/user/{uid}/app/entitlement/list";
  private static final String V1_PATCH_USER_APPS = "/pod/v1/admin/user/{uid}/app/entitlement/list";

  private ApplicationService service;
  private ApplicationApi applicationApi;
  private MockApiClient mockApiClient;

  @BeforeEach
  void init() {
    this.mockApiClient = new MockApiClient();
    AuthSession authSession = mock(AuthSession.class);
    ApiClient podClient = mockApiClient.getApiClient("/pod");
    ApplicationApi applicationApi = new ApplicationApi(podClient);
    AppEntitlementApi appEntitlementApi = new AppEntitlementApi(podClient);
    this.applicationApi = spy(applicationApi);
    this.service = new ApplicationService(this.applicationApi, appEntitlementApi, authSession,
        new RetryWithRecoveryBuilder<>());

    when(authSession.getSessionToken()).thenReturn("1234");
    when(authSession.getKeyManagerToken()).thenReturn("1234");
  }

  @Test
  void createApplicationTest() throws IOException {
    mockApiClient.onPost(V1_APP_CREATE, JsonHelper.readFromClasspath("/application/create_application.json"));

    ApplicationDetail appDetail = this.service.createApplication(new ApplicationDetail());

    assertEquals("my-test-app", appDetail.getApplicationInfo().getAppId());
    assertEquals("a test app", appDetail.getDescription());
  }

  @Test
  void createApplicationTestFailed() {
    mockApiClient.onPost(400, V1_APP_CREATE, "{}");

    ApplicationDetail applicationDetail = new ApplicationDetail();
    assertThrows(ApiRuntimeException.class, () -> this.service.createApplication(applicationDetail));
  }

  @Test
  void updateApplicationTest() throws IOException {
    mockApiClient.onPost(V1_APP_UPDATE.replace("{appId}", "my-test-app"),
        JsonHelper.readFromClasspath("/application/update_application.json"));

    ApplicationDetail appDetail = this.service.updateApplication("my-test-app", new ApplicationDetail());

    assertEquals("my-test-app", appDetail.getApplicationInfo().getAppId());
    assertEquals("updating an app", appDetail.getDescription());
  }

  @Test
  void updateApplicationTestFailed() {
    mockApiClient.onPost(400, V1_APP_UPDATE.replace("{appId}", "my-test-app"), "{}");

    ApplicationDetail applicationDetail = new ApplicationDetail();
    assertThrows(ApiRuntimeException.class,
        () -> this.service.updateApplication("my-test-app", applicationDetail));
  }

  @Test
  void deleteApplicationTest() throws ApiException {
    mockApiClient.onPost(V1_APP_DELETE.replace("{appId}", "my-test-app"), "{}");

    this.service.deleteApplication("my-test-app");

    verify(this.applicationApi).v1AdminAppIdDeletePost("1234", "my-test-app");
  }

  @Test
  void deleteApplicationTestFailed() {
    mockApiClient.onPost(400, V1_APP_DELETE.replace("{appId}", "my-test-app"), "{}");

    assertThrows(ApiRuntimeException.class,
        () -> this.service.deleteApplication("my-test-app"));
  }

  @Test
  void getApplicationTest() throws IOException {
    mockApiClient.onGet(V1_APP_GET.replace("{appId}", "my-test-app"),
        JsonHelper.readFromClasspath("/application/get_application.json"));

    ApplicationDetail appDetail = this.service.getApplication("my-test-app");

    assertEquals("my-test-app", appDetail.getApplicationInfo().getAppId());
    assertEquals("getting an app", appDetail.getDescription());
  }

  @Test
  void getApplicationTestFailed() {
    mockApiClient.onGet(400, V1_APP_GET.replace("{appId}", "my-test-app"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.getApplication("my-test-app"));
  }

  @Test
  void listAppEntitlementsTest() throws IOException {
    mockApiClient.onGet(V1_LIST_APP_ENTITLEMENTS,
        JsonHelper.readFromClasspath("/application/list_app_entitlements.json"));

    List<PodAppEntitlement> entitlements = this.service.listApplicationEntitlements();

    assertEquals(entitlements.size(), 3);
    assertEquals(entitlements.get(0).getAppId(), "djApp");
    assertEquals(entitlements.get(0).getAppName(), "Dow Jones");
    assertTrue(entitlements.get(0).getEnable());
  }

  @Test
  void listAppEntitlementsTestFailed() {
    mockApiClient.onGet(400, V1_LIST_APP_ENTITLEMENTS, "{}");

    assertThrows(ApiRuntimeException.class, this.service::listApplicationEntitlements);
  }

  @Test
  void updateAppEntitlementsTest() {
    mockApiClient.onPost(V1_UPDATE_APP_ENTITLEMENTS,
        "[\n"
            + "    {\n"
            + "        \"appId\": \"rsa-app-auth-example\",\n"
            + "        \"appName\": \"App Auth RSA Example\",\n"
            + "        \"enable\": true,\n"
            + "        \"listed\": true,\n"
            + "        \"install\": false\n"
            + "    }\n"
            + "]");
    PodAppEntitlement entitlement = new PodAppEntitlement()
        .appId("rsa-app-auth-example")
        .appName("App Auth RSA Example")
        .enable(true)
        .listed(true)
        .install(false);

    List<PodAppEntitlement> entitlements =
        this.service.updateApplicationEntitlements(Collections.singletonList(entitlement));

    assertEquals(entitlements.size(), 1);
    assertEquals(entitlements.get(0), entitlement);
  }

  @Test
  void updateAppEntitlementsTestFailed() {
    this.mockApiClient.onPost(400, V1_UPDATE_APP_ENTITLEMENTS, "{}");

    List<PodAppEntitlement> podAppEntitlements = Collections.singletonList(new PodAppEntitlement());
    assertThrows(ApiRuntimeException.class,
        () -> this.service.updateApplicationEntitlements(podAppEntitlements));
  }

  @Test
  void listUserApplicationsTest() throws IOException {
    this.mockApiClient.onGet(V1_USER_APPS.replace("{uid}", "1234"),
        JsonHelper.readFromClasspath("/application/user_apps.json"));

    List<UserAppEntitlement> entitlements = this.service.listUserApplications(1234L);

    assertEquals(entitlements.size(), 3);
    assertEquals("djApp", entitlements.get(0).getAppId());
    assertEquals("spcapiq", entitlements.get(1).getAppId());
    assertEquals(2, entitlements.get(2).getProducts().size());
  }

  @Test
  void listUserApplicationTestFailed() {
    this.mockApiClient.onGet(400, V1_USER_APPS.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.listUserApplications(1234L));
  }

  @Test
  void updateUserApplicationsTest() throws IOException {
    this.mockApiClient.onPost(V1_UPDATE_USER_APPS.replace("{uid}", "1234"),
        JsonHelper.readFromClasspath("/application/user_apps.json"));

    List<UserAppEntitlement> entitlements =
        this.service.updateUserApplications(1234L, Collections.singletonList(new UserAppEntitlement()));

    assertEquals(3, entitlements.size());
    assertEquals("djApp", entitlements.get(0).getAppId());
    assertEquals("spcapiq", entitlements.get(1).getAppId());
    assertEquals(2, entitlements.get(2).getProducts().size());
  }

  @Test
  void updateUserApplicationsTestFailed() {
    this.mockApiClient.onPost(400, V1_UPDATE_USER_APPS.replace("{uid}", "1234"), "{}");

    List<UserAppEntitlement> entitlements = Collections.singletonList(new UserAppEntitlement());
    assertThrows(ApiRuntimeException.class,
        () -> this.service.updateUserApplications(1234L, entitlements));
  }

  @Test
  void patchUserApplicationsTest() throws IOException {
    this.mockApiClient.onPatch(V1_PATCH_USER_APPS.replace("{uid}", "1234"),
        JsonHelper.readFromClasspath("/application/user_apps.json"));

    List<UserAppEntitlement> entitlements =
        this.service.patchUserApplications(1234L, Collections.singletonList(new UserAppEntitlementPatch()));

    assertEquals(3, entitlements.size());
    assertEquals("djApp", entitlements.get(0).getAppId());
    assertEquals("spcapiq", entitlements.get(1).getAppId());
    assertEquals(2, entitlements.get(2).getProducts().size());
  }

  @Test
  void patchUserApplicationsTestFailed() {
    this.mockApiClient.onPatch(400, V1_PATCH_USER_APPS.replace("{uid}", "1234"), "{}");

    List<UserAppEntitlementPatch> entitlements = Collections.singletonList(new UserAppEntitlementPatch());
    assertThrows(ApiRuntimeException.class,
        () -> this.service.patchUserApplications(1234L, entitlements));
  }
}
