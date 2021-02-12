package com.symphony.bdk.vsm.poll;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollRepository extends CrudRepository<Poll, Long> {

  Optional<Poll> findOneByCreationMessageId(String creationMessageId);

  Optional<Poll> findOneByUserIdAndRoomIdAndStatusIn(long userId, String roomId, List<PollStatus> statusList);
}
