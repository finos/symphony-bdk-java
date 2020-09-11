package com.symphony.bdk.core.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.api.invoker.ApiRuntimeException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.BotAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.impl.BotAuthenticatorRsaImpl;
import com.symphony.bdk.core.service.user.constant.RoleId;
import com.symphony.bdk.core.service.user.constant.UserFeature;
import com.symphony.bdk.core.service.user.mapper.UserDetailMapper;
import com.symphony.bdk.core.test.BdkMockServer;
import com.symphony.bdk.core.test.BdkMockServerExtension;
import com.symphony.bdk.core.test.JsonHelper;
import com.symphony.bdk.core.test.RsaTestHelper;
import com.symphony.bdk.gen.api.UserApi;
import com.symphony.bdk.gen.api.UsersApi;
import com.symphony.bdk.gen.api.model.Avatar;
import com.symphony.bdk.gen.api.model.AvatarUpdate;
import com.symphony.bdk.gen.api.model.DelegateAction;
import com.symphony.bdk.gen.api.model.Disclaimer;
import com.symphony.bdk.gen.api.model.Feature;
import com.symphony.bdk.gen.api.model.StringId;
import com.symphony.bdk.gen.api.model.UserAttributes;
import com.symphony.bdk.gen.api.model.UserDetail;
import com.symphony.bdk.gen.api.model.UserFilter;
import com.symphony.bdk.gen.api.model.UserSearchFilter;
import com.symphony.bdk.gen.api.model.UserSearchQuery;
import com.symphony.bdk.gen.api.model.UserStatus;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V2UserDetail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@ExtendWith(BdkMockServerExtension.class)
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
  private static final String GET_USER_V2 = "/pod/v2/user";
  private static final String SEARCH_USERS_V3 = "/pod/v3/users";
  private static final String SEARCH_USER_BY_QUERY = "/pod/v1/user/search";

  private UserService service;
  private UserApi spiedUserApi;
  private UsersApi spiedUsersApi;

  @BeforeEach
  void init(final BdkMockServer mockServer) throws AuthUnauthorizedException {
    mockServer.onPost("/login/pubkey/authenticate",
        res -> res.withBody("{ \"token\": \"1234\", \"name\": \"sessionToken\" }"));
    mockServer.onPost("/relay/pubkey/authenticate",
        res -> res.withBody("{ \"token\": \"1234\", \"name\": \"sessionToken\" }"));
    BotAuthenticator authenticator = new BotAuthenticatorRsaImpl(
        "username",
        RsaTestHelper.generateKeyPair().getPrivate(),
        mockServer.newApiClient("/login"),
        mockServer.newApiClient("/relay")
    );
    ApiClient podClient = mockServer.newApiClient("/pod");
    AuthSession authSession = authenticator.authenticateBot();
    UserApi userApi = new UserApi(podClient);
    this.spiedUserApi = spy(userApi);
    UsersApi usersApi = new UsersApi(podClient);
    this.spiedUsersApi = spy(usersApi);
    this.service = new UserService(this.spiedUserApi, this.spiedUsersApi, authSession);
  }

  @Test
  void getUserDetailByUidTest(final BdkMockServer mockServer) throws IOException {
    String response = JsonHelper.readFromClasspath("/user/user_detail.json");
    mockServer.onGet(V2_USER_DETAIL_BY_ID.replace("{uid}", "1234"), res -> res.withBody(response));

    V2UserDetail userDetail = this.service.getUserDetailByUid(1234L);

    assertEquals(userDetail.getUserAttributes().getCompanyName(), "Company");
    assertEquals(userDetail.getUserAttributes().getUserName(), "johndoe");
    assertEquals(userDetail.getRoles().size(), 6);
  }

  @Test
  void listUsersDetailTest(final BdkMockServer mockServer) throws IOException {
    String responseV2 = JsonHelper.readFromClasspath("/user/list_users_detail_v2.json");
    mockServer.onGet(V2_USER_LIST, res -> res.withBody(responseV2));
    List<V2UserDetail> UserDetails = this.service.listUsersDetail();

    assertEquals(UserDetails.size(), 5);
    assertEquals(UserDetails.get(0).getUserAttributes().getUserName(), "agentservice");
    assertEquals(UserDetails.get(1).getUserAttributes().getUserName(), "bot.user1");
  }

  @Test
  void listUsersDetailTestFailed(final BdkMockServer mockServer) {
    mockServer.onGetFailed(400, V2_USER_LIST, res -> res.withBody("{}"));

    assertThrows(ApiRuntimeException.class, () -> this.service.listUsersDetail());
  }

  @Test
  void listUsersDetailByFilterTest(final BdkMockServer mockServer) throws IOException {
    String responseV1 = JsonHelper.readFromClasspath("/user/list_users_detail_v1.json");

    mockServer.onPost(USER_FIND, res -> res.withBody(responseV1));
    UserFilter userFilter = new UserFilter();
    List<V2UserDetail> userDetails = this.service.listUsersDetail(userFilter);

    assertEquals(userDetails.size(), 4);
    assertEquals(userDetails.get(2).getUserAttributes().getUserName(), "bot.user");
    assertEquals(userDetails.get(3).getUserAttributes().getUserName(), "nexus.user");
  }

  @Test
  void listUsersDetailByFilterTestFailed(final BdkMockServer mockServer) {
    mockServer.onPostFailed(400, USER_FIND, res -> res.withBody("{}"));

    assertThrows(ApiRuntimeException.class, () -> this.service.listUsersDetail(new UserFilter()));
  }

  @Test
  void addRoleToUserTest(final BdkMockServer mockServer) throws ApiException {
    mockServer.onPost(ADD_ROLE_TO_USER.replace("{uid}", "1234"),
        res -> res.withBody("{\"format\": \"TEXT\", \"message\": \"Role added\"}"));

    this.service.addRoleToUser(1234L, RoleId.INDIVIDUAL);

    verify(spiedUserApi).v1AdminUserUidRolesAddPost(eq("1234"), eq(1234L), eq(new StringId().id("INDIVIDUAL")));
  }

  @Test
  void addRoleToUserTestFailed(final BdkMockServer mockServer) {
    mockServer.onPostFailed(400, ADD_ROLE_TO_USER.replace("{uid}", "1234"), res -> res.withBody("{}"));

    assertThrows(ApiRuntimeException.class, () -> this.service.addRoleToUser(1234L, RoleId.INDIVIDUAL));
  }

  @Test
  void removeRoleFromUserTest(final BdkMockServer mockServer) throws ApiException {
    mockServer.onPost(REMOVE_ROLE_FROM_USER.replace("{uid}", "1234"),
        res -> res.withBody("{\"format\": \"TEXT\", \"message\": \"Role removed\"}"));

    this.service.removeRoleFromUser(1234L, RoleId.INDIVIDUAL);

    verify(spiedUserApi).v1AdminUserUidRolesRemovePost(eq("1234"), eq(1234L), eq(new StringId().id("INDIVIDUAL")));
  }

  @Test
  void removeRoleFromUserTestFailed(final BdkMockServer mockServer) {
    mockServer.onPostFailed(400, REMOVE_ROLE_FROM_USER.replace("{uid}", "1234"), res -> res.withBody("{}"));

    assertThrows(ApiRuntimeException.class, () -> this.service.removeRoleFromUser(1234L, RoleId.INDIVIDUAL));
  }

  @Test
  void getAvatarFromUser(final BdkMockServer mockServer) {
    mockServer.onGet(GET_AVATAR_FROM_USER.replace("{uid}", "1234"), res -> res.withBody("[\n"
        + "  {\n"
        + "    \"size\": \"600\",\n"
        + "    \"url\": \"../avatars/acme/600/7215545057281/3gXMhglCCTwLPL9JAprnyHzYn5-PR49-wYDG814n1g8.png\"\n"
        + "  },\n"
        + "  {\n"
        + "    \"size\": \"150\",\n"
        + "    \"url\": \"../avatars/acme/150/7215545057281/3gXMhglCCTwLPL9JAprnyHzYn5-PR49-wYDG814n1g8.png\"\n"
        + "  }\n"
        + "]"));

    List<Avatar> avatars = this.service.getAvatarFromUser(1234L);

    assertEquals(avatars.size(), 2);
    assertEquals(avatars.get(0).getSize(), "600");
    assertEquals(avatars.get(1).getSize(), "150");
  }

  @Test
  void getAvatarFromUserTestFailed(final BdkMockServer mockServer) {
    mockServer.onGetFailed(400, GET_AVATAR_FROM_USER.replace("{uid}", "1234"), res -> res.withBody("{}"));

    assertThrows(ApiRuntimeException.class, () -> this.service.getAvatarFromUser(1234L));
  }

  @Test
  void updateAvatarOfUserTest(final BdkMockServer mockServer) throws ApiException, IOException {
    mockServer.onPost(UPDATE_AVATAR_OF_USER.replace("{uid}", "1234"),
        res -> res.withBody("{\"format\": \"TEXT\", \"message\": \"OK\"}"));
    String avatar = "iVBORw0KGgoAAAANSUhEUgAAAJgAAAAoCAMAAAA11s";
    byte[] bytes = avatar.getBytes();
    InputStream inputStream = new ByteArrayInputStream(bytes);

    this.service.updateAvatarOfUser(1234L, avatar);
    this.service.updateAvatarOfUser(1234L, bytes);
    this.service.updateAvatarOfUser(1234L, inputStream);

    verify(spiedUserApi).v1AdminUserUidAvatarUpdatePost(eq("1234"), eq(1234L), eq(new AvatarUpdate().image("iVBORw0KGgoAAAANSUhEUgAAAJgAAAAoCAMAAAA11s")));
    verify(spiedUserApi, times(2)).v1AdminUserUidAvatarUpdatePost(eq("1234"), eq(1234L), eq(new AvatarUpdate().image(Base64.getEncoder().encodeToString(bytes))));

  }

  @Test
  void updateAvatarOfUserTestFailed(final BdkMockServer mockServer) {
    mockServer.onPostFailed(400, UPDATE_AVATAR_OF_USER.replace("{uid}", "1234"), res -> res.withBody("{}"));
    String avatar = "iVBORw0KGgoAAAANSUhEUgAAAJgAAAAoCAMAAAA11s";
    byte[] bytes = avatar.getBytes();
    InputStream inputStream = new ByteArrayInputStream(bytes);

    assertThrows(ApiRuntimeException.class, () -> this.service.updateAvatarOfUser(1234L, avatar));
    assertThrows(ApiRuntimeException.class, () -> this.service.updateAvatarOfUser(1234L, bytes));
    assertThrows(ApiRuntimeException.class, () -> this.service.updateAvatarOfUser(1234L, inputStream));
  }

  @Test
  void getDisclaimerAssignedToUserTest(final BdkMockServer mockServer) {
    mockServer.onGet(GET_DISCLAIMER_OF_USER.replace("{uid}", "1234"), res -> res.withBody("{\n"
        + "  \"id\": \"571d2052e4b042aaf06d2e7a\",\n"
        + "  \"name\": \"Enterprise Disclaimer\",\n"
        + "  \"content\": \"This is a disclaimer for the enterprise.\",\n"
        + "  \"frequencyInHours\": 24,\n"
        + "  \"isDefault\": false,\n"
        + "  \"isActive\": true,\n"
        + "  \"createdDate\": 1461526610846,\n"
        + "  \"modifiedDate\": 1461526610846\n"
        + "}"));

    Disclaimer disclaimer = this.service.getDisclaimerAssignedToUser(1234L);
    assertEquals(disclaimer.getName(), "Enterprise Disclaimer");
    assertEquals(disclaimer.getIsActive(), true);
    assertEquals(disclaimer.getIsDefault(), false);
  }

  @Test
  void getDisclaimerAssignedToUserTestFailed(final BdkMockServer mockServer) {
    mockServer.onGetFailed(400, GET_DISCLAIMER_OF_USER.replace("{uid}", "1234"), res -> res.withBody("{}"));

    assertThrows(ApiRuntimeException.class, () -> this.service.getDisclaimerAssignedToUser(1234L));
  }

  @Test
  void unAssignDisclaimerFromUserTest(final BdkMockServer mockServer) throws ApiException {
    mockServer.onDelete(UNASSIGN_DISCLAIMER_FROM_USER.replace("{uid}", "1234"), res -> res.withBody("{}"));

    this.service.unAssignDisclaimerFromUser(1234L);

    verify(spiedUserApi).v1AdminUserUidDisclaimerDelete("1234", 1234L);
  }

  @Test
  void unAssignDisclaimerFromUserTestFailed(final BdkMockServer mockServer) {
    mockServer.onDeleteFailed(400, UNASSIGN_DISCLAIMER_FROM_USER.replace("{uid}", "1234"), res -> res.withBody("{}"));

    assertThrows(ApiRuntimeException.class, () -> this.service.unAssignDisclaimerFromUser(1234L));
  }

  @Test
  void assignDisclaimerToUserTest(final BdkMockServer mockServer) throws ApiException {
    mockServer.onPost(ASSIGN_DISCLAIMER_TO_USER.replace("{uid}", "1234"), res -> res.withBody("{}"));

    this.service.assignDisclaimerToUser(1234L, "disclaimer");

    verify(spiedUserApi).v1AdminUserUidDisclaimerUpdatePost(eq("1234"), eq(1234L), eq(new StringId().id("disclaimer")));
  }

  @Test
  void assignDisclaimerToUserTestFailed(final BdkMockServer mockServer) {
    mockServer.onPostFailed(400, ASSIGN_DISCLAIMER_TO_USER.replace("{uid}", "1234"), res -> res.withBody("{}"));

    assertThrows(ApiRuntimeException.class, () -> this.service.assignDisclaimerToUser(1234L, "disclaimer"));
  }

  @Test
  void getDelegatesAssignedToUserTest(final BdkMockServer mockServer) {
    mockServer.onGet(GET_DELEGATE_OF_USER.replace("{uid}", "1234"), res -> res.withBody("[7215545078461]"));

    List<Long> delegates = this.service.getDelegatesAssignedToUser(1234L);

    assertEquals(delegates.size(), 1);
    assertEquals(delegates.get(0), 7215545078461L);
  }

  @Test
  void getDelegatesAssignedToUserTestFailed(final BdkMockServer mockServer) {
    mockServer.onGetFailed(400, GET_DELEGATE_OF_USER.replace("{uid}", "1234"), res -> res.withBody("{}"));

    assertThrows(ApiRuntimeException.class, () -> this.service.getDelegatesAssignedToUser(1234L));
  }

  @Test
  void updateDelegatesAssignedToUserTest(final BdkMockServer mockServer) throws ApiException {
    mockServer.onPost(UPDATE_DELEGATE_OF_USER.replace("{uid}", "1234"), res -> res.withBody("{}"));

    this.service.updateDelegatesAssignedToUser(1234L, 1234L, DelegateAction.ActionEnum.ADD);

    verify(spiedUserApi).v1AdminUserUidDelegatesUpdatePost(
        eq("1234"),
        eq(1234L),
        eq(new DelegateAction().action(DelegateAction.ActionEnum.ADD).userId(1234L)));
  }

  @Test
  void updateDelegatesAssignedToUserTestFailed(final BdkMockServer mockServer) {
    mockServer.onPostFailed(400, UPDATE_DELEGATE_OF_USER.replace("{uid}", "1234"), res -> res.withBody("{}"));

    assertThrows(ApiRuntimeException.class,
        () -> this.service.updateDelegatesAssignedToUser(1234L, 1234L, DelegateAction.ActionEnum.ADD));
  }

  @Test
  void getFeatureEntitlementsOfUserTest(final BdkMockServer mockServer) {
    mockServer.onGet(GET_FEATURE_ENTITLEMENTS_OF_USER.replace("{uid}", "1234"), res -> res.withBody("[\n"
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
        + "]"));

    List<Feature> features = this.service.getFeatureEntitlementsOfUser(1234L);

    assertEquals(features.size(), 4);
    assertEquals(features.get(0).getEntitlment(), UserFeature.canCreatePublicRoom.name());
    assertEquals(features.get(1).getEnabled(), false);
  }

  @Test
  void getFeatureEntitlementsOfUserTestFailed(final BdkMockServer mockServer) {
    mockServer.onGetFailed(400, GET_FEATURE_ENTITLEMENTS_OF_USER.replace("{uid}", "1234"), res -> res.withBody("{}"));

    assertThrows(ApiRuntimeException.class, () -> this.service.getFeatureEntitlementsOfUser(1234L));
  }

  @Test
  void updateFeatureEntitlementsOfUserTest(final BdkMockServer mockServer) throws ApiException {
    mockServer.onPost(UPDATE_FEATURE_ENTITLEMENTS_OF_USER.replace("{uid}", "1234"), res -> res.withBody("{}"));
    List<Feature> features = Collections.singletonList(new Feature().entitlment("delegatesEnabled").enabled(true));

    this.service.updateFeatureEntitlementsOfUser(1234L, features);

    verify(spiedUserApi).v1AdminUserUidFeaturesUpdatePost("1234", 1234L, features);
  }

  @Test
  void updateFeatureEntitlementsOfUserTestFailed(final BdkMockServer mockServer) {
    mockServer.onPostFailed(400, UPDATE_FEATURE_ENTITLEMENTS_OF_USER.replace("{uid}", "1234"),
        res -> res.withBody("{}"));
    List<Feature> features = Collections.singletonList(new Feature().entitlment("delegatesEnabled").enabled(true));
    assertThrows(ApiRuntimeException.class, () -> this.service.updateFeatureEntitlementsOfUser(1234L, features));
  }

  @Test
  void getStatusOfUserTest(final BdkMockServer mockServer) {
    mockServer.onGet(GET_STATUS_OF_USER.replace("{uid}", "1234"), res -> res.withBody("{\"status\": \"ENABLED\"}"));

    UserStatus userStatus = this.service.getStatusOfUser(1234L);

    assertEquals(userStatus.getStatus(), UserStatus.StatusEnum.ENABLED);
  }

  @Test
  void getStatusOfUserTestFailed(final BdkMockServer mockServer) {
    mockServer.onGetFailed(400, GET_STATUS_OF_USER.replace("{uid}", "1234"), res -> res.withBody("{}"));

    assertThrows(ApiRuntimeException.class, () -> this.service.getStatusOfUser(1234L));
  }

  @Test
  void updateStatusOfUserTest(final BdkMockServer mockServer) throws ApiException {
    mockServer.onPost(UPDATE_STATUS_OF_USER.replace("{uid}", "1234"), res -> res.withBody("{}"));
    UserStatus userStatus = new UserStatus().status(UserStatus.StatusEnum.ENABLED);

    this.service.updateStatusOfUser(1234L, userStatus);

    verify(spiedUserApi).v1AdminUserUidStatusUpdatePost("1234", 1234L, userStatus);
  }

  @Test
  void updateStatusOfUserTestFailed(final BdkMockServer mockServer) {
    mockServer.onPostFailed(400, UPDATE_STATUS_OF_USER.replace("{uid}", "1234"), res -> res.withBody("{}"));

    assertThrows(ApiRuntimeException.class, () -> this.service.updateStatusOfUser(1234L,
        new UserStatus().status(UserStatus.StatusEnum.ENABLED)));
  }

  @Test
  void searchUserV3Test(final BdkMockServer mockServer) throws IOException {
    String response = JsonHelper.readFromClasspath("/user/users.json");
    mockServer.onGet(SEARCH_USERS_V3, res -> res.withBody(response));

    List<UserV2> users1 = this.service.searchUserByIds(Collections.singletonList(1234L), true);

    assertEquals(users1.size(), 1);
    assertEquals(users1.get(0).getDisplayName(), "Test Bot");

    List<UserV2> users2 = this.service.searchUserByEmails(Collections.singletonList("tibot@symphony.com"), true);

    assertEquals(users2.size(), 1);
    assertEquals(users2.get(0).getUsername(), "tibot");

    List<UserV2> users3 = this.service.searchUserByUsernames(Collections.singletonList("tibot"));

    assertEquals(users3.size(), 1);
    assertEquals(users3.get(0).getId(), 1234L);

    List<UserV2> users4 = this.service.searchUserByIds(Collections.singletonList(1234L));

    assertEquals(users4.size(), 1);
    assertEquals(users4.get(0).getId(), 1234L);

    List<UserV2> users5 = this.service.searchUserByEmails(Collections.singletonList("tibot@symphony.com"));

    assertEquals(users5.size(), 1);
    assertEquals(users5.get(0).getId(), 1234L);
  }

  @Test
  void searchUserV3TestFailed(final BdkMockServer mockServer) {
    mockServer.onGetFailed(400, SEARCH_USERS_V3, res -> res.withBody("{}"));

    assertThrows(ApiRuntimeException.class, () -> this.service.searchUserByIds(Collections.singletonList(1234L), true));
    assertThrows(ApiRuntimeException.class, () -> this.service.searchUserByEmails(Collections.singletonList("tibot@symphony.com"), true));
    assertThrows(ApiRuntimeException.class, () -> this.service.searchUserByUsernames(Collections.singletonList("tibot")));
    assertThrows(ApiRuntimeException.class, () -> this.service.searchUserByIds(Collections.singletonList(1234L)));
    assertThrows(ApiRuntimeException.class, () -> this.service.searchUserByEmails(Collections.singletonList("tibot@symphony.com")));
  }

  @Test
  void searchUserBySearchQueryTest(final BdkMockServer mockServer) throws IOException {
    String response = JsonHelper.readFromClasspath("/user/users_by_query.json");
    mockServer.onPost(SEARCH_USER_BY_QUERY, res -> res.withBody(response));

    UserSearchQuery query = new UserSearchQuery().query("john doe").filters(new UserSearchFilter().title("title").company("Gotham").location("New York"));

    List<UserV2> users = this.service.searchUserBySearchQuery(query, true);

    assertEquals(users.size(), 1);
    assertEquals(users.get(0).getUsername(), "john.doe");
    assertEquals(users.get(0).getDisplayName(), "John Doe");
  }

  @Test
  void userDetailMapperNullTest() {
    V2UserDetail userDetailNull = UserDetailMapper.INSTANCE.userDetailToV2UserDetail(null);

    assertNull(userDetailNull);

    V2UserDetail userDetail = UserDetailMapper.INSTANCE.userDetailToV2UserDetail(new UserDetail());

    assertNull(userDetail.getUserAttributes());

    V2UserDetail userDetail1 = UserDetailMapper.INSTANCE.userDetailToV2UserDetail(new UserDetail().userAttributes(new UserAttributes().accountType(null)));
    assertNull(userDetail1.getUserAttributes().getAccountType());
  }
}
