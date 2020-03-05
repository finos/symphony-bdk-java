package com.symphony.bot.sdk.internal.symphony.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.NumericId;
import model.RoomSearchQuery;

import java.util.List;

/**
 * Symphony room search query
 *
 * @author Gabriel Berberian
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SymphonyRoomSearchQuery {

  private String query;
  private List<String> labels;
  private Boolean active;
  private Boolean privateRoom;
  private NumericId creator;
  private NumericId owner;
  private NumericId member;
  private int skip;
  private int limit;

  public SymphonyRoomSearchQuery(RoomSearchQuery roomSearchQuery, int skip, int limit) {
    this.active = roomSearchQuery.getActive();
    this.labels = roomSearchQuery.getLabels();
    this.creator = roomSearchQuery.getCreator();
    this.privateRoom = roomSearchQuery.getPrivate();
    this.member = roomSearchQuery.getMember();
    this.owner = roomSearchQuery.getOwner();
    this.query = roomSearchQuery.getQuery();
    this.skip = skip;
    this.limit = limit;
  }

}
