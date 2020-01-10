package com.symphony.ms.bot.sdk.internal.symphony.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.StreamInfo;
import model.StreamListItem;

import java.util.Collections;
import java.util.List;

/**
 * Symphony stream data
 *
 * @author Gabriel Berberian
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SymphonyStream {

  private String streamId;
  private Boolean crossPod;
  private String origin;
  private Boolean active;
  private Long lastMessageDate;
  private StreamType streamType;
  private List<Long> members;
  private String roomName;

  public SymphonyStream(StreamInfo streamInfo) {
    this.streamId = streamInfo.getId();
    this.crossPod = streamInfo.getCrossPod();
    this.origin = streamInfo.getOrigin();
    this.active = streamInfo.getActive();
    this.lastMessageDate = streamInfo.getLastMessageDate();
    this.streamType = StreamType.value(streamInfo.getStreamType().toString());
    this.members = streamInfo.getStreamAttributes().getMembers();
    this.roomName = streamInfo.getRoomAttributes().getName();
  }

  public SymphonyStream(StreamListItem streamListItem) {
    this.streamId = streamListItem.getId();
    this.crossPod = streamListItem.getCrossPod();
    this.active = streamListItem.getActive();
    this.streamType = streamListItem.getStreamType() != null ?
        StreamType.value(streamListItem.getStreamType().getType()) : StreamType.UNKNOWN;
    this.members = streamListItem.getStreamAttributes() != null ?
        streamListItem.getStreamAttributes().getMembers() : Collections.emptyList();
    this.roomName = streamListItem.getRoomAttributes() != null ?
        streamListItem.getRoomAttributes().getName() : null;
  }
}