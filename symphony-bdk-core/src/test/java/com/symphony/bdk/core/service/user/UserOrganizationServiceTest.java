package com.symphony.bdk.core.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.user.model.UserOrganization;
import com.symphony.bdk.core.service.user.model.UserOrganization.Directory;
import com.symphony.bdk.core.service.user.model.UserOrganization.DistributionList;
import com.symphony.bdk.core.service.user.model.UserOrganization.Workspace;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.http.api.ApiClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class UserOrganizationServiceTest {

  private static final String SESSION_TOKEN = "test-session-token";
  private static final String ROOT_PATH = "/webcontroller/clientdata/GET/symphonyPrime/";
  private static final String LEFTNAV_PATH = "/webcontroller/clientdata/GET/symphonyPrime/leftnav";
  private static final String WORKSPACES_PATH = "/webcontroller/clientdata/GET/symphonyPrime/canvasState";
  private static final String DISTRO_LISTS_PATH = "/webcontroller/clientdata/GET/symphonyPrime/C2/distributionLists";

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
  void testGetUserOrganizationSuccessWithRootSnapshot() {
    String rootJson = "{\n"
        + "  \"status\": \"OK\",\n"
        + "  \"document\": {\n"
        + "    \"leftnav\": {\n"
        + "      \"groups\": {\n"
        + "        \"conversations\": [\n"
        + "          \"raw-stream-1\",\n"
        + "          {\n"
        + "            \"id\": \"folder-27001\",\n"
        + "            \"name\": \"Other Teams\",\n"
        + "            \"items\": [\"stream-a\", \"stream-b\"]\n"
        + "          }\n"
        + "        ]\n"
        + "      }\n"
        + "    },\n"
        + "    \"canvasState\": {\n"
        + "      \"savedTabs\": [\n"
        + "        {\n"
        + "          \"id\": \"tab-1001\",\n"
        + "          \"title\": \"SRE Workspace\",\n"
        + "          \"isWorkspace\": true,\n"
        + "          \"layoutId\": \"layout-sre\"\n"
        + "        }\n"
        + "      ]\n"
        + "    },\n"
        + "    \"dynamicLayouts\": {\n"
        + "      \"layout-sre\": {\n"
        + "        \"dynamicModules\": [\n"
        + "          [\n"
        + "            {\n"
        + "              \"type\": \"chat\",\n"
        + "              \"props\": {\"id\": \"sre-stream-1\", \"item\": {\"name\": \"Pierre\"}}\n"
        + "            },\n"
        + "            {\n"
        + "              \"type\": \"chat\",\n"
        + "              \"props\": {\"id\": \"sre-stream-2\", \"item\": {\"name\": \"Cedric\"}}\n"
        + "            }\n"
        + "          ]\n"
        + "        ]\n"
        + "      }\n"
        + "    }\n"
        + "  }\n"
        + "}";

    String distroJson = "{\n"
        + "  \"status\": \"OK\",\n"
        + "  \"document\": {\n"
        + "    \"a97be35a-a9a8-48c0-9863-3e697f76c9d5\": {\n"
        + "      \"id\": \"a97be35a-a9a8-48c0-9863-3e697f76c9d5\",\n"
        + "      \"name\": \"Blast People\",\n"
        + "      \"recipients\": [\n"
        + "        {\"id\": \"699220675788807\", \"type\": \"user\"},\n"
        + "        {\"id\": \"bPCpxk9mbzZnFvrGAYY6T3///mDhvvDwdA==\", \"type\": \"stream\"}\n"
        + "      ]\n"
        + "    }\n"
        + "  }\n"
        + "}";

    this.mockApiClient.onGet(ROOT_PATH, rootJson);
    this.mockApiClient.onGet(DISTRO_LISTS_PATH, distroJson);

    UserOrganization org = this.service.getUserOrganization();
    assertNotNull(org);

    // Assert directories
    assertEquals(1, org.getDirectories().size());
    Directory dir = org.getDirectories().get(0);
    assertEquals("folder-27001", dir.getId());
    assertEquals("Other Teams", dir.getName());
    assertEquals(List.of("stream-a", "stream-b"), dir.getStreamIds());

    // Assert workspaces
    assertEquals(1, org.getWorkspaces().size());
    Workspace ws = org.getWorkspaces().get(0);
    assertEquals("tab-1001", ws.getId());
    assertEquals("SRE Workspace", ws.getName());
    assertEquals(List.of("sre-stream-1", "sre-stream-2"), ws.getStreamIds());

    // Assert distribution lists
    assertEquals(1, org.getDistributionLists().size());
    DistributionList dl = org.getDistributionLists().get(0);
    assertEquals("a97be35a-a9a8-48c0-9863-3e697f76c9d5", dl.getId());
    assertEquals("Blast People", dl.getName());
    assertEquals(List.of("bPCpxk9mbzZnFvrGAYY6T3///mDhvvDwdA=="), dl.getStreamIds());
    assertEquals(2, dl.getRecipients().size());
    assertEquals("699220675788807", dl.getRecipients().get(0).getId());
    assertEquals("user", dl.getRecipients().get(0).getType());
    assertEquals("bPCpxk9mbzZnFvrGAYY6T3///mDhvvDwdA==", dl.getRecipients().get(1).getId());
    assertEquals("stream", dl.getRecipients().get(1).getType());
  }

  @Test
  void testGetUserDirectoriesSubpath() {
    String leftNavJson = "{\n"
        + "  \"status\": \"OK\",\n"
        + "  \"document\": {\n"
        + "    \"groups\": {\n"
        + "      \"conversations\": [\n"
        + "        \"chat-pinned-1\",\n"
        + "        {\n"
        + "          \"id\": \"folder-1\",\n"
        + "          \"name\": \"My Folder\",\n"
        + "          \"items\": [\"s1\", \"s2\"]\n"
        + "        }\n"
        + "      ]\n"
        + "    }\n"
        + "  }\n"
        + "}";

    this.mockApiClient.onGet(LEFTNAV_PATH, leftNavJson);

    List<Directory> directories = this.service.getUserDirectories();
    assertEquals(1, directories.size());
    assertEquals("folder-1", directories.get(0).getId());
    assertEquals("My Folder", directories.get(0).getName());
    assertEquals(List.of("s1", "s2"), directories.get(0).getStreamIds());
  }

  @Test
  void testGetUserDistributionLists() {
    String distroJson = "{\n"
        + "  \"status\": \"OK\",\n"
        + "  \"document\": {\n"
        + "    \"list-1\": {\n"
        + "      \"id\": \"list-1\",\n"
        + "      \"name\": \"VIP List\",\n"
        + "      \"recipients\": [\n"
        + "        {\"id\": \"user-1\", \"type\": \"user\"},\n"
        + "        {\"id\": \"stream-1\", \"type\": \"stream\"}\n"
        + "      ]\n"
        + "    }\n"
        + "  }\n"
        + "}";

    this.mockApiClient.onGet(DISTRO_LISTS_PATH, distroJson);

    List<DistributionList> lists = this.service.getUserDistributionLists();
    assertEquals(1, lists.size());
    assertEquals("list-1", lists.get(0).getId());
    assertEquals("VIP List", lists.get(0).getName());
    assertEquals(List.of("stream-1"), lists.get(0).getStreamIds());
  }

  @Test
  void testGetWorkspacesFallbackToCustomWorkspaces() {
    String fallbackJson = "{\n"
        + "  \"status\": \"OK\",\n"
        + "  \"document\": {\n"
        + "    \"customWorkspaces\": [\n"
        + "      {\n"
        + "        \"id\": \"ws-1\",\n"
        + "        \"name\": \"Legacy Workspace\",\n"
        + "        \"items\": [\"s-100\", \"s-200\"]\n"
        + "      }\n"
        + "    ]\n"
        + "  }\n"
        + "}";

    this.mockApiClient.onGet(ROOT_PATH, fallbackJson);

    List<Workspace> workspaces = this.service.getUserWorkspaces();
    assertEquals(1, workspaces.size());
    assertEquals("ws-1", workspaces.get(0).getId());
    assertEquals("Legacy Workspace", workspaces.get(0).getName());
    assertEquals(List.of("s-100", "s-200"), workspaces.get(0).getStreamIds());
  }

  @Test
  void test404ReturnsEmptyGracefully() {
    this.mockApiClient.onGet(404, ROOT_PATH, "{}");
    this.mockApiClient.onGet(404, LEFTNAV_PATH, "{}");
    this.mockApiClient.onGet(404, WORKSPACES_PATH, "{}");
    this.mockApiClient.onGet(404, DISTRO_LISTS_PATH, "{}");

    UserOrganization org = this.service.getUserOrganization();
    assertNotNull(org);
    assertTrue(org.getDirectories().isEmpty());
    assertTrue(org.getWorkspaces().isEmpty());
    assertTrue(org.getDistributionLists().isEmpty());

    assertTrue(this.service.getUserDirectories().isEmpty());
    assertTrue(this.service.getUserWorkspaces().isEmpty());
    assertTrue(this.service.getUserDistributionLists().isEmpty());
  }

  @Test
  void testOboModeExecution() {
    AuthSession oboSession = mock(AuthSession.class);
    when(oboSession.getSessionToken()).thenReturn("obo-token");

    UserOrganizationService oboService = this.service.obo(oboSession);
    assertNotNull(oboService);

    String distroJson = "{\n"
        + "  \"status\": \"OK\",\n"
        + "  \"document\": {\n"
        + "    \"list-obo\": {\n"
        + "      \"id\": \"list-obo\",\n"
        + "      \"name\": \"OBO List\",\n"
        + "      \"recipients\": [{\"id\": \"stream-obo\", \"type\": \"stream\"}]\n"
        + "    }\n"
        + "  }\n"
        + "}";

    this.mockApiClient.onGet(DISTRO_LISTS_PATH, distroJson);
    List<DistributionList> lists = oboService.getUserDistributionLists();
    assertEquals(1, lists.size());
    assertEquals("list-obo", lists.get(0).getId());
  }

  @Test
  void testMissingAuthSessionThrowsIllegalStateException() {
    ApiClient usersClient = this.mockApiClient.getApiClient("");
    UserOrganizationService unauthenticatedService = new UserOrganizationService(usersClient, new RetryWithRecoveryBuilder<>());

    assertThrows(IllegalStateException.class, unauthenticatedService::getUserOrganization);
  }

  @Test
  void testParseDistributionListsWithUserProvidedSample() {
    String sample = "{\n"
        + "    \"status\": \"OK\",\n"
        + "    \"document\": {\n"
        + "        \"a97be35a-a9a8-48c0-9863-3e697f76c9d5\": {\n"
        + "            \"id\": \"a97be35a-a9a8-48c0-9863-3e697f76c9d5\",\n"
        + "            \"name\": \"Blast People\",\n"
        + "            \"recipients\": [\n"
        + "                {\n"
        + "                    \"id\": \"699220675788807\",\n"
        + "                    \"type\": \"user\"\n"
        + "                },\n"
        + "                {\n"
        + "                    \"id\": \"699220675788806\",\n"
        + "                    \"type\": \"user\"\n"
        + "                },\n"
        + "                {\n"
        + "                    \"id\": \"bPCpxk9mbzZnFvrGAYY6T3///mDhvvDwdA==\",\n"
        + "                    \"type\": \"stream\"\n"
        + "                }\n"
        + "            ]\n"
        + "        }\n"
        + "    }\n"
        + "}";

    this.mockApiClient.onGet(DISTRO_LISTS_PATH, sample);

    List<DistributionList> result = this.service.getUserDistributionLists();
    assertEquals(1, result.size());
    DistributionList dl = result.get(0);
    assertEquals("a97be35a-a9a8-48c0-9863-3e697f76c9d5", dl.getId());
    assertEquals("Blast People", dl.getName());
    assertEquals(1, dl.getStreamIds().size());
    assertEquals("bPCpxk9mbzZnFvrGAYY6T3///mDhvvDwdA==", dl.getStreamIds().get(0));
    assertEquals(3, dl.getRecipients().size());
    assertEquals("699220675788807", dl.getRecipients().get(0).getId());
    assertEquals("user", dl.getRecipients().get(0).getType());
    assertEquals("699220675788806", dl.getRecipients().get(1).getId());
    assertEquals("user", dl.getRecipients().get(1).getType());
    assertEquals("bPCpxk9mbzZnFvrGAYY6T3///mDhvvDwdA==", dl.getRecipients().get(2).getId());
    assertEquals("stream", dl.getRecipients().get(2).getType());
  }

  @Test
  void testParseDirectoriesWithPrimeJsonSnippet() {
    String primeSnippet = "{\n"
        + "  \"status\": \"OK\",\n"
        + "  \"document\": {\n"
        + "    \"leftnav\": {\n"
        + "      \"groups\": {\n"
        + "        \"conversations\": [\n"
        + "          \"mQGBc6YxSXe4VymtQpVdA3///qZraOq2dA==\",\n"
        + "          {\n"
        + "            \"id\": \"folder-27001\",\n"
        + "            \"name\": \"Other Teams\",\n"
        + "            \"items\": [\n"
        + "              \"hBYiyjJxRpyRgy5y3nBQgX///rNLnbBjdA==\",\n"
        + "              \"Xak486Jkcco5WOQkHlgZBn///rMC6YWkdA==\",\n"
        + "              \"/UwvEK2mMofbtrhFw5NY83///p3g/tb2dA==\"\n"
        + "            ]\n"
        + "          },\n"
        + "          {\n"
        + "            \"id\": \"folder-2008\",\n"
        + "            \"name\": \"RANDOM\",\n"
        + "            \"items\": []\n"
        + "          },\n"
        + "          \"/Mc+INWneo2r0A3efYhAKn///pYciXMrdA==\"\n"
        + "        ]\n"
        + "      }\n"
        + "    }\n"
        + "  }\n"
        + "}";

    this.mockApiClient.onGet(LEFTNAV_PATH, primeSnippet);

    List<Directory> directories = this.service.getUserDirectories();
    assertEquals(2, directories.size());

    Directory d1 = directories.get(0);
    assertEquals("folder-27001", d1.getId());
    assertEquals("Other Teams", d1.getName());
    assertEquals(3, d1.getStreamIds().size());
    assertTrue(d1.getStreamIds().contains("hBYiyjJxRpyRgy5y3nBQgX///rNLnbBjdA=="));

    Directory d2 = directories.get(1);
    assertEquals("folder-2008", d2.getId());
    assertEquals("RANDOM", d2.getName());
    assertTrue(d2.getStreamIds().isEmpty());
  }
}
