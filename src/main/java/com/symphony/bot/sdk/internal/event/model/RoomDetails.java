package com.symphony.bot.sdk.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.Keyword;
import model.RoomProperties;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Symphony room details
 *
 * @author Gabriel Berberian
 */
@Data
@NoArgsConstructor
public class RoomDetails {

  private String name;
  private String description;
  private UserDetails creatorUser;
  private Long createdDate;
  private Boolean external;
  private Boolean crossPod;
  private Boolean isPublic;
  private Boolean copyProtected;
  private Boolean readOnly;
  private Boolean discoverable;
  private Boolean membersCanInvite;
  private Map<String, String> keywords;
  private Boolean canViewHistory;

  public RoomDetails(RoomProperties room) {
    this.name = room.getName();
    this.description = room.getDescription();
    this.creatorUser =
        room.getCreatorUser() != null ? new UserDetails(room.getCreatorUser()) : null;
    this.external = room.getExternal();
    this.crossPod = room.getCrossPod();
    this.isPublic = room.getPublic();
    this.copyProtected = room.getCopyProtected();
    this.readOnly = room.getReadOnly();
    this.discoverable = room.getDiscoverable();
    this.membersCanInvite = room.getMembersCanInvite();
    this.keywords =
        room.getKeywords().stream().collect(Collectors.toMap(Keyword::getKey, Keyword::getValue));
    this.canViewHistory = room.getCanViewHistory();
  }
}
