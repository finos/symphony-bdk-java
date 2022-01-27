package com.symphony.bdk.core.service.pagination;

import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
public abstract class PaginatedService {

  public static final int DEFAULT_PAGINATION_CHUNK_SIZE = 100;
  public static final int DEFAULT_PAGINATION_TOTAL_SIZE = 100;

  protected final int chunkSize;
  protected final int maxSize;

  protected PaginatedService(Integer chunkSize, Integer maxSize) {
    this.chunkSize = chunkSize == null ? PaginatedService.DEFAULT_PAGINATION_CHUNK_SIZE : chunkSize;
    this.maxSize = maxSize == null ? PaginatedService.DEFAULT_PAGINATION_TOTAL_SIZE : maxSize;

    checkSizes();
  }

  private void checkSizes() {
    if (chunkSize <= 0) {
      throw new IllegalArgumentException("chunkSize must be a strict positive integer");
    }
    if (maxSize < 0) {
      throw new IllegalArgumentException("maxSize must be a positive integer");
    }
  }
}
