package com.symphony.bdk.vsm.poll;

import com.symphony.bdk.gen.api.model.V3RoomDetail;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PollService {

  private final PollRepository repository;

  public Poll createPoll(final V4User creator, final V3RoomDetail stream, final V4Message creationMessage) {
    final Poll newPoll = new Poll();
    newPoll.setStatus(PollStatus.IN_CREATION);
    newPoll.setRoomId(stream.getRoomSystemInfo().getId());
    newPoll.setRoomName(stream.getRoomAttributes().getName());
    newPoll.setUserId(creator.getUserId());
    newPoll.setCreationMessageId(creationMessage.getMessageId());
    return this.repository.save(newPoll);
  }

  public Poll findFromCreationMessage(final String creationMessageId) {
    return this.repository.findOneByCreationMessageId(creationMessageId)
        .orElseThrow(() -> new IllegalStateException("No poll found for message " + creationMessageId));
  }

  public void save(Poll poll) {
    this.repository.save(poll);
  }
}
