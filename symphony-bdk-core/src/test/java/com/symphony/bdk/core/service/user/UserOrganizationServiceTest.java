package com.symphony.bdk.core.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.gen.api.model.OrganisationStructure;
import com.symphony.bdk.gen.api.model.UserOrganisationsResponse;
import com.symphony.bdk.http.api.ApiClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class UserOrganizationServiceTest {

  private static final String SESSION_TOKEN = "test-session-token";
  private static final String ORGANISATIONS_PATH = "/v5/users/organisations";

  private static final String ORGANISATIONS_JSON = "{\n"
      + "  \"directories\": [\n"
      + "    {\"id\": \"folder-27001\", \"name\": \"Other Teams\", \"streamIds\": [\"stream-a\", \"stream-b\"]}\n"
      + "  ],\n"
      + "  \"workspaces\": [\n"
      + "    {\"id\": \"tab-1001\", \"name\": \"SRE Workspace\", \"streamIds\": [\"sre-stream-1\", \"sre-stream-2\"]}\n"
      + "  ],\n"
      + "  \"blastLists\": [\n"
      + "    {\"id\": \"a97be35a\", \"name\": \"Blast People\", \"streamIds\": [\"blast-stream-1\"]}\n"
      + "  ]\n"
      + "}";

  private MockApiClient mockApiClient;
  private AuthSession authSession;
  private UserOrganizationService service;

  @BeforeEach
  void setUp() {
    this.mockApiClient = new MockApiClient();
    this.authSession = mock(AuthSession.class);
    when(this.authSession.getSessionToken()).thenReturn(SESSION_TOKEN);

    ApiClient usersClient = this.mockApiClient.getApiClient("");
    this.service = new UserOrganizationService(usersClient, this.authSession, new RetryWithRecoveryBuilder<>());
  }

  @Test
  void testGetUserOrganizationSuccess() {
    this.mockApiClient.onGet(ORGANISATIONS_PATH, ORGANISATIONS_JSON);

    UserOrganisationsResponse org = this.service.getUserOrganization();
    assertNotNull(org);

    assertEquals(1, org.getDirectories().size());
    OrganisationStructure dir = org.getDirectories().get(0);
    assertEquals("folder-27001", dir.getId());
    assertEquals("Other Teams", dir.getName());
    assertEquals(List.of("stream-a", "stream-b"), dir.getStreamIds());

    assertEquals(1, org.getWorkspaces().size());
    OrganisationStructure ws = org.getWorkspaces().get(0);
    assertEquals("tab-1001", ws.getId());
    assertEquals("SRE Workspace", ws.getName());
    assertEquals(List.of("sre-stream-1", "sre-stream-2"), ws.getStreamIds());

    assertEquals(1, org.getBlastLists().size());
    OrganisationStructure bl = org.getBlastLists().get(0);
    assertEquals("a97be35a", bl.getId());
    assertEquals("Blast People", bl.getName());
    assertEquals(List.of("blast-stream-1"), bl.getStreamIds());
  }

  @Test
  void testGetUserDirectories() {
    this.mockApiClient.onGet(ORGANISATIONS_PATH, ORGANISATIONS_JSON);

    List<OrganisationStructure> directories = this.service.getUserDirectories();
    assertEquals(1, directories.size());
    assertEquals("folder-27001", directories.get(0).getId());
    assertEquals(List.of("stream-a", "stream-b"), directories.get(0).getStreamIds());
  }

  @Test
  void testGetUserWorkspaces() {
    this.mockApiClient.onGet(ORGANISATIONS_PATH, ORGANISATIONS_JSON);

    List<OrganisationStructure> workspaces = this.service.getUserWorkspaces();
    assertEquals(1, workspaces.size());
    assertEquals("tab-1001", workspaces.get(0).getId());
    assertEquals("SRE Workspace", workspaces.get(0).getName());
  }

  @Test
  void testGetUserDistributionLists() {
    this.mockApiClient.onGet(ORGANISATIONS_PATH, ORGANISATIONS_JSON);

    List<OrganisationStructure> blastLists = this.service.getUserDistributionLists();
    assertEquals(1, blastLists.size());
    assertEquals("a97be35a", blastLists.get(0).getId());
    assertEquals("Blast People", blastLists.get(0).getName());
    assertEquals(List.of("blast-stream-1"), blastLists.get(0).getStreamIds());
  }

  @Test
  void testEmptyResponseReturnsEmptyLists() {
    this.mockApiClient.onGet(ORGANISATIONS_PATH, "{}");

    UserOrganisationsResponse org = this.service.getUserOrganization();
    assertNotNull(org);
    assertTrue(org.getDirectories().isEmpty());
    assertTrue(org.getWorkspaces().isEmpty());
    assertTrue(org.getBlastLists().isEmpty());
  }

  @Test
  void testOboModeExecution() {
    AuthSession oboSession = mock(AuthSession.class);
    when(oboSession.getSessionToken()).thenReturn("obo-token");

    UserOrganizationService oboService = this.service.obo(oboSession);
    assertNotNull(oboService);

    this.mockApiClient.onGet(ORGANISATIONS_PATH, ORGANISATIONS_JSON);
    List<OrganisationStructure> blastLists = oboService.getUserDistributionLists();
    assertEquals(1, blastLists.size());
    assertEquals("a97be35a", blastLists.get(0).getId());
  }

  @Test
  void testMissingAuthSessionThrowsIllegalStateException() {
    ApiClient usersClient = this.mockApiClient.getApiClient("");
    UserOrganizationService unauthenticatedService =
        new UserOrganizationService(usersClient, new RetryWithRecoveryBuilder<>());

    assertThrows(IllegalStateException.class, unauthenticatedService::getUserOrganization);
  }
}
