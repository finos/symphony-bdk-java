package com.symphony.bdk.cli.command.stream;

import com.symphony.bdk.gen.api.model.V3RoomAttributes;

import picocli.CommandLine.Option;

/**
 * Shared {@code create-room}/{@code update-room} attribute options, applied to a {@link
 * V3RoomAttributes} instance. Every field is left {@code null}/{@code false} unless the
 * corresponding flag is explicitly passed, so {@link #applyTo} only overrides attributes the user
 * asked for.
 */
class RoomAttributesOptions {

  @Option(names = "--description", description = "Room description.")
  String description;

  @Option(names = "--public", description = "Make the room public.")
  boolean publicFlag;

  @Option(names = "--private", description = "Make the room private.")
  boolean privateFlag;

  @Option(names = "--discoverable", description = "Allow non-participants to search and list the room.")
  Boolean discoverable;

  @Option(names = "--members-can-invite", description = "Allow any participant (not just owners) to add new members.")
  Boolean membersCanInvite;

  @Option(names = "--read-only", description = "Restrict sending messages to room owners.")
  Boolean readOnly;

  @Option(names = "--view-history", description = "Allow new members to view the room's chat history.")
  Boolean viewHistory;

  @Option(names = "--cross-pod", description = "Mark the room as a cross-pod room.")
  Boolean crossPod;

  void applyTo(V3RoomAttributes attributes) {
    if (description != null) {
      attributes.setDescription(description);
    }
    if (publicFlag) {
      attributes.setPublic(true);
    } else if (privateFlag) {
      attributes.setPublic(false);
    }
    if (discoverable != null) {
      attributes.setDiscoverable(discoverable);
    }
    if (membersCanInvite != null) {
      attributes.setMembersCanInvite(membersCanInvite);
    }
    if (readOnly != null) {
      attributes.setReadOnly(readOnly);
    }
    if (viewHistory != null) {
      attributes.setViewHistory(viewHistory);
    }
    if (crossPod != null) {
      attributes.setCrossPod(crossPod);
    }
  }
}
