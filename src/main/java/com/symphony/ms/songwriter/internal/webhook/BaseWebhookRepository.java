package com.symphony.ms.songwriter.internal.webhook;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.symphony.ms.songwriter.internal.webhook.model.BaseWebhook;

@Repository
public interface BaseWebhookRepository<T extends BaseWebhook> extends MongoRepository<T, String> {

  Optional<T> findByHash(String hash);

}
