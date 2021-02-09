package com.symphony.bdk.vsm.poll;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PollRepository extends CrudRepository<Poll, Long> {

  Optional<Poll> findOneByCreationMessageId(String creationMessageId);
}
