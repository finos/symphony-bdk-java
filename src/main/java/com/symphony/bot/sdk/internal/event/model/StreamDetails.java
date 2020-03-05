package com.symphony.bot.sdk.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.Stream;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.symphony.bot.sdk.internal.symphony.model.StreamType;

/**
 * Symphony stream details
 *
 * @author Gabriel Berberian
 */
@Data
@NoArgsConstructor
public class StreamDetails {

  private StreamType streamType;
  private String roomName;
  private List<UserDetails> members;
  private Boolean external;
  private Boolean crossPod;

  public StreamDetails(Stream stream) {
    this.streamType = StreamType.value(stream.getStreamType().toUpperCase());
    this.roomName = stream.getRoomName();
    this.members = stream.getMembers() == null ? Collections.emptyList()
        : stream.getMembers().stream().map(UserDetails::new).collect(Collectors.toList());
    this.external = stream.getExternal();
    this.crossPod = stream.getCrossPod();
  }

}
