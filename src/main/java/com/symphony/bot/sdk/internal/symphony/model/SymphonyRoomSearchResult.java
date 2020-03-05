package com.symphony.bot.sdk.internal.symphony.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.RoomSearchResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Symphony room search result
 *
 * @author Gabriel Berberian
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SymphonyRoomSearchResult {
  private int count;
  private int skip;
  private int limit;
  private SymphonyRoomSearchQuery query;
  private List<SymphonyRoom> rooms;

  public SymphonyRoomSearchResult(RoomSearchResult roomSearchResult) {
    this.count = roomSearchResult.getCount();
    this.skip = roomSearchResult.getSkip();
    this.limit = roomSearchResult.getLimit();
    this.query =
        new SymphonyRoomSearchQuery(roomSearchResult.getQuery(), roomSearchResult.getSkip(),
            roomSearchResult.getLimit());
    this.rooms =
        roomSearchResult.getRooms()
            .stream()
            .map(SymphonyRoom::new)
            .collect(Collectors.toList());
  }

}
