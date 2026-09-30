package com.symphony.bdk.core.service.user.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apiguardian.api.API;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Aggregates a user's personal organization data including directories/folders, multi-chat workspaces,
 * and blast distribution lists.
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(force = true)
@AllArgsConstructor
@API(status = API.Status.EXPERIMENTAL)
public class UserOrganization {

  private final List<Directory> directories;
  private final List<Workspace> workspaces;
  private final List<DistributionList> distributionLists;

  public List<Directory> getDirectories() {
    return directories != null ? directories : Collections.emptyList();
  }

  public List<Workspace> getWorkspaces() {
    return workspaces != null ? workspaces : Collections.emptyList();
  }

  public List<DistributionList> getDistributionLists() {
    return distributionLists != null ? distributionLists : Collections.emptyList();
  }

  /**
   * Represents a custom user directory/folder containing conversation stream IDs.
   */
  @Getter
  @ToString
  @EqualsAndHashCode
  @NoArgsConstructor(force = true)
  @AllArgsConstructor
  @API(status = API.Status.EXPERIMENTAL)
  public static class Directory {
    private final @Nullable String id;
    private final @Nullable String name;
    private final List<String> streamIds;

    public List<String> getStreamIds() {
      return streamIds != null ? streamIds : Collections.emptyList();
    }
  }

  /**
   * Represents a user workspace configuration with pinned or grouped conversation stream IDs.
   */
  @Getter
  @ToString
  @EqualsAndHashCode
  @NoArgsConstructor(force = true)
  @AllArgsConstructor
  @API(status = API.Status.EXPERIMENTAL)
  public static class Workspace {
    private final @Nullable String id;
    private final @Nullable String name;
    private final List<String> streamIds;

    public List<String> getStreamIds() {
      return streamIds != null ? streamIds : Collections.emptyList();
    }
  }

  /**
   * Represents a blast distribution list containing stream IDs and detailed recipients.
   */
  @Getter
  @ToString
  @EqualsAndHashCode
  @NoArgsConstructor(force = true)
  @AllArgsConstructor
  @API(status = API.Status.EXPERIMENTAL)
  public static class DistributionList {
    private final @Nullable String id;
    private final @Nullable String name;
    private final List<String> streamIds;
    private final List<Recipient> recipients;

    public DistributionList(@Nullable String id, @Nullable String name, List<String> streamIds) {
      this(id, name, streamIds, Collections.emptyList());
    }

    public List<String> getStreamIds() {
      return streamIds != null ? streamIds : Collections.emptyList();
    }

    public List<Recipient> getRecipients() {
      return recipients != null ? recipients : Collections.emptyList();
    }
  }

  /**
   * Represents a recipient in a blast distribution list (can be a user or a stream).
   */
  @Getter
  @ToString
  @EqualsAndHashCode
  @NoArgsConstructor(force = true)
  @AllArgsConstructor
  @API(status = API.Status.EXPERIMENTAL)
  public static class Recipient {
    private final @Nullable String id;
    private final @Nullable String type;
  }
}
