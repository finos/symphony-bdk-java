package com.symphony.bdk.examples;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.service.message.model.SortDir;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.gen.api.model.MessageSearchQuery;
import com.symphony.bdk.gen.api.model.V4Message;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * This demonstrates a basic usage of the message service.
 */
@Slf4j
public class SearchMessageExampleMain {

  public static void main(String[] args) throws Exception {
    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    final List<V4Message> results = bdk.messages().searchMessages(
        new MessageSearchQuery()
            .fromDate(0L)
            .streamType("IM"),
        new PaginationAttribute(0, 1000),
        SortDir.ASC
    );

    log.info("Found {} messages:", results.size());
    for (V4Message result : results) {
       log.info("- {} -> {}", result.getMessageId(), result.getMessage());
    }
  }
}
