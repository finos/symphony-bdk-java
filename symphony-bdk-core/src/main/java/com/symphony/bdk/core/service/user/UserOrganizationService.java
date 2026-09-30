package com.symphony.bdk.core.service.user;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.retry.function.SupplierWithApiException;
import com.symphony.bdk.core.service.OboService;
import com.symphony.bdk.core.service.user.model.UserOrganization;
import com.symphony.bdk.core.service.user.model.UserOrganization.Directory;
import com.symphony.bdk.core.service.user.model.UserOrganization.DistributionList;
import com.symphony.bdk.core.service.user.model.UserOrganization.Recipient;
import com.symphony.bdk.core.service.user.model.UserOrganization.Workspace;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiResponse;
import com.symphony.bdk.http.api.util.TypeReference;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;
import org.jspecify.annotations.Nullable;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to retrieve user organization data, including custom folders/directories,
 * multi-chat workspaces, and blast distribution lists from Symphony client data.
 */
@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class UserOrganizationService implements OboService<UserOrganizationService> {

  private static final ObjectMapper MAPPER = JsonMapper.builder()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .build();

  private static final String CLIENT_DATA_ROOT = "/webcontroller/clientdata/GET/symphonyPrime";
  private static final String DISTRIBUTION_LISTS_SUBPATH = "C2/distributionLists";
  private static final String LEFTNAV_SUBPATH = "leftnav";
  private static final String CANVAS_STATE_SUBPATH = "canvasState";

  private final ApiClient apiClient;
  private final @Nullable AuthSession authSession;
  private final RetryWithRecoveryBuilder<?> retryBuilder;

  public UserOrganizationService(ApiClient apiClient, AuthSession authSession,
      RetryWithRecoveryBuilder<?> retryBuilder) {
    this.apiClient = apiClient;
    this.authSession = authSession;
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder)
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
  }

  public UserOrganizationService(ApiClient apiClient, RetryWithRecoveryBuilder<?> retryBuilder) {
    this.apiClient = apiClient;
    this.authSession = null;
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder);
  }

  @Override
  public UserOrganizationService obo(AuthSession oboSession) {
    return new UserOrganizationService(this.apiClient, oboSession, this.retryBuilder);
  }

  /**
   * Retrieves the current user's aggregated organization data (directories, workspaces, and distribution lists).
   *
   * @return a {@link UserOrganization} containing the user's organization data.
   */
  public UserOrganization getUserOrganization() {
    return executeAndRetry("getUserOrganization", () -> {
      JsonNode rootNode = fetchClientDataJson("");
      List<Directory> directories;
      List<Workspace> workspaces;

      if (rootNode != null) {
        directories = parseDirectories(rootNode);
        workspaces = parseWorkspaces(rootNode);
      } else {
        JsonNode leftNavNode = fetchClientDataJson(LEFTNAV_SUBPATH);
        directories = leftNavNode != null ? parseDirectories(leftNavNode) : Collections.emptyList();

        JsonNode canvasNode = fetchClientDataJson(CANVAS_STATE_SUBPATH);
        workspaces = canvasNode != null ? parseWorkspaces(canvasNode) : Collections.emptyList();
      }

      JsonNode distroNode = fetchClientDataJson(DISTRIBUTION_LISTS_SUBPATH);
      List<DistributionList> distributionLists = distroNode != null ? parseDistributionLists(distroNode) : Collections.emptyList();

      return new UserOrganization(directories, workspaces, distributionLists);
    });
  }

  /**
   * Retrieves the current user's custom directories/folders.
   *
   * @return a list of {@link Directory} instances.
   */
  public List<Directory> getUserDirectories() {
    return executeAndRetry("getUserDirectories", () -> {
      JsonNode leftNavNode = fetchClientDataJson(LEFTNAV_SUBPATH);
      if (leftNavNode == null) {
        leftNavNode = fetchClientDataJson("");
      }
      return leftNavNode != null ? parseDirectories(leftNavNode) : Collections.emptyList();
    });
  }

  /**
   * Retrieves the current user's workspaces.
   *
   * @return a list of {@link Workspace} instances.
   */
  public List<Workspace> getUserWorkspaces() {
    return executeAndRetry("getUserWorkspaces", () -> {
      JsonNode rootNode = fetchClientDataJson("");
      if (rootNode == null) {
        rootNode = fetchClientDataJson(CANVAS_STATE_SUBPATH);
      }
      return rootNode != null ? parseWorkspaces(rootNode) : Collections.emptyList();
    });
  }

  /**
   * Retrieves the current user's blast distribution lists.
   *
   * @return a list of {@link DistributionList} instances.
   */
  public List<DistributionList> getUserDistributionLists() {
    return executeAndRetry("getUserDistributionLists", () -> {
      JsonNode distroNode = fetchClientDataJson(DISTRIBUTION_LISTS_SUBPATH);
      return distroNode != null ? parseDistributionLists(distroNode) : Collections.emptyList();
    });
  }

  private <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    checkAuthSession(this.authSession);
    return RetryWithRecovery.executeAndRetry(this.retryBuilder, name, this.apiClient.getBasePath(), supplier);
  }

  private @Nullable JsonNode fetchClientDataJson(String subPath) throws ApiException {
    Map<String, String> headerParams = new HashMap<>();
    if (this.authSession != null && this.authSession.getSessionToken() != null) {
      headerParams.put("sessionToken", this.authSession.getSessionToken());
    }

    String fullPath = CLIENT_DATA_ROOT + (subPath.isEmpty() ? "/" : "/" + subPath);
    try {
      ApiResponse<Object> response = this.apiClient.invokeAPI(
          fullPath,
          "GET",
          Collections.emptyList(),
          null,
          headerParams,
          Collections.emptyMap(),
          Collections.emptyMap(),
          "application/json",
          "application/json",
          new String[0],
          new TypeReference<Object>() {}
      );
      if (response != null && response.getData() != null) {
        Object data = response.getData();
        if (data instanceof String s) {
          if (!s.trim().isEmpty()) {
            return MAPPER.readTree(s);
          }
        } else {
          return MAPPER.valueToTree(data);
        }
      }
    } catch (ApiException e) {
      if (e.getCode() == 404) {
        log.debug("No client-data found for path: {}", fullPath);
        return null;
      }
      throw e;
    } catch (JacksonException e) {
      log.warn("Failed to parse client-data JSON for path: {}", fullPath, e);
    }
    return null;
  }

  /**
   * Parses custom folders/directories from leftnav JSON.
   */
  public static List<Directory> parseDirectories(JsonNode root) {
    JsonNode doc = root.has("document") ? root.get("document") : root;
    JsonNode leftnav = doc.has("leftnav") ? doc.get("leftnav") : doc;

    List<Directory> directories = new ArrayList<>();
    for (JsonNode item : leftnav.path("groups").path("conversations")) {
      if (item.isObject()) {
        String id = item.path("id").asText(null);
        String name = item.path("name").asText(null);
        List<String> streamIds = extractStreamIds(item.path("items"));
        if (id != null || name != null || !streamIds.isEmpty()) {
          directories.add(new Directory(id, name, streamIds));
        }
      }
    }
    return directories;
  }

  /**
   * Parses workspaces from canvasState, dynamicLayouts, or customWorkspaces.
   */
  public static List<Workspace> parseWorkspaces(JsonNode root) {
    JsonNode doc = root.path("document");
    if (doc.isMissingNode()) doc = root;

    JsonNode savedTabs = doc.path("canvasState").path("savedTabs");
    JsonNode dynamicLayouts = doc.path("dynamicLayouts");

    List<Workspace> workspaces = new ArrayList<>();
    if (savedTabs.isArray() && !savedTabs.isEmpty()) {
      for (JsonNode tab : savedTabs) {
        if (tab.path("isWorkspace").asBoolean(false) || tab.has("layoutId")) {
          String id = tab.path("id").asText(null);
          String name = tab.hasNonNull("title") ? tab.get("title").asText() : tab.path("label").asText(null);
          String layoutId = tab.path("layoutId").asText(null);
          List<String> streamIds = new ArrayList<>();
          if (layoutId != null && dynamicLayouts.has(layoutId)) {
            extractStreams(dynamicLayouts.get(layoutId).path("dynamicModules"), streamIds);
          }
          if (id != null || name != null || !streamIds.isEmpty()) {
            workspaces.add(new Workspace(id, name, streamIds));
          }
        }
      }
    } else {
      JsonNode customWs = doc.has("customWorkspaces") ? doc.get("customWorkspaces") : doc.path("workspaces");
      for (JsonNode ws : customWs) {
        if (ws.isObject()) {
          List<String> streamIds = extractStreamIds(ws.path("items"));
          if (streamIds.isEmpty()) streamIds = extractStreamIds(ws.path("streams"));
          if (streamIds.isEmpty()) streamIds = extractStreamIds(ws.path("streamIds"));
          workspaces.add(new Workspace(ws.path("id").asText(null), ws.path("name").asText(null), streamIds));
        }
      }
    }
    return workspaces;
  }

  private static void extractStreams(JsonNode node, List<String> streamIds) {
    if (node == null || node.isMissingNode()) return;
    if (node.isArray()) {
      node.forEach(child -> extractStreams(child, streamIds));
    } else if (node.isObject() && ("chat".equalsIgnoreCase(node.path("type").asText()) || node.has("props"))) {
      String id = node.path("props").hasNonNull("id") ? node.path("props").get("id").asText()
          : (node.path("props").path("item").hasNonNull("id") ? node.path("props").path("item").get("id").asText()
          : node.path("viewId").asText(null));
      if (id != null && !streamIds.contains(id)) {
        streamIds.add(id);
      }
    }
  }

  private static List<String> extractStreamIds(JsonNode arrayNode) {
    List<String> streamIds = new ArrayList<>();
    if (arrayNode.isArray()) {
      for (JsonNode item : arrayNode) {
        String id = item.isObject() ? (item.hasNonNull("id") ? item.get("id").asText() : item.path("streamId").asText(null)) : item.asText(null);
        if (id != null) {
          streamIds.add(id);
        }
      }
    }
    return streamIds;
  }

  /**
   * Parses distribution lists from C2/distributionLists response using Jackson DTO binding.
   */
  public static List<DistributionList> parseDistributionLists(JsonNode root) {
    JsonNode doc = root.path("document");
    if (doc.isMissingNode()) doc = root;
    if (doc.has("C2") && doc.get("C2").has("distributionLists")) {
      doc = doc.get("C2").get("distributionLists");
    }

    try {
      if (doc.isObject()) {
        Map<String, RawDistributionList> map = MAPPER.treeToValue(doc,
            new tools.jackson.core.type.TypeReference<Map<String, RawDistributionList>>() {});
        return map != null ? map.values().stream().map(RawDistributionList::toDomain).toList() : Collections.emptyList();
      } else if (doc.isArray()) {
        List<RawDistributionList> list = MAPPER.treeToValue(doc,
            new tools.jackson.core.type.TypeReference<List<RawDistributionList>>() {});
        return list != null ? list.stream().map(RawDistributionList::toDomain).toList() : Collections.emptyList();
      }
    } catch (JacksonException e) {
      log.warn("Failed to deserialize distribution lists", e);
    }
    return Collections.emptyList();
  }

  /**
   * Internal DTO for Jackson direct binding from SymphonyPrime client-data payload.
   */
  @API(status = API.Status.EXPERIMENTAL)
  public record RawDistributionList(
      @Nullable String id,
      @Nullable String name,
      @Nullable List<Recipient> recipients,
      @Nullable List<String> items,
      @Nullable List<String> streamIds
  ) {
    public DistributionList toDomain() {
      List<Recipient> recs = this.recipients != null ? this.recipients : Collections.emptyList();
      List<String> streams = new ArrayList<>();
      for (Recipient r : recs) {
        if ("stream".equalsIgnoreCase(r.getType()) && r.getId() != null) {
          streams.add(r.getId());
        }
      }
      if (streams.isEmpty() && this.items != null) streams.addAll(this.items);
      if (streams.isEmpty() && this.streamIds != null) streams.addAll(this.streamIds);
      return new DistributionList(this.id, this.name, streams, recs);
    }
  }
}
