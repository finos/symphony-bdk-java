package com.symphony.bdk.core.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.pagination.model.CursorPaginationAttribute;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.core.service.pagination.model.StreamPaginationAttribute;
import com.symphony.bdk.core.service.user.constant.RoleId;
import com.symphony.bdk.core.service.user.constant.UserFeature;
import com.symphony.bdk.core.service.user.mapper.UserDetailMapper;
import com.symphony.bdk.core.test.JsonHelper;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.gen.api.AuditTrailApi;
import com.symphony.bdk.gen.api.UserApi;
import com.symphony.bdk.gen.api.UsersApi;
import com.symphony.bdk.gen.api.model.Avatar;
import com.symphony.bdk.gen.api.model.AvatarUpdate;
import com.symphony.bdk.gen.api.model.DelegateAction;
import com.symphony.bdk.gen.api.model.Disclaimer;
import com.symphony.bdk.gen.api.model.Feature;
import com.symphony.bdk.gen.api.model.FollowersList;
import com.symphony.bdk.gen.api.model.FollowersListResponse;
import com.symphony.bdk.gen.api.model.FollowingListResponse;
import com.symphony.bdk.gen.api.model.RoleDetail;
import com.symphony.bdk.gen.api.model.StringId;
import com.symphony.bdk.gen.api.model.UserAttributes;
import com.symphony.bdk.gen.api.model.UserDetail;
import com.symphony.bdk.gen.api.model.UserFilter;
import com.symphony.bdk.gen.api.model.UserSearchFilter;
import com.symphony.bdk.gen.api.model.UserSearchQuery;
import com.symphony.bdk.gen.api.model.UserStatus;
import com.symphony.bdk.gen.api.model.UserSuspension;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V1AuditTrailInitiatorList;
import com.symphony.bdk.gen.api.model.V1AuditTrailInitiatorResponse;
import com.symphony.bdk.gen.api.model.V2UserAttributes;
import com.symphony.bdk.gen.api.model.V2UserCreate;
import com.symphony.bdk.gen.api.model.V2UserDetail;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class UserServiceTest {
  private static final String V2_USER_DETAIL_BY_ID = "/pod/v2/admin/user/{uid}";
  private static final String V2_USER_LIST = "/pod/v2/admin/user/list";
  private static final String V2_USER_CREATE = "/pod/v2/admin/user/create";
  private static final String V2_USER_UPDATE = "/pod/v2/admin/user/{uid}/update";
  private static final String USER_FIND = "/pod/v1/admin/user/find";
  private static final String ADD_ROLE_TO_USER = "/pod/v1/admin/user/{uid}/roles/add";
  private static final String REMOVE_ROLE_FROM_USER = "/pod/v1/admin/user/{uid}/roles/remove";
  private static final String LIST_ROLES = "/pod/v1/admin/system/roles/list";
  private static final String GET_AVATAR_FROM_USER = "/pod/v1/admin/user/{uid}/avatar";
  private static final String UPDATE_AVATAR_OF_USER = "/pod/v1/admin/user/{uid}/avatar/update";
  private static final String GET_DISCLAIMER_OF_USER = "/pod/v1/admin/user/{uid}/disclaimer";
  private static final String UNASSIGN_DISCLAIMER_FROM_USER = "/pod/v1/admin/user/{uid}/disclaimer";
  private static final String ASSIGN_DISCLAIMER_TO_USER = "/pod/v1/admin/user/{uid}/disclaimer/update";
  private static final String GET_DELEGATE_OF_USER = "/pod/v1/admin/user/{uid}/delegates";
  private static final String UPDATE_DELEGATE_OF_USER = "/pod/v1/admin/user/{uid}/delegates/update";
  private static final String GET_FEATURE_ENTITLEMENTS_OF_USER = "/pod/v1/admin/user/{uid}/features";
  private static final String UPDATE_FEATURE_ENTITLEMENTS_OF_USER = "/pod/v1/admin/user/{uid}/features/update";
  private static final String GET_STATUS_OF_USER = "/pod/v1/admin/user/{uid}/status";
  private static final String UPDATE_STATUS_OF_USER = "/pod/v1/admin/user/{uid}/status/update";
  private static final String SEARCH_USERS_V3 = "/pod/v3/users";
  private static final String SEARCH_USER_BY_QUERY = "/pod/v1/user/search";
  private static final String V1_USER_FOLLOW = "/pod/v1/user/{uid}/follow";
  private static final String V1_USER_UNFOLLOW = "/pod/v1/user/{uid}/unfollow";
  private static final String V1_LIST_FOLLOWERS = "/pod/v1/user/{uid}/followers";
  private static final String V1_LIST_FOLLOWING = "/pod/v1/user/{uid}/following";
  private static final String V1_AUDIT_TRAIL_PRIVILEGED_USER = "/agent/v1/audittrail/privilegeduser";
  private static final String SUSPEND_USER = "/pod/v1/admin/user/{uid}/suspension/update";
  private static final String MISSING_REQUIRED_PARAMETER_EXCEPTION_MESSAGE = "Missing the required parameter '%s' when calling %s";

  private UserService service;
  private UserApi spiedUserApi;
  private MockApiClient mockApiClient;
  private UsersApi spiedUsersApi;
  private AuditTrailApi spiedAuditTrailApi;
  private AuthSession authSession;

  @BeforeEach
  void init() {
    this.mockApiClient = new MockApiClient();
    this.authSession = mock(AuthSession.class);

    ApiClient podClient = mockApiClient.getApiClient("/pod");
    ApiClient agentClient = mockApiClient.getApiClient("/agent");
    this.spiedUserApi = spy(new UserApi(podClient));
    this.spiedUsersApi = spy(new UsersApi(podClient));
    this.spiedAuditTrailApi = spy(new AuditTrailApi(agentClient));
    this.service = new UserService(this.spiedUserApi, spiedUsersApi, this.spiedAuditTrailApi, authSession, new RetryWithRecoveryBuilder());

    when(authSession.getSessionToken()).thenReturn("1234");
    when(authSession.getKeyManagerToken()).thenReturn("1234");
  }

  @Test
  void nonOboEndpointShouldThrowExceptionInOboMode() {
    this.service = new UserService(this.spiedUserApi, spiedUsersApi, this.spiedAuditTrailApi, new RetryWithRecoveryBuilder());
    assertThrows(IllegalStateException.class, () -> this.service.getUserDetail(1234L));
  }

  @Test
  void listUsersByIdOboMode() throws IOException {
    String response = JsonHelper.readFromClasspath("/user/users.json");
    this.mockApiClient.onGet(SEARCH_USERS_V3, response);

    List<UserV2> users = this.service.obo(this.authSession).listUsersByIds(Collections.singletonList(1234L));
    assertEquals(users.size(), 1);
  }

  @Test
  void getUserDetailByUidTest() throws IOException {
    String response = JsonHelper.readFromClasspath("/user/user_detail.json");
    this.mockApiClient.onGet(V2_USER_DETAIL_BY_ID.replace("{uid}", "1234"), response);

    V2UserDetail userDetail = this.service.getUserDetail(1234L);

    assertEquals(userDetail.getUserAttributes().getCompanyName(), "Company");
    assertEquals(userDetail.getUserAttributes().getUserName(), "johndoe");
    assertEquals(V2UserAttributes.AccountTypeEnum.NORMAL, userDetail.getUserAttributes().getAccountType());
    assertEquals(userDetail.getRoles().size(), 6);
  }

  @Test
  void getUserDetailWithUnknownAccountType() throws IOException {
    String response = JsonHelper.readFromClasspath("/user/user_detail_unknown_account_type.json");
    this.mockApiClient.onGet(V2_USER_DETAIL_BY_ID.replace("{uid}", "1234"), response);

    V2UserDetail userDetail = this.service.getUserDetail(1234L);

    assertEquals(userDetail.getUserAttributes().getCompanyName(), "Company");
    assertEquals(userDetail.getUserAttributes().getUserName(), "johndoe");
    assertNull(userDetail.getUserAttributes().getAccountType());
    assertEquals(userDetail.getRoles().size(), 6);
  }

  @Test
  void listUsersDetailTest() throws IOException {
    String responseV2 = JsonHelper.readFromClasspath("/user/list_users_detail_v2.json");
    this.mockApiClient.onGet(V2_USER_LIST, responseV2);
    List<V2UserDetail> userDetails = this.service.listUsersDetail();

    assertEquals(userDetails.size(), 5);
    assertEquals(userDetails.get(0).getUserAttributes().getUserName(), "agentservice");
    assertEquals(userDetails.get(1).getUserAttributes().getUserName(), "bot.user1");
  }

  @Test
  void listUsersDetailTestFailed() {
    this.mockApiClient.onGet(400, V2_USER_LIST, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.listUsersDetail());
  }

  @Test
  void listUsersDetailSkipLimitTest() throws IOException {
    String responseV2 = JsonHelper.readFromClasspath("/user/list_users_detail_v2.json");
    this.mockApiClient.onGet(V2_USER_LIST, responseV2);
    List<V2UserDetail> userDetails = this.service.listUsersDetail(new PaginationAttribute(0, 100));

    assertEquals(userDetails.size(), 5);
    assertEquals(userDetails.get(0).getUserAttributes().getUserName(), "agentservice");
    assertEquals(userDetails.get(1).getUserAttributes().getUserName(), "bot.user1");
  }

  @Test
  void listAllUsersDetailTest() throws IOException {
    String responseV2 = JsonHelper.readFromClasspath("/user/list_users_detail_v2.json");
    this.mockApiClient.onGet(V2_USER_LIST, responseV2);
    List<V2UserDetail> userDetails = this.service.listAllUsersDetail().collect(Collectors.toList());

    assertEquals(userDetails.size(), 5);
    assertEquals(userDetails.get(0).getUserAttributes().getUserName(), "agentservice");
    assertEquals(userDetails.get(1).getUserAttributes().getUserName(), "bot.user1");
  }

  @Test
  void listAllUsersDetailPaginationTest() throws IOException {
    String responseV2 = JsonHelper.readFromClasspath("/user/list_users_detail_v2.json");
    this.mockApiClient.onGet(V2_USER_LIST, responseV2);
    List<V2UserDetail> userDetails =
        this.service.listAllUsersDetail(new StreamPaginationAttribute(100, 100)).collect(Collectors.toList());

    assertEquals(userDetails.size(), 5);
    assertEquals(userDetails.get(0).getUserAttributes().getUserName(), "agentservice");
    assertEquals(userDetails.get(1).getUserAttributes().getUserName(), "bot.user1");
  }

  @Test
  void listUsersDetailByFilterTest() throws IOException {
    String responseV1 = JsonHelper.readFromClasspath("/user/list_users_detail_v1.json");

    this.mockApiClient.onPost(USER_FIND, responseV1);
    UserFilter userFilter = new UserFilter();
    List<V2UserDetail> userDetails = this.service.listUsersDetail(userFilter);

    assertEquals(userDetails.size(), 4);
    assertEquals(userDetails.get(2).getUserAttributes().getUserName(), "bot.user");
    assertEquals(userDetails.get(3).getUserAttributes().getUserName(), "nexus.user");
  }

  @Test
  void listUsersDetailByFilterTestFailed() {
    this.mockApiClient.onPost(400, USER_FIND, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.listUsersDetail(new UserFilter()));
  }

  @Test
  void listUsersDetailByFilterSkipLimitTest() throws IOException {
    String responseV1 = JsonHelper.readFromClasspath("/user/list_users_detail_v1.json");

    this.mockApiClient.onPost(USER_FIND, responseV1);
    UserFilter userFilter = new UserFilter();
    List<V2UserDetail> userDetails = this.service.listUsersDetail(userFilter, new PaginationAttribute(0, 100));

    assertEquals(userDetails.size(), 4);
    assertEquals(userDetails.get(2).getUserAttributes().getUserName(), "bot.user");
    assertEquals(userDetails.get(3).getUserAttributes().getUserName(), "nexus.user");
  }

  @Test
  void listAllUsersDetailByFilterTest() throws IOException {
    String responseV1 = JsonHelper.readFromClasspath("/user/list_users_detail_v1.json");

    this.mockApiClient.onPost(USER_FIND, responseV1);
    UserFilter userFilter = new UserFilter();
    List<V2UserDetail> userDetails = this.service.listAllUsersDetail(userFilter).collect(Collectors.toList());

    assertEquals(userDetails.size(), 4);
    assertEquals(userDetails.get(2).getUserAttributes().getUserName(), "bot.user");
    assertEquals(userDetails.get(3).getUserAttributes().getUserName(), "nexus.user");
  }

  @Test
  void listAllUsersDetailByFilterPaginationTest() throws IOException {
    String responseV1 = JsonHelper.readFromClasspath("/user/list_users_detail_v1.json");

    this.mockApiClient.onPost(USER_FIND, responseV1);
    UserFilter userFilter = new UserFilter();
    List<V2UserDetail> userDetails =
        this.service.listAllUsersDetail(userFilter, new StreamPaginationAttribute(100, 100))
            .collect(Collectors.toList());

    assertEquals(userDetails.size(), 4);
    assertEquals(userDetails.get(2).getUserAttributes().getUserName(), "bot.user");
    assertEquals(userDetails.get(3).getUserAttributes().getUserName(), "nexus.user");
  }

  @Test
  void addRoleToUserTest() throws ApiException {
    this.mockApiClient.onPost(ADD_ROLE_TO_USER.replace("{uid}", "1234"),
        "{\"format\": \"TEXT\", \"message\": \"Role added\"}");

    this.service.addRole(1234L, RoleId.INDIVIDUAL);

    verify(spiedUserApi).v1AdminUserUidRolesAddPost(eq("1234"), eq(1234L), eq(new StringId().id("INDIVIDUAL")));
  }

  @Test
  void addRoleToUserTestFailed() {
    this.mockApiClient.onPost(400, ADD_ROLE_TO_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.addRole(1234L, RoleId.INDIVIDUAL));
  }

  @Test
  void removeRoleFromUserTest() throws ApiException {
    this.mockApiClient.onPost(REMOVE_ROLE_FROM_USER.replace("{uid}", "1234"),
        "{\"format\": \"TEXT\", \"message\": \"Role removed\"}");

    this.service.removeRole(1234L, RoleId.INDIVIDUAL);

    verify(spiedUserApi).v1AdminUserUidRolesRemovePost(eq("1234"), eq(1234L), eq(new StringId().id("INDIVIDUAL")));
  }

  @Test
  void removeRoleFromUserTestFailed() {
    this.mockApiClient.onPost(400, REMOVE_ROLE_FROM_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.removeRole(1234L, RoleId.INDIVIDUAL));
  }

  @Test
  void listRoles() throws IOException {
    String response = JsonHelper.readFromClasspath("/roles/list_roles.json");
    this.mockApiClient.onGet(LIST_ROLES, response);
    List<RoleDetail> roleDetails = this.service.listRoles();

    assertEquals(12, roleDetails.size());
    assertEquals("Content Management", roleDetails.get(0).getName());
  }


  @Test
  void listRolesTestFailed() {
    this.mockApiClient.onGet(400, LIST_ROLES, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.listRoles());
  }

  @Test
  void getAvatarFromUser() {
    this.mockApiClient.onGet(GET_AVATAR_FROM_USER.replace("{uid}", "1234"), "[\n"
        + "  {\n"
        + "    \"size\": \"600\",\n"
        + "    \"url\": \"../avatars/acme/600/7215545057281/3gXMhglCCTwLPL9JAprnyHzYn5-PR49-wYDG814n1g8.png\"\n"
        + "  },\n"
        + "  {\n"
        + "    \"size\": \"150\",\n"
        + "    \"url\": \"../avatars/acme/150/7215545057281/3gXMhglCCTwLPL9JAprnyHzYn5-PR49-wYDG814n1g8.png\"\n"
        + "  }\n"
        + "]");

    List<Avatar> avatars = this.service.getAvatar(1234L);

    assertEquals(avatars.size(), 2);
    assertEquals(avatars.get(0).getSize(), "600");
    assertEquals(avatars.get(1).getSize(), "150");
  }

  @Test
  void getAvatarFromUserTestFailed() {
    this.mockApiClient.onGet(400, GET_AVATAR_FROM_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.getAvatar(1234L));
  }

  @Test
  void updateAvatarOfUserTest() throws ApiException, IOException {
    this.mockApiClient.onPost(UPDATE_AVATAR_OF_USER.replace("{uid}", "1234"),
        "{\"format\": \"TEXT\", \"message\": \"OK\"}");
    String avatar = "iVBORw0KGgoAAAANSUhEUgAAAJgAAAAoCAMAAAA11s";
    byte[] bytes = avatar.getBytes();
    InputStream inputStream = new ByteArrayInputStream(bytes);

    this.service.updateAvatar(1234L, avatar);
    this.service.updateAvatar(1234L, bytes);
    this.service.updateAvatar(1234L, inputStream);

    verify(spiedUserApi).v1AdminUserUidAvatarUpdatePost(eq("1234"), eq(1234L),
        eq(new AvatarUpdate().image("iVBORw0KGgoAAAANSUhEUgAAAJgAAAAoCAMAAAA11s")));
    verify(spiedUserApi, times(2)).v1AdminUserUidAvatarUpdatePost(eq("1234"), eq(1234L),
        eq(new AvatarUpdate().image(Base64.getEncoder().encodeToString(bytes))));

  }

  @Test
  void updateAvatarOfUserTestFailed() {
    this.mockApiClient.onPost(400, UPDATE_AVATAR_OF_USER.replace("{uid}", "1234"), "{}");
    String avatar = "iVBORw0KGgoAAAANSUhEUgAAAJgAAAAoCAMAAAA11s";
    byte[] bytes = avatar.getBytes();
    InputStream inputStream = new ByteArrayInputStream(bytes);

    assertThrows(ApiRuntimeException.class, () -> this.service.updateAvatar(1234L, avatar));
    assertThrows(ApiRuntimeException.class, () -> this.service.updateAvatar(1234L, bytes));
    assertThrows(ApiRuntimeException.class, () -> this.service.updateAvatar(1234L, inputStream));
  }

  @Test
  void getDisclaimerAssignedToUserTest() {
    this.mockApiClient.onGet(GET_DISCLAIMER_OF_USER.replace("{uid}", "1234"), "{\n"
        + "  \"id\": \"571d2052e4b042aaf06d2e7a\",\n"
        + "  \"name\": \"Enterprise Disclaimer\",\n"
        + "  \"content\": \"This is a disclaimer for the enterprise.\",\n"
        + "  \"frequencyInHours\": 24,\n"
        + "  \"isDefault\": false,\n"
        + "  \"isActive\": true,\n"
        + "  \"createdDate\": 1461526610846,\n"
        + "  \"modifiedDate\": 1461526610846\n"
        + "}");

    Disclaimer disclaimer = this.service.getDisclaimer(1234L);
    assertEquals(disclaimer.getName(), "Enterprise Disclaimer");
    assertEquals(disclaimer.getIsActive(), true);
    assertEquals(disclaimer.getIsDefault(), false);
  }

  @Test
  void getDisclaimerAssignedToUserTestFailed() {
    this.mockApiClient.onGet(400, GET_DISCLAIMER_OF_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.getDisclaimer(1234L));
  }

  @Test
  void unAssignDisclaimerFromUserTest() throws ApiException {
    this.mockApiClient.onDelete(UNASSIGN_DISCLAIMER_FROM_USER.replace("{uid}", "1234"), "{}");

    this.service.removeDisclaimer(1234L);

    verify(spiedUserApi).v1AdminUserUidDisclaimerDelete("1234", 1234L);
  }

  @Test
  void unAssignDisclaimerFromUserTestFailed() {
    this.mockApiClient.onDelete(400, UNASSIGN_DISCLAIMER_FROM_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.removeDisclaimer(1234L));
  }

  @Test
  void assignDisclaimerToUserTest() throws ApiException {
    this.mockApiClient.onPost(ASSIGN_DISCLAIMER_TO_USER.replace("{uid}", "1234"), "{}");

    this.service.addDisclaimer(1234L, "disclaimer");

    verify(spiedUserApi).v1AdminUserUidDisclaimerUpdatePost(eq("1234"), eq(1234L), eq(new StringId().id("disclaimer")));
  }

  @Test
  void assignDisclaimerToUserTestFailed() {
    this.mockApiClient.onPost(400, ASSIGN_DISCLAIMER_TO_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.addDisclaimer(1234L, "disclaimer"));
  }

  @Test
  void getDelegatesAssignedToUserTest() {
    this.mockApiClient.onGet(GET_DELEGATE_OF_USER.replace("{uid}", "1234"), "[7215545078461]");

    List<Long> delegates = this.service.getDelegates(1234L);

    assertEquals(delegates.size(), 1);
    assertEquals(delegates.get(0), 7215545078461L);
  }

  @Test
  void getDelegatesAssignedToUserTestFailed() {
    this.mockApiClient.onGet(400, GET_DELEGATE_OF_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.getDelegates(1234L));
  }

  @Test
  void updateDelegatesAssignedToUserTest() throws ApiException {
    this.mockApiClient.onPost(UPDATE_DELEGATE_OF_USER.replace("{uid}", "1234"), "{}");

    this.service.udpateDelegates(1234L, 1234L, DelegateAction.ActionEnum.ADD);

    verify(spiedUserApi).v1AdminUserUidDelegatesUpdatePost(
        eq("1234"),
        eq(1234L),
        eq(new DelegateAction().action(DelegateAction.ActionEnum.ADD).userId(1234L)));
  }

  @Test
  void updateDelegatesAssignedToUserTestFailed() {
    this.mockApiClient.onPost(400, UPDATE_DELEGATE_OF_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class,
        () -> this.service.udpateDelegates(1234L, 1234L, DelegateAction.ActionEnum.ADD));
  }

  @Test
  void getFeatureEntitlementsOfUserTest() {
    this.mockApiClient.onGet(GET_FEATURE_ENTITLEMENTS_OF_USER.replace("{uid}", "1234"), "[\n"
        + "  {\n"
        + "    \"entitlment\": \"canCreatePublicRoom\",\n"
        + "    \"enabled\": true\n"
        + "  },\n"
        + "  {\n"
        + "    \"entitlment\": \"isExternalRoomEnabled\",\n"
        + "    \"enabled\": false\n"
        + "  },\n"
        + "  {\n"
        + "    \"entitlment\": \"delegatesEnabled\",\n"
        + "    \"enabled\": true\n"
        + "  },\n"
        + "  {\n"
        + "    \"entitlment\": \"isExternalIMEnabled\",\n"
        + "    \"enabled\": true\n"
        + "  }\n"
        + "]");

    List<Feature> features = this.service.getFeatureEntitlements(1234L);

    assertEquals(features.size(), 4);
    assertEquals(features.get(0).getEntitlment(), UserFeature.canCreatePublicRoom.name());
    assertEquals(features.get(1).getEnabled(), false);
  }

  @Test
  void getFeatureEntitlementsOfUserTestFailed() {
    this.mockApiClient.onGet(400, GET_FEATURE_ENTITLEMENTS_OF_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.getFeatureEntitlements(1234L));
  }

  @Test
  void updateFeatureEntitlementsOfUserTest() throws ApiException {
    this.mockApiClient.onPost(UPDATE_FEATURE_ENTITLEMENTS_OF_USER.replace("{uid}", "1234"), "{}");
    List<Feature> features = Collections.singletonList(new Feature().entitlment("delegatesEnabled").enabled(true));

    this.service.updateFeatureEntitlements(1234L, features);

    verify(spiedUserApi).v1AdminUserUidFeaturesUpdatePost("1234", 1234L, features);
  }

  @Test
  void updateFeatureEntitlementsOfUserTestFailed() {
    this.mockApiClient.onPost(400, UPDATE_FEATURE_ENTITLEMENTS_OF_USER.replace("{uid}", "1234"),
        "{}");
    List<Feature> features = Collections.singletonList(new Feature().entitlment("delegatesEnabled").enabled(true));
    assertThrows(ApiRuntimeException.class, () -> this.service.updateFeatureEntitlements(1234L, features));
  }

  @Test
  void getStatusOfUserTest() {
    this.mockApiClient.onGet(GET_STATUS_OF_USER.replace("{uid}", "1234"), "{\"status\": \"ENABLED\"}");

    UserStatus userStatus = this.service.getStatus(1234L);

    assertEquals(userStatus.getStatus(), UserStatus.StatusEnum.ENABLED);
  }

  @Test
  void getStatusOfUserTestFailed() {
    this.mockApiClient.onGet(400, GET_STATUS_OF_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.getStatus(1234L));
  }

  @Test
  void updateStatusOfUserTest() throws ApiException {
    this.mockApiClient.onPost(UPDATE_STATUS_OF_USER.replace("{uid}", "1234"), "{}");
    UserStatus userStatus = new UserStatus().status(UserStatus.StatusEnum.ENABLED);

    this.service.updateStatus(1234L, userStatus);

    verify(spiedUserApi).v1AdminUserUidStatusUpdatePost("1234", 1234L, userStatus);
  }

  @Test
  void updateStatusOfUserTestFailed() {
    this.mockApiClient.onPost(400, UPDATE_STATUS_OF_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.updateStatus(1234L,
        new UserStatus().status(UserStatus.StatusEnum.ENABLED)));
  }

  @Test
  void searchUserV3Test() throws IOException {
    String response = JsonHelper.readFromClasspath("/user/users.json");
    this.mockApiClient.onGet(SEARCH_USERS_V3, response);

    List<UserV2> users1 = this.service.listUsersByIds(Collections.singletonList(1234L), true, true);

    assertEquals(users1.size(), 1);
    assertEquals(users1.get(0).getDisplayName(), "Test Bot");

    List<UserV2> users2 = this.service.listUsersByEmails(Collections.singletonList("tibot@symphony.com"), true, true);

    assertEquals(users2.size(), 1);
    assertEquals(users2.get(0).getUsername(), "tibot");

    List<UserV2> users3 = this.service.listUsersByUsernames(Collections.singletonList("tibot"), true);

    assertEquals(users3.size(), 1);
    assertEquals(users3.get(0).getId(), 1234L);

    List<UserV2> users4 = this.service.listUsersByIds(Collections.singletonList(1234L));

    assertEquals(users4.size(), 1);
    assertEquals(users4.get(0).getId(), 1234L);

    List<UserV2> users5 = this.service.listUsersByEmails(Collections.singletonList("tibot@symphony.com"));

    assertEquals(users5.size(), 1);
    assertEquals(users5.get(0).getId(), 1234L);

    List<UserV2> users6 = this.service.listUsersByUsernames(Collections.singletonList("tibot"));

    assertEquals(users6.size(), 1);
    assertEquals(users6.get(0).getId(), 1234L);
  }

  @Test
  void searchUserV3TestFailed() {
    this.mockApiClient.onGet(400, SEARCH_USERS_V3, "{}");

    assertThrows(ApiRuntimeException.class,
        () -> this.service.listUsersByIds(Collections.singletonList(1234L), true, true));
    this.mockApiClient.onGet(400, SEARCH_USERS_V3, "{}");
    assertThrows(ApiRuntimeException.class,
        () -> this.service.listUsersByEmails(Collections.singletonList("tibot@symphony.com"), true, true));
    assertThrows(ApiRuntimeException.class,
        () -> this.service.listUsersByUsernames(Collections.singletonList("tibot"), true));
    assertThrows(ApiRuntimeException.class, () -> this.service.listUsersByIds(Collections.singletonList(1234L)));
    assertThrows(ApiRuntimeException.class,
        () -> this.service.listUsersByEmails(Collections.singletonList("tibot@symphony.com")));
    assertThrows(ApiRuntimeException.class,
        () -> this.service.listUsersByUsernames(Collections.singletonList("tibot")));
  }

  @Test
  void searchUserV3ByIds_withParams_Content() {
    this.mockApiClient.onGet(SEARCH_USERS_V3, "{}");
    assertEquals(Collections.emptyList(),
        this.service.listUsersByIds(Collections.singletonList(1234L), true, true));
  }

  @Test
  void searchUserV3ByIds_noParams_noContent() {
    this.mockApiClient.onGet(SEARCH_USERS_V3, "{}");
    assertEquals(Collections.emptyList(),
        this.service.listUsersByIds(Collections.singletonList(1234L)));
  }

  @Test
  void searchUserV3ByEmails_withParams_noContent() {
    this.mockApiClient.onGet(SEARCH_USERS_V3, "{}");
    assertEquals(Collections.emptyList(),
        this.service.listUsersByEmails(Collections.singletonList("x@x.com"), true, true));
  }

  @Test
  void searchUserV3ByUsernames_noParams_noContent() {
    this.mockApiClient.onGet(SEARCH_USERS_V3, "{}");
    assertEquals(Collections.emptyList(),
        this.service.listUsersByEmails(Collections.singletonList("user-name")));
  }

  @Test
  void searchUserV3ByUsernames_withParams_noContent() {
    this.mockApiClient.onGet(SEARCH_USERS_V3, "{}");
    assertEquals(Collections.emptyList(),
        this.service.listUsersByUsernames(Collections.singletonList("user-name"), true));
  }

  @Test
  void searchUserV3ByEmails_noParams_noContent() {
    this.mockApiClient.onGet(SEARCH_USERS_V3, "{}");
    assertEquals(Collections.emptyList(),
        this.service.listUsersByUsernames(Collections.singletonList("x@x.com")));
  }

  @Test
  void searchUserBySearchQueryTest() throws IOException {
    String response = JsonHelper.readFromClasspath("/user/users_by_query.json");
    this.mockApiClient.onPost(SEARCH_USER_BY_QUERY, response);

    UserSearchQuery query = new UserSearchQuery().query("john doe")
        .filters(new UserSearchFilter().title("title").company("Gotham").location("New York"));

    List<UserV2> users = this.service.searchUsers(query, true);

    assertEquals(users.size(), 1);
    assertEquals(users.get(0).getUsername(), "john.doe");
    assertEquals(users.get(0).getDisplayName(), "John Doe");
  }

  @Test
  void searchUserBySearchQuerySkipLimitTest() throws IOException {
    String response = JsonHelper.readFromClasspath("/user/users_by_query.json");
    this.mockApiClient.onPost(SEARCH_USER_BY_QUERY, response);

    UserSearchQuery query = new UserSearchQuery().query("john doe")
        .filters(new UserSearchFilter().title("title").company("Gotham").location("New York"));

    List<UserV2> users = this.service.searchUsers(query, true, new PaginationAttribute(0, 100));

    assertEquals(users.size(), 1);
    assertEquals(users.get(0).getUsername(), "john.doe");
    assertEquals(users.get(0).getDisplayName(), "John Doe");
  }

  @Test
  void searchAllUserBySearchQueryTest() throws IOException {
    String response = JsonHelper.readFromClasspath("/user/users_by_query.json");
    this.mockApiClient.onPost(SEARCH_USER_BY_QUERY, response);

    UserSearchQuery query = new UserSearchQuery().query("john doe")
        .filters(new UserSearchFilter().title("title").company("Gotham").location("New York"));

    List<UserV2> users = this.service.searchAllUsers(query, true).collect(Collectors.toList());

    assertEquals(users.size(), 1);
    assertEquals(users.get(0).getUsername(), "john.doe");
    assertEquals(users.get(0).getDisplayName(), "John Doe");
  }

  @Test
  void searchAllUserBySearchQueryPaginationTest() throws IOException {
    String response = JsonHelper.readFromClasspath("/user/users_by_query.json");
    this.mockApiClient.onPost(SEARCH_USER_BY_QUERY, response);

    UserSearchQuery query = new UserSearchQuery().query("john doe")
        .filters(new UserSearchFilter().title("title").company("Gotham").location("New York"));

    List<UserV2> users = this.service.searchAllUsers(query, true, new StreamPaginationAttribute(100, 100))
        .collect(Collectors.toList());

    assertEquals(users.size(), 1);
    assertEquals(users.get(0).getUsername(), "john.doe");
    assertEquals(users.get(0).getDisplayName(), "John Doe");
  }

  @Test
  void followUserTest() throws ApiException {
    this.mockApiClient.onPost(V1_USER_FOLLOW.replace("{uid}", "1234"), "{}");

    this.service.followUser(Arrays.asList(12345L, 12346L), 1234L);

    verify(this.spiedUserApi).v1UserUidFollowPost(eq("1234"), eq(1234L),
        eq(new FollowersList().followers(Arrays.asList(12345L, 12346L))));
  }

  @Test
  void followUserFailed() {
    this.mockApiClient.onPost(400, V1_USER_FOLLOW.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.followUser(Collections.singletonList(12345L), 1234L));
  }

  @Test
  void unfollowUserTest() throws ApiException {
    this.mockApiClient.onPost(V1_USER_UNFOLLOW.replace("{uid}", "1234"), "{}");

    this.service.unfollowUser(Arrays.asList(12345L, 12346L), 1234L);

    verify(this.spiedUserApi).v1UserUidUnfollowPost(eq("1234"), eq(1234L),
        eq(new FollowersList().followers(Arrays.asList(12345L, 12346L))));
  }

  @Test
  void unfollowFailed() {
    this.mockApiClient.onPost(400, V1_USER_UNFOLLOW.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.unfollowUser(Collections.singletonList(12345L), 1234L));
  }

  @Test
  void listUserFollowersTest() {
    this.mockApiClient.onGet(V1_LIST_FOLLOWERS.replace("{uid}", "1234"),
        "{\n"
            + "    \"count\": 3,\n"
            + "    \"followers\": [\n"
            + "        13056700579848,\n"
            + "        13056700580889,\n"
            + "        13056700580890\n"
            + "    ],\n"
            + "    \"pagination\": {\n"
            + "        \"cursors\": {\n"
            + "            \"before\": \"1\",\n"
            + "            \"after\": \"4\"\n"
            + "        }\n"
            + "    }\n"
            + "}");

    FollowersListResponse response = this.service.listUserFollowers(1234L);

    assertEquals(response.getCount(), 3);
    assertEquals(response.getFollowers().get(0), 13056700579848L);
    assertEquals(response.getFollowers().get(1), 13056700580889L);
    assertEquals(response.getFollowers().get(2), 13056700580890L);
  }

  @Test
  void listUserFollowersPaginationTest() {
    this.mockApiClient.onGet(V1_LIST_FOLLOWERS.replace("{uid}", "1234"),
        "{\n"
            + "    \"count\": 3,\n"
            + "    \"followers\": [\n"
            + "        13056700579848,\n"
            + "        13056700580889,\n"
            + "        13056700580890\n"
            + "    ],\n"
            + "    \"pagination\": {\n"
            + "        \"cursors\": {\n"
            + "            \"before\": \"1\",\n"
            + "            \"after\": \"4\"\n"
            + "        }\n"
            + "    }\n"
            + "}");

    FollowersListResponse response = this.service.listUserFollowers(1234L, new CursorPaginationAttribute(4, 1, 5));

    assertEquals(response.getCount(), 3);
    assertEquals(response.getFollowers().get(0), 13056700579848L);
    assertEquals(response.getFollowers().get(1), 13056700580889L);
    assertEquals(response.getFollowers().get(2), 13056700580890L);
  }

  @Test
  void listAllUserFollowersTest() {
    this.mockApiClient.onGet(V1_LIST_FOLLOWERS.replace("{uid}", "1234"), "{\n"
        + "    \"count\": 3,\n"
        + "    \"followers\": [\n"
        + "        13056700579848,\n"
        + "        13056700580889,\n"
        + "        13056700580890\n"
        + "    ],\n"
        + "    \"pagination\": {\n"
        + "        \"cursors\": {\n"
        + "            \"before\": \"1\"\n"
        + "        }\n"
        + "    }\n"
        + "}");

    List<Long> followers = this.service.listAllUserFollowers(1234L).collect(Collectors.toList());

    assertEquals(followers.size(), 3);
    assertEquals(followers.get(0), 13056700579848L);
    assertEquals(followers.get(1), 13056700580889L);
    assertEquals(followers.get(2), 13056700580890L);
  }

  @Test
  void listAllUserFollowersNoPaginationTest() {
    this.mockApiClient.onGet(V1_LIST_FOLLOWERS.replace("{uid}", "1234"), "{\n"
        + "    \"count\": 3,\n"
        + "    \"followers\": [\n"
        + "        13056700579848,\n"
        + "        13056700580889,\n"
        + "        13056700580890\n"
        + "    ]\n"
        + "}");

    List<Long> followers = this.service.listAllUserFollowers(1234L).collect(Collectors.toList());

    assertEquals(followers.size(), 3);
    assertEquals(followers.get(0), 13056700579848L);
    assertEquals(followers.get(1), 13056700580889L);
    assertEquals(followers.get(2), 13056700580890L);
  }

  @Test
  void listAllUserFollowersPaginationTest() {
    this.mockApiClient.onGet(V1_LIST_FOLLOWERS.replace("{uid}", "1234"), "{\n"
        + "    \"count\": 3,\n"
        + "    \"followers\": [\n"
        + "        13056700579848,\n"
        + "        13056700580889,\n"
        + "        13056700580890\n"
        + "    ],\n"
        + "    \"pagination\": {\n"
        + "        \"cursors\": {\n"
        + "            \"before\": \"1\"\n"
        + "        }\n"
        + "    }\n"
        + "}");

    List<Long> followers = this.service.listAllUserFollowers(1234L, new StreamPaginationAttribute(10, 10)).collect(Collectors.toList());

    assertEquals(followers.size(), 3);
    assertEquals(followers.get(0), 13056700579848L);
    assertEquals(followers.get(1), 13056700580889L);
    assertEquals(followers.get(2), 13056700580890L);
  }

  @Test
  void listUserFollowingTest() {
    this.mockApiClient.onGet(V1_LIST_FOLLOWING.replace("{uid}", "1234"),
        "{\n"
            + "    \"count\": 2,\n"
            + "    \"following\": [\n"
            + "        13056700580888,\n"
            + "        13056700580889\n"
            + "    ],\n"
            + "    \"pagination\": {\n"
            + "        \"cursors\": {\n"
            + "            \"before\": \"1\"\n"
            + "        }\n"
            + "    }\n"
            + "}");

    FollowingListResponse response = this.service.listUsersFollowing(1234L);

    assertEquals(response.getCount(), 2);
    assertEquals(response.getFollowing().get(0), 13056700580888L);
    assertEquals(response.getFollowing().get(1), 13056700580889L);
  }

  @Test
  void listUserFollowingPaginationTest() {
    this.mockApiClient.onGet(V1_LIST_FOLLOWING.replace("{uid}", "1234"),
        "{\n"
            + "    \"count\": 2,\n"
            + "    \"following\": [\n"
            + "        13056700580888,\n"
            + "        13056700580889\n"
            + "    ],\n"
            + "    \"pagination\": {\n"
            + "        \"cursors\": {\n"
            + "            \"before\": \"1\"\n"
            + "        }\n"
            + "    }\n"
            + "}");

    FollowingListResponse response = this.service.listUsersFollowing(1234L, new CursorPaginationAttribute(4, 1, 5));

    assertEquals(response.getCount(), 2);
    assertEquals(response.getFollowing().get(0), 13056700580888L);
    assertEquals(response.getFollowing().get(1), 13056700580889L);
  }

  @Test
  void listAllUserFollowingTest() {
    this.mockApiClient.onGet(V1_LIST_FOLLOWING.replace("{uid}", "1234"),
        "{\n"
            + "    \"count\": 2,\n"
            + "    \"following\": [\n"
            + "        13056700580888,\n"
            + "        13056700580889\n"
            + "    ],\n"
            + "    \"pagination\": {\n"
            + "        \"cursors\": {\n"
            + "            \"before\": \"1\"\n"
            + "        }\n"
            + "    }\n"
            + "}");

    List<Long> followers = this.service.listAllUserFollowing(1234L).collect(Collectors.toList());

    assertEquals(followers.size(), 2);
    assertEquals(followers.get(0), 13056700580888L);
    assertEquals(followers.get(1), 13056700580889L);
  }

  @Test
  void listAllUserFollowingsPaginationTest() {
    this.mockApiClient.onGet(V1_LIST_FOLLOWING.replace("{uid}", "1234"),
        "{\n"
            + "    \"count\": 2,\n"
            + "    \"following\": [\n"
            + "        13056700580888,\n"
            + "        13056700580889\n"
            + "    ],\n"
            + "    \"pagination\": {\n"
            + "        \"cursors\": {\n"
            + "            \"before\": \"1\"\n"
            + "        }\n"
            + "    }\n"
            + "}");

    List<Long> followers = this.service.listAllUserFollowing(1234L, new StreamPaginationAttribute(10, 10)).collect(Collectors.toList());

    assertEquals(followers.size(), 2);
    assertEquals(followers.get(0), 13056700580888L);
    assertEquals(followers.get(1), 13056700580889L);
  }

  @Test
  void listAllUserFollowingsPaginationNoPaginationTest() {
    this.mockApiClient.onGet(V1_LIST_FOLLOWING.replace("{uid}", "1234"),
        "{\n"
            + "    \"count\": 2,\n"
            + "    \"following\": [\n"
            + "        13056700580888,\n"
            + "        13056700580889\n"
            + "    ]\n"
            + "}");

    List<Long> followers = this.service.listAllUserFollowing(1234L, new StreamPaginationAttribute(10, 10)).collect(Collectors.toList());

    assertEquals(followers.size(), 2);
    assertEquals(followers.get(0), 13056700580888L);
    assertEquals(followers.get(1), 13056700580889L);
  }

  @Test
  void userDetailMapperNullTest() {
    V2UserDetail userDetailNull = UserDetailMapper.INSTANCE.userDetailToV2UserDetail(null);

    assertNull(userDetailNull);

    V2UserDetail userDetail = UserDetailMapper.INSTANCE.userDetailToV2UserDetail(new UserDetail());

    assertNull(userDetail.getUserAttributes());

    V2UserDetail userDetail1 = UserDetailMapper.INSTANCE.userDetailToV2UserDetail(
        new UserDetail().userAttributes(new UserAttributes().accountType(null)));
    assertNull(userDetail1.getUserAttributes().getAccountType());
  }

  @Test
  void createUser() throws IOException {
    String responseV2 = JsonHelper.readFromClasspath("/user/user_detail.json");
    this.mockApiClient.onPost(V2_USER_CREATE, responseV2);
    V2UserDetail userDetails = this.service.create(new V2UserCreate());

    assertEquals(userDetails.getUserAttributes().getUserName(), "johndoe");
  }

  @Test
  void createUserMissingPayload() {
    Exception exception = assertThrows(ApiRuntimeException.class, () -> this.service.create(null));
    assertTrue(exception.getMessage().contains(
        String.format(MISSING_REQUIRED_PARAMETER_EXCEPTION_MESSAGE, "payload", "v2AdminUserCreatePost")));
  }

  @Test
  void updateUser() throws IOException {
    String responseV2 = JsonHelper.readFromClasspath("/user/user_detail.json");
    responseV2 = responseV2.replace("\"userName\": \"johndoe\"", "\"userName\": \"johndoe-UPDATED\"");
    Long userId = 7215545078461L;

    this.mockApiClient.onPost(V2_USER_UPDATE.replace("{uid}", userId+""), responseV2);
    V2UserDetail userDetails = this.service.update(userId, new V2UserAttributes());

    assertEquals(userDetails.getUserAttributes().getUserName(), "johndoe-UPDATED");
  }

  @Test
  void updateUserMissingPayload() {
    this.mockApiClient.onPost(V2_USER_UPDATE.replace("{uid}", "123L"), "resContent");
    Exception exception = assertThrows(ApiRuntimeException.class, () -> this.service.update(123L, null));

    assertTrue(exception.getMessage().contains(
        String.format(MISSING_REQUIRED_PARAMETER_EXCEPTION_MESSAGE, "payload", "v2AdminUserUidUpdatePost")));
  }

  @Test
  void updateUserMissingUserId() {
    this.mockApiClient.onPost(V2_USER_UPDATE.replace("{uid}", "123L"), "resContent");
    Exception exception = assertThrows(ApiRuntimeException.class, () -> this.service.update(null, new V2UserAttributes()));

    assertTrue(exception.getMessage().contains(
        String.format(MISSING_REQUIRED_PARAMETER_EXCEPTION_MESSAGE, "uid", "v2AdminUserUidUpdatePost")));
  }

  @Test
  void listAuditTrail() throws IOException {
    String response = JsonHelper.readFromClasspath("/audit_trail/audit_trail_initiator_list_v1.json");
    this.mockApiClient.onGet(V1_AUDIT_TRAIL_PRIVILEGED_USER, response);

    V1AuditTrailInitiatorList v1AuditTrailInitiatorList = this.service.listAuditTrail(1551888601279L, 1551888601279L,
        new CursorPaginationAttribute(1,3,2),1353716993L, "SUPER_ADMINISTRATOR");

    assertEquals(v1AuditTrailInitiatorList.getItems().size(), 2);
    assertEquals(v1AuditTrailInitiatorList.getPagination(), null);
  }

  @Test
  void listAllAuditTrailNoPagination() throws IOException {
    String response = JsonHelper.readFromClasspath("/audit_trail/audit_trail_initiator_list_v1.json");
    this.mockApiClient.onGet(V1_AUDIT_TRAIL_PRIVILEGED_USER, response);

    final List<V1AuditTrailInitiatorResponse> auditTrails =
        this.service.listAllAuditTrail(1551888601279L, 1551888601279L, 1353716993L, "SUPER_ADMINISTRATOR", 2, 3)
            .collect(Collectors.toList());

    assertEquals(auditTrails.size(), 2);
  }

  @Test
  void listAllAuditTrail() throws IOException {
    String response = JsonHelper.readFromClasspath("/audit_trail/audit_trail_initiator_list_v1_pagination.json");
    this.mockApiClient.onGet(V1_AUDIT_TRAIL_PRIVILEGED_USER, response);

    final List<V1AuditTrailInitiatorResponse> auditTrails =
        this.service.listAllAuditTrail(1551888601279L, 1551888601279L, 1353716993L, "SUPER_ADMINISTRATOR", 2, 3)
            .collect(Collectors.toList());

    assertEquals(auditTrails.size(), 2);
  }

  @Test
  void listAuditTrailOnlyRequiredParams() throws IOException {
    String response = JsonHelper.readFromClasspath("/audit_trail/audit_trail_initiator_list_v1.json");
    this.mockApiClient.onGet(V1_AUDIT_TRAIL_PRIVILEGED_USER, response);

    V1AuditTrailInitiatorList v1AuditTrailInitiatorList = this.service.listAuditTrail(1551888601279L, null,
        null, null, null);

    assertEquals(v1AuditTrailInitiatorList.getItems().size(), 2);
    assertEquals(v1AuditTrailInitiatorList.getPagination(), null);
  }

  @Test
  void listAuditTrailRequiredParamsMissing() throws IOException {
    this.mockApiClient.onGet(V1_AUDIT_TRAIL_PRIVILEGED_USER, "resContent");
    Exception exception = assertThrows(ApiRuntimeException.class, () -> this.service.listAuditTrail(null, 1551888601279L,
        new CursorPaginationAttribute(1,3,2),1353716993L, "SUPER_ADMINISTRATOR"));

    assertTrue(exception.getMessage().contains(
        String.format(MISSING_REQUIRED_PARAMETER_EXCEPTION_MESSAGE, "startTimestamp", "v1AudittrailPrivilegeduserGet")));
  }

  @Test
  void suspendUserTestSuccess() throws ApiException {
    this.mockApiClient.onPut(SUSPEND_USER.replace("{uid}", "1234"), "{}");

    UserSuspension userSuspension = new UserSuspension();
    userSuspension.setSuspended(true);
    userSuspension.setSuspensionReason("reason why");
    userSuspension.setSuspendedUntil(Instant.now().toEpochMilli());

    this.service.suspendUser(1234L, "reason why", Instant.now());

    verify(spiedUserApi).v1AdminUserUserIdSuspensionUpdatePut(
        eq("1234"),
        eq(1234L),
        eq(userSuspension));
  }

  @Test
  void suspendUserTestFailed() {
    this.mockApiClient.onPut(400, SUSPEND_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class,
        () -> this.service.suspendUser(1234L, "reason", Instant.now().plus(Duration.ofDays(1))));
  }

  @Test
  void unsuspendUserTestSuccess() throws ApiException {
    this.mockApiClient.onPut(SUSPEND_USER.replace("{uid}", "1234"), "{}");

    UserSuspension userSuspension = new UserSuspension();
    userSuspension.setSuspended(false);

    this.service.unsuspendUser(1234L);

    verify(spiedUserApi).v1AdminUserUserIdSuspensionUpdatePut(
        eq("1234"),
        eq(1234L),
        eq(userSuspension));
  }

  @Test
  void unsuspendUserTestFailed() {
    this.mockApiClient.onPut(400, SUSPEND_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class,
        () -> this.service.unsuspendUser(1234L));
  }
}
