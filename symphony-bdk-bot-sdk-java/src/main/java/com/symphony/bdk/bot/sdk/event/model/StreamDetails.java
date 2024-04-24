package com.symphony.bdk.bot.sdk.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.Stream;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.symphony.bdk.bot.sdk.symphony.model.StreamType;

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
  private List<Integer> recipientTenantIds;

  public StreamDetails(Stream stream) {
    this.streamType = StreamType.value(stream.getStreamType().toUpperCase());
    this.roomName = stream.getRoomName();
    this.members = stream.getMembers() == null ? Collections.emptyList()
        : stream.getMembers().stream().map(UserDetails::new).collect(Collectors.toList());
    this.external = stream.getExternal();
    this.crossPod = stream.getCrossPod();
    this.recipientTenantIds = stream.getRecipientTenantIds();
  }

}
