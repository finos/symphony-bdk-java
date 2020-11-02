package com.symphony.bdk.core.service.pagination;

import com.symphony.bdk.core.service.pagination.model.StreamPaginationAttribute;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.apiguardian.api.API;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Retrieve a list of elements given a {@link PaginatedApi}.
 * Goal is to fetch elements lazily.
 * @param <T> the type of objects to retrieve
 */
@API(status = API.Status.INTERNAL)
public class PaginatedService<T> {
  private final PaginatedApi<T> paginatedApi;
  private final int chunkSize;
  private final int maxSize;

  /**
   * Get the valid pagination attribute.
   * This method returns default chunk size and default total size if these attributes equal null or the given {@link StreamPaginationAttribute} is null.
   *
   * @param pagination        the {@link StreamPaginationAttribute} to be validated.
   * @param defaultChunkSize  default chunkSize to be set if chunkSize is null.
   * @param defaultTotalSize  default totalSize to be set if totalSize is null.
   * @return the valid {@link StreamPaginationAttribute}.
   */
  public static StreamPaginationAttribute getValidPaginationAttribute(StreamPaginationAttribute pagination, Integer defaultChunkSize, Integer defaultTotalSize) {

    if (pagination != null) {
      final int actualChunkSize = pagination.chunkSize() == null ? defaultChunkSize : pagination.chunkSize();
      final int actualTotalSize = pagination.totalSize() == null ? defaultTotalSize : pagination.totalSize();

      return pagination.chunkSize(actualChunkSize).totalSize(actualTotalSize);
    }
    return new StreamPaginationAttribute().chunkSize(defaultChunkSize).totalSize(defaultTotalSize);
  }

  /**
   * The only constructor
   *
   * @param paginatedApi the paginated api used to retrieve the chunks of elements
   * @param chunkSize the maximum number to retrieve in one call of {@link PaginatedApi#get(int, int)}
   * @param maxSize the maximum number to retrieve in total
   */
  public PaginatedService(PaginatedApi<T> paginatedApi, int chunkSize, int maxSize) {
    checkSizes(chunkSize, maxSize);
    this.paginatedApi = paginatedApi;
    this.chunkSize = chunkSize;
    this.maxSize = maxSize;
  }

  private void checkSizes(int chunkSize, int maxSize) {
    if (chunkSize <= 0) {
      throw new IllegalArgumentException("chunkSize must be a strict positive integer");
    }
    if (maxSize < 0) {
      throw new IllegalArgumentException("maxSize must be a positive integer");
    }
  }

  /**
   * Returns an iterator of elements with lazy fetching.
   *
   * @return an {@link Iterator} which lazily makes calls to the {@link PaginatedApi}.
   */
  public Iterator<T> iterator() {
    return new PaginatedIterator();
  }

  /**
   * Returns an stream of elements with lazy fetching.
   *
   * @return a {@link Stream} which lazily makes calls to the {@link PaginatedApi}.
   */
  public Stream<T> stream() {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
  }

  @API(status = API.Status.INTERNAL)
  private class PaginatedIterator implements Iterator<T> {

    private List<T> currentChunk;
    private int remainingItemsInChunk;
    private int currentIndexInChunk;
    private int currentIndexInTotal;
    private int currentOffset;

    public PaginatedIterator() {
      currentChunk = Collections.emptyList();
      currentIndexInChunk = -1;
      currentIndexInTotal = -1;
      remainingItemsInChunk = 0;
      currentOffset = 0;
    }

    @Override
    public boolean hasNext() {
      //we already fetched maxSize items
      if (currentIndexInTotal == maxSize - 1) {
        return false;
      }

      //we didn't fetch maxSize items and we have remaining items in the chunk
      if (remainingItemsInChunk != 0) {
        return true;
      }

      //no remaining items in chunk but chunk was already smaller than chunkSize: we are already at the end
      if (currentIndexInTotal != -1 && currentChunk.size() < chunkSize) {
        return false;
      }

      //no remaining items in chunk, let's fetch a new one
      fetchNewChunk();

      return remainingItemsInChunk != 0;
    }

    private void fetchNewChunk() {
      try {
        currentChunk = paginatedApi.get(currentOffset, chunkSize);
        remainingItemsInChunk = currentChunk == null ? 0 : currentChunk.size();
        currentIndexInChunk = -1;
        currentOffset += chunkSize;
      } catch (ApiException e) {
        throw new ApiRuntimeException(e);
      }
    }

    @Override
    public T next() {
      currentIndexInTotal++;
      currentIndexInChunk++;
      remainingItemsInChunk--;
      return currentChunk.get(currentIndexInChunk);
    }
  }

}
