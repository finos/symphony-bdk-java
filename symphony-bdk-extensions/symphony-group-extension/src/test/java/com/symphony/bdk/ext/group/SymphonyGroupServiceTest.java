package com.symphony.bdk.ext.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.BotAuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.ext.group.auth.OAuthSession;
import com.symphony.bdk.ext.group.gen.api.model.AddMember;
import com.symphony.bdk.ext.group.gen.api.model.CreateGroup;
import com.symphony.bdk.ext.group.gen.api.model.GroupList;
import com.symphony.bdk.ext.group.gen.api.model.Member;
import com.symphony.bdk.ext.group.gen.api.model.Pagination;
import com.symphony.bdk.ext.group.gen.api.model.PaginationCursors;
import com.symphony.bdk.ext.group.gen.api.model.ReadGroup;
import com.symphony.bdk.ext.group.gen.api.model.SortOrder;
import com.symphony.bdk.ext.group.gen.api.model.Status;
import com.symphony.bdk.ext.group.gen.api.model.UpdateGroup;
import com.symphony.bdk.ext.group.gen.api.model.UploadAvatar;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiResponse;
import com.symphony.bdk.http.api.ApiRuntimeException;
import com.symphony.bdk.http.api.Pair;
import com.symphony.bdk.http.api.auth.Authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class SymphonyGroupServiceTest {

  private static final String TOKEN = "1234";
  private ApiClientFactory clientFactory;
  private ApiClient loginClient;
  private ApiClient profileManagerClient;
  private BotAuthSession authSession;
  private SymphonyGroupService groupService;

  @BeforeEach
  void setUp() throws ApiException {
    loginClient = spy(TestApiClient.class);

    final OAuthSession.TokenResponse tokenResponse = new OAuthSession.TokenResponse();
    tokenResponse.setToken(TOKEN);

    when(loginClient.invokeAPI(eq("/idm/tokens"), eq("POST"), any(), any(), any(), any(), any(), any(), any(), any(),
        any()))
        .thenReturn(new ApiResponse<>(200, Collections.emptyMap(), tokenResponse));

    profileManagerClient = spy(TestApiClient.class);

    clientFactory = mock(ApiClientFactory.class);
    when(clientFactory.getLoginClient()).thenReturn(loginClient);
    when(clientFactory.getPodClient(eq("/profile-manager"))).thenReturn(profileManagerClient);

    authSession = mock(BotAuthSession.class);

    groupService = new SymphonyGroupService(ofMinimalInterval(2), clientFactory, authSession);
  }

  @Test
  void testGroupServiceInitialization() throws ApiException {
    verify(clientFactory, times(1)).getLoginClient();
    verify(clientFactory, times(1)).getPodClient(eq("/profile-manager"));
    verifyClientCalled(loginClient, "POST", "/idm/tokens");

    final Authentication bearerAuth = profileManagerClient.getAuthentications().get("bearerAuth");
    assertNotNull(bearerAuth);

    Map<String, String> headerParams = new HashMap<>();
    bearerAuth.apply(headerParams);
    assertEquals(Collections.singletonMap("Authorization", "Bearer " + TOKEN), headerParams);
  }

  @Test
  void testInsertGroup() throws ApiException {
    final ReadGroup groupToReturn = new ReadGroup();
    when(profileManagerClient.invokeAPI(eq("/v1/groups"), eq("POST"), any(), any(), any(), any(), any(), any(),
        any(), any(), any())).thenReturn(new ApiResponse<>(200, Collections.emptyMap(), groupToReturn));

    final CreateGroup inputGroup = new CreateGroup();
    final ReadGroup readGroup = groupService.insertGroup(inputGroup);

    assertEquals(groupToReturn, readGroup);
    verifyClientCalledWithBody(profileManagerClient, "POST", "/v1/groups", inputGroup);
  }

  @Test
  void testUpdateGroup() throws ApiException {
    final ReadGroup groupToReturn = new ReadGroup();
    when(profileManagerClient.invokeAPI(startsWith("/v1/groups/"), eq("PUT"), any(), any(), any(), any(), any(), any(),
        any(), any(), any())).thenReturn(new ApiResponse<>(200, Collections.emptyMap(), groupToReturn));

    final String groupId = "gid";
    final String ifMatch = "ifMatch";
    final ReadGroup readGroup = groupService.updateGroup(ifMatch, groupId, new UpdateGroup());

    assertEquals(groupToReturn, readGroup);
    final Map<String, String> expectedHeaders = new HashMap<String, String>() {{
      put("If-Match", ifMatch);
      put("X-Symphony-Host", "");
    }};
    verifyClientCalledWithHeaders(profileManagerClient, "PUT", "/v1/groups/" + groupId, expectedHeaders);
  }

  @Test
  void testUpdateAvatar() throws ApiException {
    final ReadGroup groupToReturn = new ReadGroup();
    when(profileManagerClient.invokeAPI(startsWith("/v1/groups/"), eq("POST"), any(), any(), any(), any(), any(), any(),
        any(), any(), any())).thenReturn(new ApiResponse<>(200, Collections.emptyMap(), groupToReturn));

    final String groupId = "gid";
    final byte[] image = "picture".getBytes(StandardCharsets.UTF_8);

    final ReadGroup readGroup = groupService.updateAvatar(groupId, image);

    assertEquals(groupToReturn, readGroup);
    verifyClientCalledWithBody(profileManagerClient, "POST", "/v1/groups/" + groupId + "/avatar", new UploadAvatar().image(image));
  }

  @Test
  void testGetGroup() throws ApiException {
    final ReadGroup groupToReturn = new ReadGroup();
    when(profileManagerClient.invokeAPI(startsWith("/v1/groups/"), eq("GET"), any(), any(), any(), any(), any(), any(),
        any(), any(), any()))
        .thenReturn(new ApiResponse<>(200, Collections.emptyMap(), groupToReturn));

    final String groupId = "gid";
    assertEquals(groupToReturn, groupService.getGroup(groupId));

    verifyClientCalled(profileManagerClient, "GET", "/v1/groups/" + groupId);
  }

  @Test
  void testListGroups() throws ApiException {
    GroupList groupsToReturn = new GroupList();

    when(profileManagerClient.invokeAPI(startsWith("/v1/groups/type/"), eq("GET"), any(), any(), any(), any(), any(), any(),
        any(), any(), any()))
        .thenReturn(new ApiResponse<>(200, Collections.emptyMap(), groupsToReturn));

    final Status status = Status.ACTIVE;
    final String before = "bef";
    final String after = "aft";
    final int limit = 1234;
    final SortOrder sortOrder = SortOrder.ASC;
    final GroupList groups = groupService.listGroups(status, before, after, limit, sortOrder);

    assertEquals(groupsToReturn, groups);

    List<Pair> expectedQueryParams = Arrays.asList(new Pair("status", status.getValue()), new Pair("before", before), new Pair("after", after), new Pair("limit", Integer.toString(limit)), new Pair("sortOrder", sortOrder.getValue()));
    verifyClientCalled(profileManagerClient, "GET", "/v1/groups/type/SDL", expectedQueryParams);
  }

  @Test
  void testListAllGroups() throws ApiException {
    final ReadGroup firstReadGroup = new ReadGroup().id("firstGroup");
    final ReadGroup secondReadGroup = new ReadGroup().id("secondGroup");
    final String nextPage = "nextPage";

    when(profileManagerClient.invokeAPI(startsWith("/v1/groups/type/"), eq("GET"), any(), any(), any(), any(), any(), any(),
        any(), any(), any()))
        .thenReturn(new ApiResponse<>(200, Collections.emptyMap(),
            new GroupList().addDataItem(firstReadGroup).pagination(new Pagination().cursors(new PaginationCursors().after(nextPage)))))
        .thenReturn(new ApiResponse<>(200, Collections.emptyMap(),
            new GroupList().addDataItem(secondReadGroup).pagination(new Pagination())));

    final Status status = Status.ACTIVE;
    final SortOrder sortOrder = SortOrder.ASC;
    final int chunkSize = 1;
    final List<ReadGroup> groups = groupService.listAllGroups(status, sortOrder, chunkSize, 10).collect(Collectors.toList());

    assertEquals(Arrays.asList(firstReadGroup, secondReadGroup), groups);

    verifyClientCalled(profileManagerClient, "GET", "/v1/groups/type/SDL",
        Arrays.asList(new Pair("status", status.getValue()), new Pair("limit", Integer.toString(chunkSize)), new Pair("sortOrder", sortOrder.getValue())));
    verifyClientCalled(profileManagerClient, "GET", "/v1/groups/type/SDL",
        Arrays.asList(new Pair("status", status.getValue()), new Pair("after", nextPage), new Pair("limit", Integer.toString(chunkSize)), new Pair("sortOrder", sortOrder.getValue())));
  }

  @Test
  void testAddMemberToGroup() throws ApiException {
    final ReadGroup groupToReturn = new ReadGroup();
    when(profileManagerClient.invokeAPI(startsWith("/v1/groups/"), eq("POST"), any(), any(), any(), any(), any(), any(),
        any(), any(), any()))
        .thenReturn(new ApiResponse<>(200, Collections.emptyMap(), groupToReturn));

    final String groupId = "gid";
    final long userId = 12987981103203L;

    final ReadGroup readGroup = groupService.addMemberToGroup(groupId, userId);

    assertEquals(groupToReturn, readGroup);

    final AddMember expectedMember = new AddMember().member(new Member().memberId(userId).memberTenant(189));
    verifyClientCalledWithBody(profileManagerClient, "POST", "/v1/groups/" + groupId + "/member", expectedMember);
  }

  @Test
  void testUnauthorizedTriggersRefresh() throws ApiException {
    final ReadGroup groupToReturn = new ReadGroup();
    when(profileManagerClient.invokeAPI(startsWith("/v1/groups/"), eq("GET"), any(), any(), any(), any(), any(), any(),
        any(), any(), any()))
        .thenThrow(new ApiException(401, "unauthorized"))
        .thenReturn(new ApiResponse<>(200, Collections.emptyMap(), groupToReturn));

    final String groupId = "gid";
    assertEquals(groupToReturn, groupService.getGroup(groupId));

    verify(profileManagerClient, times(2)).invokeAPI(eq("/v1/groups/" + groupId), eq("GET"), any(), any(), any(), any(), any(), any(), any(), any(),
        any());
    verify(loginClient, times(2)).invokeAPI(eq("/idm/tokens"), eq("POST"), any(), any(), any(), any(), any(), any(), any(), any(),
        any());
  }

  @Test
  void testRetriesExhausted() throws ApiException {
    when(profileManagerClient.invokeAPI(startsWith("/v1/groups/"), eq("GET"), any(), any(), any(), any(), any(), any(),
        any(), any(), any()))
        .thenThrow(new ApiException(501, "Error"));

    final String groupId = "gid";
    assertThrows(ApiRuntimeException.class, () -> groupService.getGroup(groupId));

    verify(profileManagerClient, times(2)).invokeAPI(eq("/v1/groups/" + groupId), eq("GET"), any(), any(), any(), any(), any(), any(), any(), any(),
        any());
  }

  private static RetryWithRecoveryBuilder ofMinimalInterval(int maxAttempts) {
    BdkRetryConfig retryConfig = new BdkRetryConfig();
    retryConfig.setMultiplier(1.0);
    retryConfig.setInitialIntervalMillis(10L);
    retryConfig.setMaxIntervalMillis(10L);
    retryConfig.setMaxAttempts(maxAttempts);

    return new RetryWithRecoveryBuilder().retryConfig(retryConfig);
  }

  private void verifyClientCalled(ApiClient client, String method, String path) throws ApiException {
    verify(client, times(1)).invokeAPI(eq(path), eq(method), any(), any(), any(), any(), any(), any(), any(), any(),
        any());
  }

  private void verifyClientCalled(ApiClient client, String method, String path, List<Pair> headerParams) throws ApiException {
    verify(client, times(1)).invokeAPI(eq(path), eq(method), eq(headerParams), any(), any(), any(), any(), any(), any(), any(),
        any());
  }

  private void verifyClientCalledWithBody(ApiClient client, String method, String path, Object body) throws ApiException {
    verify(client, times(1)).invokeAPI(eq(path), eq(method), any(), eq(body), any(), any(), any(), any(), any(), any(),
        any());
  }

  private void verifyClientCalledWithHeaders(ApiClient client, String method, String path, Map<String, String> headerParams) throws ApiException {
    verify(client, times(1)).invokeAPI(eq(path), eq(method), any(), any(), eq(headerParams), any(), any(), any(), any(), any(),
        any());
  }
}
