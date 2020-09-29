package com.symphony.bdk.core.service.datafeed;

import org.apiguardian.api.API;

import java.util.Optional;

/**
 * A repository interface for storing a datafeed id.
 * By using {@link com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV1}, the created datafeed id should be persisted manually on the BDK side.
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
   * Read the persisted datafeed id from the storage.
   *
   * @return The persisted datafeed id.
   */
  Optional<String> read();
}
