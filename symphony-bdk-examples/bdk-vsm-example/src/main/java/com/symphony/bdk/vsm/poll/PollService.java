package com.symphony.bdk.vsm.poll;

import com.symphony.bdk.gen.api.model.V3RoomDetail;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PollService {

  private final PollRepository pollRepository;
  private final VoteRepository voteRepository;

  public Poll createPoll(final V4User creator, final V3RoomDetail stream, final V4Message creationMessage) {
    final Poll newPoll = new Poll();
    newPoll.setStatus(PollStatus.IN_CREATION);
    newPoll.setRoomId(stream.getRoomSystemInfo().getId());
    newPoll.setRoomName(stream.getRoomAttributes().getName());
    newPoll.setUserId(creator.getUserId());
    newPoll.setCreationMessageId(creationMessage.getMessageId());
    return this.pollRepository.save(newPoll);
  }

  public Poll findFromCreationMessage(final String creationMessageId) {
    return this.pollRepository.findOneByCreationMessageId(creationMessageId)
        .orElseThrow(() -> new IllegalStateException("No poll found for message " + creationMessageId));
  }

  public Poll findById(long pollId) {
    return this.pollRepository.findById(pollId)
        .orElseThrow(() -> new IllegalStateException("No poll found with id=" + pollId));
  }

  public void save(Poll poll) {
    this.pollRepository.save(poll);
  }

  public Vote vote(Poll poll, long voterId, String choice) {
    final Vote vote = new Vote();
    vote.setPoll(poll);
    vote.setUserId(voterId);
    vote.setValue(choice);

    return this.voteRepository.save(vote);
  }

  public String getVoteValue(Poll poll, Vote vote) {
    if ("option1".equals(vote.getValue())) {
      return poll.getOption1();
    } else if ("option2".equals(vote.getValue())) {
      return poll.getOption2();
    } else if ("option3".equals(vote.getValue())) {
      return poll.getOption3();
    } else if ("option4".equals(vote.getValue())) {
      return poll.getOption4();
    }
    return null;
  }
}
