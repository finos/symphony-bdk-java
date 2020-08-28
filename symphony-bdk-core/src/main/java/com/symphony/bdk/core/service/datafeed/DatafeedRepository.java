package com.symphony.bdk.core.service.datafeed;

/**
 * A repository interface for storing a datafeed id.
 * By using DatafeedServiceV1, the created datafeed id should be persisted manually on the BDK side.
 */
public interface DatafeedRepository {

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
  String read();
}
