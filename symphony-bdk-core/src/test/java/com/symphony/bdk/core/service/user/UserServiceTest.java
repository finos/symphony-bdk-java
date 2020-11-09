package com.symphony.bdk.core.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.core.service.pagination.model.CursorPaginationAttribute;
import com.symphony.bdk.core.service.pagination.model.StreamPaginationAttribute;
import com.symphony.bdk.core.service.user.constant.RoleId;
import com.symphony.bdk.core.service.user.constant.UserFeature;
import com.symphony.bdk.core.service.user.mapper.UserDetailMapper;
import com.symphony.bdk.core.test.JsonHelper;
import com.symphony.bdk.core.test.MockApiClient;
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
import com.symphony.bdk.gen.api.model.StringId;
import com.symphony.bdk.gen.api.model.UserAttributes;
import com.symphony.bdk.gen.api.model.UserDetail;
import com.symphony.bdk.gen.api.model.UserFilter;
import com.symphony.bdk.gen.api.model.UserSearchFilter;
import com.symphony.bdk.gen.api.model.UserSearchQuery;
import com.symphony.bdk.gen.api.model.UserStatus;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V2UserDetail;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class UserServiceTest {
  private static final String V2_USER_DETAIL_BY_ID = "/pod/v2/admin/user/{uid}";
  private static final String V2_USER_LIST = "/pod/v2/admin/user/list";
  private static final String USER_FIND = "/pod/v1/admin/user/find";
  private static final String ADD_ROLE_TO_USER = "/pod/v1/admin/user/{uid}/roles/add";
  private static final String REMOVE_ROLE_FROM_USER = "/pod/v1/admin/user/{uid}/roles/remove";
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

  private UserService service;
  private UserApi spiedUserApi;
  private UsersApi spiedUsersApi;
  private MockApiClient mockApiClient;

  @BeforeEach
  void init() {
    this.mockApiClient = new MockApiClient();
    AuthSession authSession = mock(AuthSession.class);
    ApiClient podClient = mockApiClient.getApiClient("/pod");
    UserApi userApi = new UserApi(podClient);
    this.spiedUserApi = spy(userApi);
    UsersApi usersApi = new UsersApi(podClient);
    this.spiedUsersApi = spy(usersApi);
    this.service = new UserService(this.spiedUserApi, this.spiedUsersApi, authSession, new RetryWithRecoveryBuilder());

    when(authSession.getSessionToken()).thenReturn("1234");
    when(authSession.getKeyManagerToken()).thenReturn("1234");
  }

  @Test
  void getUserDetailByUidTest() throws IOException {
    String response = JsonHelper.readFromClasspath("/user/user_detail.json");
    this.mockApiClient.onGet(V2_USER_DETAIL_BY_ID.replace("{uid}", "1234"), response);

    V2UserDetail userDetail = this.service.getUserDetailByUid(1234L);

    assertEquals(userDetail.getUserAttributes().getCompanyName(), "Company");
    assertEquals(userDetail.getUserAttributes().getUserName(), "johndoe");
    assertEquals(userDetail.getRoles().size(), 6);
  }

  @Test
  void listUsersDetailTest() throws IOException {
    String responseV2 = JsonHelper.readFromClasspath("/user/list_users_detail_v2.json");
    this.mockApiClient.onGet(V2_USER_LIST, responseV2);
    List<V2UserDetail> UserDetails = this.service.listUsersDetail();

    assertEquals(UserDetails.size(), 5);
    assertEquals(UserDetails.get(0).getUserAttributes().getUserName(), "agentservice");
    assertEquals(UserDetails.get(1).getUserAttributes().getUserName(), "bot.user1");
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
    List<V2UserDetail> UserDetails = this.service.listUsersDetail(new PaginationAttribute(0, 100));

    assertEquals(UserDetails.size(), 5);
    assertEquals(UserDetails.get(0).getUserAttributes().getUserName(), "agentservice");
    assertEquals(UserDetails.get(1).getUserAttributes().getUserName(), "bot.user1");
  }

  @Test
  void listAllUsersDetailTest() throws IOException {
    String responseV2 = JsonHelper.readFromClasspath("/user/list_users_detail_v2.json");
    this.mockApiClient.onGet(V2_USER_LIST, responseV2);
    List<V2UserDetail> UserDetails = this.service.listAllUsersDetail().collect(Collectors.toList());

    assertEquals(UserDetails.size(), 5);
    assertEquals(UserDetails.get(0).getUserAttributes().getUserName(), "agentservice");
    assertEquals(UserDetails.get(1).getUserAttributes().getUserName(), "bot.user1");
  }

  @Test
  void listAllUsersDetailPaginationTest() throws IOException {
    String responseV2 = JsonHelper.readFromClasspath("/user/list_users_detail_v2.json");
    this.mockApiClient.onGet(V2_USER_LIST, responseV2);
    List<V2UserDetail> UserDetails =
        this.service.listAllUsersDetail(new StreamPaginationAttribute(100, 100)).collect(Collectors.toList());

    assertEquals(UserDetails.size(), 5);
    assertEquals(UserDetails.get(0).getUserAttributes().getUserName(), "agentservice");
    assertEquals(UserDetails.get(1).getUserAttributes().getUserName(), "bot.user1");
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

    this.service.addRoleToUser(1234L, RoleId.INDIVIDUAL);

    verify(spiedUserApi).v1AdminUserUidRolesAddPost(eq("1234"), eq(1234L), eq(new StringId().id("INDIVIDUAL")));
  }

  @Test
  void addRoleToUserTestFailed() {
    this.mockApiClient.onPost(400, ADD_ROLE_TO_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.addRoleToUser(1234L, RoleId.INDIVIDUAL));
  }

  @Test
  void removeRoleFromUserTest() throws ApiException {
    this.mockApiClient.onPost(REMOVE_ROLE_FROM_USER.replace("{uid}", "1234"),
        "{\"format\": \"TEXT\", \"message\": \"Role removed\"}");

    this.service.removeRoleFromUser(1234L, RoleId.INDIVIDUAL);

    verify(spiedUserApi).v1AdminUserUidRolesRemovePost(eq("1234"), eq(1234L), eq(new StringId().id("INDIVIDUAL")));
  }

  @Test
  void removeRoleFromUserTestFailed() {
    this.mockApiClient.onPost(400, REMOVE_ROLE_FROM_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.removeRoleFromUser(1234L, RoleId.INDIVIDUAL));
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

    List<Avatar> avatars = this.service.getAvatarFromUser(1234L);

    assertEquals(avatars.size(), 2);
    assertEquals(avatars.get(0).getSize(), "600");
    assertEquals(avatars.get(1).getSize(), "150");
  }

  @Test
  void getAvatarFromUserTestFailed() {
    this.mockApiClient.onGet(400, GET_AVATAR_FROM_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.getAvatarFromUser(1234L));
  }

  @Test
  void updateAvatarOfUserTest() throws ApiException, IOException {
    this.mockApiClient.onPost(UPDATE_AVATAR_OF_USER.replace("{uid}", "1234"),
        "{\"format\": \"TEXT\", \"message\": \"OK\"}");
    String avatar = "iVBORw0KGgoAAAANSUhEUgAAAJgAAAAoCAMAAAA11s";
    byte[] bytes = avatar.getBytes();
    InputStream inputStream = new ByteArrayInputStream(bytes);

    this.service.updateAvatarOfUser(1234L, avatar);
    this.service.updateAvatarOfUser(1234L, bytes);
    this.service.updateAvatarOfUser(1234L, inputStream);

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

    assertThrows(ApiRuntimeException.class, () -> this.service.updateAvatarOfUser(1234L, avatar));
    assertThrows(ApiRuntimeException.class, () -> this.service.updateAvatarOfUser(1234L, bytes));
    assertThrows(ApiRuntimeException.class, () -> this.service.updateAvatarOfUser(1234L, inputStream));
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

    Disclaimer disclaimer = this.service.getDisclaimerAssignedToUser(1234L);
    assertEquals(disclaimer.getName(), "Enterprise Disclaimer");
    assertEquals(disclaimer.getIsActive(), true);
    assertEquals(disclaimer.getIsDefault(), false);
  }

  @Test
  void getDisclaimerAssignedToUserTestFailed() {
    this.mockApiClient.onGet(400, GET_DISCLAIMER_OF_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.getDisclaimerAssignedToUser(1234L));
  }

  @Test
  void unAssignDisclaimerFromUserTest() throws ApiException {
    this.mockApiClient.onDelete(UNASSIGN_DISCLAIMER_FROM_USER.replace("{uid}", "1234"), "{}");

    this.service.unAssignDisclaimerFromUser(1234L);

    verify(spiedUserApi).v1AdminUserUidDisclaimerDelete("1234", 1234L);
  }

  @Test
  void unAssignDisclaimerFromUserTestFailed() {
    this.mockApiClient.onDelete(400, UNASSIGN_DISCLAIMER_FROM_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.unAssignDisclaimerFromUser(1234L));
  }

  @Test
  void assignDisclaimerToUserTest() throws ApiException {
    this.mockApiClient.onPost(ASSIGN_DISCLAIMER_TO_USER.replace("{uid}", "1234"), "{}");

    this.service.assignDisclaimerToUser(1234L, "disclaimer");

    verify(spiedUserApi).v1AdminUserUidDisclaimerUpdatePost(eq("1234"), eq(1234L), eq(new StringId().id("disclaimer")));
  }

  @Test
  void assignDisclaimerToUserTestFailed() {
    this.mockApiClient.onPost(400, ASSIGN_DISCLAIMER_TO_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.assignDisclaimerToUser(1234L, "disclaimer"));
  }

  @Test
  void getDelegatesAssignedToUserTest() {
    this.mockApiClient.onGet(GET_DELEGATE_OF_USER.replace("{uid}", "1234"), "[7215545078461]");

    List<Long> delegates = this.service.getDelegatesAssignedToUser(1234L);

    assertEquals(delegates.size(), 1);
    assertEquals(delegates.get(0), 7215545078461L);
  }

  @Test
  void getDelegatesAssignedToUserTestFailed() {
    this.mockApiClient.onGet(400, GET_DELEGATE_OF_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.getDelegatesAssignedToUser(1234L));
  }

  @Test
  void updateDelegatesAssignedToUserTest() throws ApiException {
    this.mockApiClient.onPost(UPDATE_DELEGATE_OF_USER.replace("{uid}", "1234"), "{}");

    this.service.updateDelegatesAssignedToUser(1234L, 1234L, DelegateAction.ActionEnum.ADD);

    verify(spiedUserApi).v1AdminUserUidDelegatesUpdatePost(
        eq("1234"),
        eq(1234L),
        eq(new DelegateAction().action(DelegateAction.ActionEnum.ADD).userId(1234L)));
  }

  @Test
  void updateDelegatesAssignedToUserTestFailed() {
    this.mockApiClient.onPost(400, UPDATE_DELEGATE_OF_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class,
        () -> this.service.updateDelegatesAssignedToUser(1234L, 1234L, DelegateAction.ActionEnum.ADD));
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

    List<Feature> features = this.service.getFeatureEntitlementsOfUser(1234L);

    assertEquals(features.size(), 4);
    assertEquals(features.get(0).getEntitlment(), UserFeature.canCreatePublicRoom.name());
    assertEquals(features.get(1).getEnabled(), false);
  }

  @Test
  void getFeatureEntitlementsOfUserTestFailed() {
    this.mockApiClient.onGet(400, GET_FEATURE_ENTITLEMENTS_OF_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.getFeatureEntitlementsOfUser(1234L));
  }

  @Test
  void updateFeatureEntitlementsOfUserTest() throws ApiException {
    this.mockApiClient.onPost(UPDATE_FEATURE_ENTITLEMENTS_OF_USER.replace("{uid}", "1234"), "{}");
    List<Feature> features = Collections.singletonList(new Feature().entitlment("delegatesEnabled").enabled(true));

    this.service.updateFeatureEntitlementsOfUser(1234L, features);

    verify(spiedUserApi).v1AdminUserUidFeaturesUpdatePost("1234", 1234L, features);
  }

  @Test
  void updateFeatureEntitlementsOfUserTestFailed() {
    this.mockApiClient.onPost(400, UPDATE_FEATURE_ENTITLEMENTS_OF_USER.replace("{uid}", "1234"),
        "{}");
    List<Feature> features = Collections.singletonList(new Feature().entitlment("delegatesEnabled").enabled(true));
    assertThrows(ApiRuntimeException.class, () -> this.service.updateFeatureEntitlementsOfUser(1234L, features));
  }

  @Test
  void getStatusOfUserTest() {
    this.mockApiClient.onGet(GET_STATUS_OF_USER.replace("{uid}", "1234"), "{\"status\": \"ENABLED\"}");

    UserStatus userStatus = this.service.getStatusOfUser(1234L);

    assertEquals(userStatus.getStatus(), UserStatus.StatusEnum.ENABLED);
  }

  @Test
  void getStatusOfUserTestFailed() {
    this.mockApiClient.onGet(400, GET_STATUS_OF_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.getStatusOfUser(1234L));
  }

  @Test
  void updateStatusOfUserTest() throws ApiException {
    this.mockApiClient.onPost(UPDATE_STATUS_OF_USER.replace("{uid}", "1234"), "{}");
    UserStatus userStatus = new UserStatus().status(UserStatus.StatusEnum.ENABLED);

    this.service.updateStatusOfUser(1234L, userStatus);

    verify(spiedUserApi).v1AdminUserUidStatusUpdatePost("1234", 1234L, userStatus);
  }

  @Test
  void updateStatusOfUserTestFailed() {
    this.mockApiClient.onPost(400, UPDATE_STATUS_OF_USER.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.updateStatusOfUser(1234L,
        new UserStatus().status(UserStatus.StatusEnum.ENABLED)));
  }

  @Test
  void searchUserV3Test() throws IOException {
    String response = JsonHelper.readFromClasspath("/user/users.json");
    this.mockApiClient.onGet(SEARCH_USERS_V3, response);

    List<UserV2> users1 = this.service.searchUserByIds(Collections.singletonList(1234L), true, true);

    assertEquals(users1.size(), 1);
    assertEquals(users1.get(0).getDisplayName(), "Test Bot");

    List<UserV2> users2 = this.service.searchUserByEmails(Collections.singletonList("tibot@symphony.com"), true, true);

    assertEquals(users2.size(), 1);
    assertEquals(users2.get(0).getUsername(), "tibot");

    List<UserV2> users3 = this.service.searchUserByUsernames(Collections.singletonList("tibot"), true);

    assertEquals(users3.size(), 1);
    assertEquals(users3.get(0).getId(), 1234L);

    List<UserV2> users4 = this.service.searchUserByIds(Collections.singletonList(1234L));

    assertEquals(users4.size(), 1);
    assertEquals(users4.get(0).getId(), 1234L);

    List<UserV2> users5 = this.service.searchUserByEmails(Collections.singletonList("tibot@symphony.com"));

    assertEquals(users5.size(), 1);
    assertEquals(users5.get(0).getId(), 1234L);

    List<UserV2> users6 = this.service.searchUserByUsernames(Collections.singletonList("tibot"));

    assertEquals(users6.size(), 1);
    assertEquals(users6.get(0).getId(), 1234L);
  }

  @Test
  void searchUserV3TestFailed() {
    this.mockApiClient.onGet(400, SEARCH_USERS_V3, "{}");

    assertThrows(ApiRuntimeException.class,
        () -> this.service.searchUserByIds(Collections.singletonList(1234L), true, true));
    this.mockApiClient.onGet(400, SEARCH_USERS_V3, "{}");
    assertThrows(ApiRuntimeException.class,
        () -> this.service.searchUserByEmails(Collections.singletonList("tibot@symphony.com"), true, true));
    assertThrows(ApiRuntimeException.class,
        () -> this.service.searchUserByUsernames(Collections.singletonList("tibot"), true));
    assertThrows(ApiRuntimeException.class, () -> this.service.searchUserByIds(Collections.singletonList(1234L)));
    assertThrows(ApiRuntimeException.class,
        () -> this.service.searchUserByEmails(Collections.singletonList("tibot@symphony.com")));
    assertThrows(ApiRuntimeException.class,
        () -> this.service.searchUserByUsernames(Collections.singletonList("tibot")));
  }

  @Test
  void searchUserBySearchQueryTest() throws IOException {
    String response = JsonHelper.readFromClasspath("/user/users_by_query.json");
    this.mockApiClient.onPost(SEARCH_USER_BY_QUERY, response);

    UserSearchQuery query = new UserSearchQuery().query("john doe")
        .filters(new UserSearchFilter().title("title").company("Gotham").location("New York"));

    List<UserV2> users = this.service.searchUserBySearchQuery(query, true);

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

    List<UserV2> users = this.service.searchUserBySearchQuery(query, true, new PaginationAttribute(0, 100));

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

    List<UserV2> users = this.service.searchAllUsersBySearchQuery(query, true).collect(Collectors.toList());

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

    List<UserV2> users = this.service.searchAllUsersBySearchQuery(query, true, new StreamPaginationAttribute(100, 100))
        .collect(Collectors.toList());

    assertEquals(users.size(), 1);
    assertEquals(users.get(0).getUsername(), "john.doe");
    assertEquals(users.get(0).getDisplayName(), "John Doe");
  }

  @Test
  void followUserTest() throws ApiException {
    this.mockApiClient.onPost(V1_USER_FOLLOW.replace("{uid}", "1234"), "{}");

    this.service.followUser(1234L, Arrays.asList(12345L, 12346L));

    verify(this.spiedUserApi).v1UserUidFollowPost(eq("1234"), eq(1234L),
        eq(new FollowersList().followers(Arrays.asList(12345L, 12346L))));
  }

  @Test
  void followUserFailed() {
    this.mockApiClient.onPost(400, V1_USER_FOLLOW.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.followUser(1234L, Collections.singletonList(12345L)));
  }

  @Test
  void unfollowUserTest() throws ApiException {
    this.mockApiClient.onPost(V1_USER_UNFOLLOW.replace("{uid}", "1234"), "{}");

    this.service.unfollowUser(1234L, Arrays.asList(12345L, 12346L));

    verify(this.spiedUserApi).v1UserUidUnfollowPost(eq("1234"), eq(1234L),
        eq(new FollowersList().followers(Arrays.asList(12345L, 12346L))));
  }

  @Test
  void unfollowFailed() {
    this.mockApiClient.onPost(400, V1_USER_UNFOLLOW.replace("{uid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.unfollowUser(1234L, Collections.singletonList(12345L)));
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
        + "            \"before\": \"1\",\n"
        + "            \"after\": \"4\"\n"
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
        + "            \"before\": \"1\",\n"
        + "            \"after\": \"4\"\n"
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
  void userDetailMapperNullTest() {
    V2UserDetail userDetailNull = UserDetailMapper.INSTANCE.userDetailToV2UserDetail(null);

    assertNull(userDetailNull);

    V2UserDetail userDetail = UserDetailMapper.INSTANCE.userDetailToV2UserDetail(new UserDetail());

    assertNull(userDetail.getUserAttributes());

    V2UserDetail userDetail1 = UserDetailMapper.INSTANCE.userDetailToV2UserDetail(
        new UserDetail().userAttributes(new UserAttributes().accountType(null)));
    assertNull(userDetail1.getUserAttributes().getAccountType());
  }
}
