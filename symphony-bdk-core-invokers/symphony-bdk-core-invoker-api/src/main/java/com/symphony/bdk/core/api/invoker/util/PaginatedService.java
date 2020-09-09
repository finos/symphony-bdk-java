package com.symphony.bdk.core.api.invoker.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PaginatedService<T> {
  private PaginatedApi<T> paginatedApi;
  private int chunkSize; //max number of items to retrieve in one call
  private int maxSize; //max total number of items to retrieve

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

  public Iterator<T> iterator() {
    return new PaginatedIterator();
  }

  public Stream<T> stream() {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
  }

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
      currentChunk = paginatedApi.get(currentOffset, chunkSize);
      remainingItemsInChunk = currentChunk.size();
      currentIndexInChunk = -1;
      currentOffset += chunkSize;

      return remainingItemsInChunk != 0;
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
