package com.symphony.bdk.bot.sdk.symphony.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import model.RoomMember;

/**
 * Symphony room member
 *
 * @author Gabriel Berberian
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SymphonyRoomMember {

  private Long userId;
  private Boolean owner;
  private Long joinDate;

  public SymphonyRoomMember(RoomMember roomMember) {
    this.userId = roomMember.getId();
    this.owner = roomMember.getOwner();
    this.joinDate = roomMember.getJoinDate();
  }
}
