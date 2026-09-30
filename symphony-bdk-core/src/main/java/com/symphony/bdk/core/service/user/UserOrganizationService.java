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
import tools.jackson.databind.node.MissingNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
  private static final String DISTRO_LISTS_PATH = "C2/distributionLists";

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
      JsonNode primeDoc = fetchDocument("");
      JsonNode distroDoc = fetchDocument(DISTRO_LISTS_PATH);

      return new UserOrganization(
          parseDirectories(primeDoc),
          parseWorkspaces(primeDoc),
          parseDistributionLists(distroDoc)
      );
    });
  }

  /**
   * Retrieves the current user's custom directories/folders.
   *
   * @return a list of {@link Directory} instances.
   */
  public List<Directory> getUserDirectories() {
    return executeAndRetry("getUserDirectories", () -> {
      JsonNode doc = fetchDocument("leftnav");
      if (doc.isMissingNode()) {
        doc = fetchDocument("");
      }
      return parseDirectories(doc);
    });
  }

  /**
   * Retrieves the current user's workspaces.
   *
   * @return a list of {@link Workspace} instances.
   */
  public List<Workspace> getUserWorkspaces() {
    return executeAndRetry("getUserWorkspaces", () -> parseWorkspaces(fetchDocument("")));
  }

  /**
   * Retrieves the current user's blast distribution lists.
   *
   * @return a list of {@link DistributionList} instances.
   */
  public List<DistributionList> getUserDistributionLists() {
    return executeAndRetry("getUserDistributionLists", () -> parseDistributionLists(fetchDocument(DISTRO_LISTS_PATH)));
  }

  /**
   * Parses custom folders/directories from leftnav JSON.
   */
  public static List<Directory> parseDirectories(JsonNode doc) {
    List<Directory> directories = new ArrayList<>();
    JsonNode leftnav = doc.has("leftnav") ? doc.get("leftnav") : doc;
    for (JsonNode item : leftnav.path("groups").path("conversations")) {
      if (item.isObject()) {
        try {
          FolderDto folder = MAPPER.treeToValue(item, FolderDto.class);
          directories.add(new Directory(folder.id(), folder.name(), folder.items() != null ? folder.items() : Collections.emptyList()));
        } catch (JacksonException e) {
          log.warn("Failed to parse folder: {}", item, e);
        }
      }
    }
    return directories;
  }

  /**
   * Parses workspaces from canvasState and dynamicLayouts (or customWorkspaces fallback).
   */
  public static List<Workspace> parseWorkspaces(JsonNode doc) {
    List<Workspace> workspaces = new ArrayList<>();
    JsonNode savedTabs = doc.path("canvasState").path("savedTabs");
    JsonNode dynamicLayouts = doc.path("dynamicLayouts");

    for (JsonNode tab : savedTabs) {
      if (tab.path("isWorkspace").asBoolean(false) || tab.has("layoutId")) {
        String layoutId = tab.path("layoutId").asText("");
        List<String> streams = dynamicLayouts.path(layoutId).path("dynamicModules").findValues("props")
            .stream()
            .map(p -> p.path("id").asText(null))
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        workspaces.add(new Workspace(tab.path("id").asText(null), tab.path("title").asText(null), streams));
      }
    }

    if (workspaces.isEmpty()) {
      for (JsonNode ws : doc.path("customWorkspaces")) {
        if (ws.isObject()) {
          List<String> items = ws.path("items").values().stream().map(JsonNode::asText).toList();
          workspaces.add(new Workspace(ws.path("id").asText(null), ws.path("name").asText(null), items));
        }
      }
    }
    return workspaces;
  }

  /**
   * Parses distribution lists from C2/distributionLists response using Jackson DTO binding.
   */
  public static List<DistributionList> parseDistributionLists(JsonNode doc) {
    JsonNode distroNode = doc.path("C2").has("distributionLists") ? doc.path("C2").get("distributionLists") : doc;
    try {
      if (distroNode.isObject()) {
        Map<String, DistributionListDto> map = MAPPER.treeToValue(distroNode,
            new tools.jackson.core.type.TypeReference<Map<String, DistributionListDto>>() {});
        return map != null ? map.values().stream().map(DistributionListDto::toDomain).toList() : Collections.emptyList();
      } else if (distroNode.isArray()) {
        List<DistributionListDto> list = MAPPER.treeToValue(distroNode,
            new tools.jackson.core.type.TypeReference<List<DistributionListDto>>() {});
        return list != null ? list.stream().map(DistributionListDto::toDomain).toList() : Collections.emptyList();
      }
    } catch (JacksonException e) {
      log.warn("Failed to deserialize distribution lists", e);
    }
    return Collections.emptyList();
  }

  private JsonNode fetchDocument(String subPath) throws ApiException {
    Map<String, String> headerParams = new HashMap<>();
    if (this.authSession != null && this.authSession.getSessionToken() != null) {
      headerParams.put("sessionToken", this.authSession.getSessionToken());
    }

    String fullPath = CLIENT_DATA_ROOT + (subPath.isEmpty() ? "/" : "/" + subPath);
    try {
      ApiResponse<Object> response = this.apiClient.invokeAPI(
          fullPath, "GET", Collections.emptyList(), null, headerParams,
          Collections.emptyMap(), Collections.emptyMap(),
          "application/json", "application/json", new String[0], new TypeReference<Object>() {}
      );
      if (response != null && response.getData() != null) {
        Object data = response.getData();
        JsonNode root = (data instanceof String s) ? MAPPER.readTree(s) : MAPPER.valueToTree(data);
        return root.path("document");
      }
    } catch (ApiException e) {
      if (e.getCode() == 404) {
        return MissingNode.getInstance();
      }
      throw e;
    } catch (JacksonException e) {
      log.warn("Failed to parse client-data JSON for path: {}", fullPath, e);
    }
    return MissingNode.getInstance();
  }

  private <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    checkAuthSession(this.authSession);
    return RetryWithRecovery.executeAndRetry(this.retryBuilder, name, this.apiClient.getBasePath(), supplier);
  }

  /**
   * Internal DTO for Jackson folder binding.
   */
  @API(status = API.Status.EXPERIMENTAL)
  public record FolderDto(@Nullable String id, @Nullable String name, @Nullable List<String> items) {}

  /**
   * Internal DTO for Jackson distribution list binding.
   */
  @API(status = API.Status.EXPERIMENTAL)
  public record DistributionListDto(@Nullable String id, @Nullable String name, @Nullable List<Recipient> recipients) {
    public DistributionList toDomain() {
      List<Recipient> recs = this.recipients != null ? this.recipients : Collections.emptyList();
      List<String> streams = recs.stream()
          .filter(r -> "stream".equalsIgnoreCase(r.getType()) && r.getId() != null)
          .map(Recipient::getId)
          .toList();
      return new DistributionList(this.id, this.name, streams, recs);
    }
  }
}
