package com.symphony.bdk.core.service.clientdata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.gen.api.model.ClientDataResponse;
import com.symphony.bdk.gen.api.model.DistributionLists;
import com.symphony.bdk.gen.api.model.ClientDataStructure;
import com.symphony.bdk.http.api.ApiClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class ClientDataServiceTest {

  private static final String SESSION_TOKEN = "test-session-token";
  private static final String CLIENT_DATA_PATH = "/v5/users/clientdata";

  private static final String CLIENT_DATA_JSON = "{\n"
      + "  \"folders\": [\n"
      + "    {\"id\": \"folder-27001\", \"name\": \"Other Teams\", \"streamIds\": [\"stream-a\", \"stream-b\"]}\n"
      + "  ],\n"
      + "  \"workspaces\": [\n"
      + "    {\"id\": \"tab-1001\", \"name\": \"SRE Workspace\", \"streamIds\": [\"sre-stream-1\", \"sre-stream-2\"]}\n"
      + "  ],\n"
      + "  \"distributionLists\": [\n"
      + "    {\"id\": \"a97be35a\", \"name\": \"Blast People\", \"recipients\": ["
      + "       {\"id\": \"user-1\", \"type\": \"user\"},"
      + "       {\"id\": \"blast-stream-1\", \"type\": \"stream\"}]}\n"
      + "  ]\n"
      + "}";

  private MockApiClient mockApiClient;
  private AuthSession authSession;
  private ClientDataService service;

  @BeforeEach
  void setUp() {
    this.mockApiClient = new MockApiClient();
    this.authSession = mock(AuthSession.class);
    when(this.authSession.getSessionToken()).thenReturn(SESSION_TOKEN);

    ApiClient usersClient = this.mockApiClient.getApiClient("");
    this.service = new ClientDataService(usersClient, this.authSession, new RetryWithRecoveryBuilder<>());
  }

  @Test
  void testGetClientDataSuccess() {
    this.mockApiClient.onGet(CLIENT_DATA_PATH, CLIENT_DATA_JSON);

    ClientDataResponse org = this.service.getClientData();
    assertNotNull(org);

    assertEquals(1, org.getFolders().size());
    ClientDataStructure dir = org.getFolders().get(0);
    assertEquals("folder-27001", dir.getId());
    assertEquals("Other Teams", dir.getName());
    assertEquals(List.of("stream-a", "stream-b"), dir.getStreamIds());

    assertEquals(1, org.getWorkspaces().size());
    ClientDataStructure ws = org.getWorkspaces().get(0);
    assertEquals("tab-1001", ws.getId());
    assertEquals("SRE Workspace", ws.getName());
    assertEquals(List.of("sre-stream-1", "sre-stream-2"), ws.getStreamIds());

    assertEquals(1, org.getDistributionLists().size());
    DistributionLists bl = org.getDistributionLists().get(0);
    assertEquals("a97be35a", bl.getId());
    assertEquals("Blast People", bl.getName());
    assertEquals(2, bl.getRecipients().size());
    assertEquals("user-1", bl.getRecipients().get(0).getId());
    assertEquals("user", bl.getRecipients().get(0).getType());
    assertEquals("blast-stream-1", bl.getRecipients().get(1).getId());
    assertEquals("stream", bl.getRecipients().get(1).getType());
  }

  @Test
  void testGetUserFolders() {
    this.mockApiClient.onGet(CLIENT_DATA_PATH, CLIENT_DATA_JSON);

    List<ClientDataStructure> folders = this.service.getUserFolders();
    assertEquals(1, folders.size());
    assertEquals("folder-27001", folders.get(0).getId());
    assertEquals(List.of("stream-a", "stream-b"), folders.get(0).getStreamIds());
  }

  @Test
  void testGetUserDirectoriesBackwardsCompatibility() {
    this.mockApiClient.onGet(CLIENT_DATA_PATH, CLIENT_DATA_JSON);

    List<ClientDataStructure> directories = this.service.getUserDirectories();
    assertEquals(1, directories.size());
    assertEquals("folder-27001", directories.get(0).getId());
    assertEquals(List.of("stream-a", "stream-b"), directories.get(0).getStreamIds());
  }

  @Test
  void testGetUserWorkspaces() {
    this.mockApiClient.onGet(CLIENT_DATA_PATH, CLIENT_DATA_JSON);

    List<ClientDataStructure> workspaces = this.service.getUserWorkspaces();
    assertEquals(1, workspaces.size());
    assertEquals("tab-1001", workspaces.get(0).getId());
    assertEquals("SRE Workspace", workspaces.get(0).getName());
  }

  @Test
  void testGetUserDistributionLists() {
    this.mockApiClient.onGet(CLIENT_DATA_PATH, CLIENT_DATA_JSON);

    List<DistributionLists> distributionLists = this.service.getUserDistributionLists();
    assertEquals(1, distributionLists.size());
    assertEquals("a97be35a", distributionLists.get(0).getId());
    assertEquals("Blast People", distributionLists.get(0).getName());
    assertEquals(2, distributionLists.get(0).getRecipients().size());
    assertEquals("stream", distributionLists.get(0).getRecipients().get(1).getType());
  }

  @Test
  void testEmptyResponseReturnsEmptyLists() {
    this.mockApiClient.onGet(CLIENT_DATA_PATH, "{}");

    ClientDataResponse org = this.service.getClientData();
    assertNotNull(org);
    assertTrue(org.getFolders().isEmpty());
    assertTrue(org.getWorkspaces().isEmpty());
    assertTrue(org.getDistributionLists().isEmpty());
  }

  @Test
  void testOboModeExecution() {
    AuthSession oboSession = mock(AuthSession.class);
    when(oboSession.getSessionToken()).thenReturn("obo-token");

    OboClientDataService oboService = this.service.obo(oboSession);
    assertNotNull(oboService);

    this.mockApiClient.onGet(CLIENT_DATA_PATH, CLIENT_DATA_JSON);
    List<DistributionLists> distributionLists = oboService.getUserDistributionLists();
    assertEquals(1, distributionLists.size());
    assertEquals("a97be35a", distributionLists.get(0).getId());
  }

  @Test
  void testMissingAuthSessionThrowsIllegalStateException() {
    ApiClient usersClient = this.mockApiClient.getApiClient("");
    ClientDataService unauthenticatedService =
        new ClientDataService(usersClient, new RetryWithRecoveryBuilder<>());

    assertThrows(IllegalStateException.class, unauthenticatedService::getClientData);
  }
}
