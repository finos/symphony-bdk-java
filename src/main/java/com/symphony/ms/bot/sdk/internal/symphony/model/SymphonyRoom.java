package com.symphony.ms.bot.sdk.internal.symphony.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.Keyword;
import model.RoomInfo;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Symphony room data
 *
 * @author Gabriel Berberian
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SymphonyRoom {

  private String name;
  private String description;
  private Boolean membersCanInvite;
  private Boolean discoverable;
  private Boolean publicRoom;
  private Boolean readOnly;
  private Boolean copyProtected;
  private Boolean crossPod;
  private Boolean viewHistory;
  private Boolean multiLateralRoom;
  private Map<String, String> keywords;
  private String id;
  private long creationDate;
  private long createdByUserId;
  private boolean active;

  public SymphonyRoom(RoomInfo roomInfo) {
    this.name = roomInfo.getRoomAttributes().getName();
    this.description = roomInfo.getRoomAttributes().getDescription();
    this.membersCanInvite = roomInfo.getRoomAttributes().getMembersCanInvite();
    this.discoverable = roomInfo.getRoomAttributes().getDiscoverable();
    this.publicRoom = roomInfo.getRoomAttributes().getPublic();
    this.readOnly = roomInfo.getRoomAttributes().getReadOnly();
    this.copyProtected = roomInfo.getRoomAttributes().getCopyProtected();
    this.crossPod = roomInfo.getRoomAttributes().getCrossPod();
    this.viewHistory = roomInfo.getRoomAttributes().getViewHistory();
    this.multiLateralRoom = roomInfo.getRoomAttributes().getMultiLateralRoom();
    this.keywords = roomInfo.getRoomAttributes()
        .getKeywords()
        .stream()
        .collect(Collectors.toMap(Keyword::getKey, Keyword::getValue));
    this.id = roomInfo.getRoomSystemInfo().getId();
    this.creationDate = roomInfo.getRoomSystemInfo().getCreationDate();
    this.createdByUserId = roomInfo.getRoomSystemInfo().getCreatedByUserId();
    this.active = roomInfo.getRoomSystemInfo().isActive();
  }

}
