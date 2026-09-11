package com.symphony.bdk.core.service.pagination.model;

import lombok.Getter;
import org.apiguardian.api.API;

/**
 * Stream Pagination Attribute model to be used in stream pagination methods provided by bdk services class.
 */
@Getter
@API(status = API.Status.EXPERIMENTAL)
public class StreamPaginationAttribute {

  /**
   * Size of elements to retrieve in one call.
   */
  private final Integer chunkSize;

  /**
   * Total maximum number of messages to return.
   */
  private final Integer totalSize;

  public StreamPaginationAttribute(Integer chunkSize, Integer totalSize) {
    this.chunkSize = chunkSize;
    this.totalSize = totalSize;
  }
}
