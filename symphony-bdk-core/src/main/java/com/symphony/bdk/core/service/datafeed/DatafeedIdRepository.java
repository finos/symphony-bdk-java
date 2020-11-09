package com.symphony.bdk.core.service.datafeed;

import org.apiguardian.api.API;

import java.util.Optional;

/**
 * A repository interface for storing a datafeed id.
 * By using {@link com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV1},
 * the created datafeed id and agent base url should be persisted manually on the BDK side.
 */
@API(status = API.Status.STABLE)
public interface DatafeedIdRepository {

  /**
   * Persists the created datafeed id into the storage.
   *
   * @param datafeedId the datafeed id to be persisted.
   */
  void write(String datafeedId);

  /**
   * Persists the created datafeed id and agent base path into the storage.
   *
   * @param datafeedId    the datafeed id to be persisted.
   * @param agentBasePath the agent base path (i.e. scheme, host, port, context path) to be persisted.
   */
  void write(String datafeedId, String agentBasePath);

  /**
   * Read the persisted datafeed id from the storage.
   *
   * @return The persisted datafeed id.
   */
  Optional<String> read();

  /**
   * Read the persisted agent base path from the storage.
   *
   * @return the persisted agent base path.
   */
  Optional<String> readAgentBasePath();
}
