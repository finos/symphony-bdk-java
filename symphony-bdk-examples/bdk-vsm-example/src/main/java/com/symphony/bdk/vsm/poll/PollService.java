package com.symphony.bdk.vsm.poll;

import com.symphony.bdk.gen.api.model.V3RoomDetail;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

  public boolean hasInProgressPoll(Long userId, String streamId) {
    return this.pollRepository.findOneByUserIdAndRoomIdAndStatusIn(
        userId, streamId, Arrays.asList(PollStatus.IN_CREATION, PollStatus.PENDING)).isPresent();
  }

  public Optional<Poll> findActiveByUserAndStream(Long userId, String streamId) {
    return this.pollRepository.findOneByUserIdAndRoomIdAndStatusIn(
        userId, streamId, Collections.singletonList(PollStatus.PENDING));
  }

  public Map<String, Integer> end(Poll poll) {
    poll.setStatus(PollStatus.FINISHED);
    final Map<String, Integer> results = new HashMap<>();

    results.put(poll.getOption1(), computeVotesForOption("option1", poll.getVotes()));
    results.put(poll.getOption2(), computeVotesForOption("option2", poll.getVotes()));
    if (poll.getOption3() != null) {
      results.put(poll.getOption3(), computeVotesForOption("option3", poll.getVotes()));
    }
    if (poll.getOption4() != null) {
      results.put(poll.getOption4(), computeVotesForOption("option4", poll.getVotes()));
    }

    return results;
  }

  private static int computeVotesForOption(String value, List<Vote> votes) {
    return (int) votes.stream().filter(v -> v.getValue().equals(value)).count();
  }
}
