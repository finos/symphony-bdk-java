package com.symphony.bdk.core.service.pagination.model;

import lombok.Getter;

/**
 * Stream Pagination Attribute model to be used in stream pagination methods provided by bdk services class.
 */
@Getter
public class StreamPaginationAttribute {

  private final Integer chunkSize;
  private final Integer totalSize;

  public StreamPaginationAttribute(Integer chunkSize, Integer totalSize) {
    if (chunkSize == null || totalSize == null) {
      throw new IllegalArgumentException("Chunk size and total size for stream pagination have to be not null.");
    }

    this.chunkSize = chunkSize;
    this.totalSize = totalSize;
  }
}
