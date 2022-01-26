package com.symphony.bdk.core.service.pagination;

import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
public abstract class PaginatedService {

  public static final int DEFAULT_PAGINATION_CHUNK_SIZE = 100;
  public static final int DEFAULT_PAGINATION_TOTAL_SIZE = 100;

  protected final int chunkSize;
  protected final int maxSize;

  protected PaginatedService(int chunkSize, int maxSize) {
    checkSizes(chunkSize, maxSize);
    this.chunkSize = chunkSize;
    this.maxSize = maxSize;
  }

  private static void checkSizes(int chunkSize, int maxSize) {
    if (chunkSize <= 0) {
      throw new IllegalArgumentException("chunkSize must be a strict positive integer");
    }
    if (maxSize < 0) {
      throw new IllegalArgumentException("maxSize must be a positive integer");
    }
  }
}
