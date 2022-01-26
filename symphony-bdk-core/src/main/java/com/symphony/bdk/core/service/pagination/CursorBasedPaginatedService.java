package com.symphony.bdk.core.service.pagination;

import com.symphony.bdk.core.service.pagination.model.CursorPaginatedPayload;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@API(status = API.Status.INTERNAL)
public class CursorBasedPaginatedService<T> extends PaginatedService {
  private CursorBasedPaginatedApi<T> paginatedApi;

  /**
   * @param paginatedApi the paginated api to be called
   * @param chunkSize    the max number of items to be retrieved in one call
   * @param maxSize      the total max number of items to be retrieved
   */
  public CursorBasedPaginatedService(CursorBasedPaginatedApi<T> paginatedApi, Integer chunkSize, Integer maxSize) {
    super(chunkSize, maxSize);
    this.paginatedApi = paginatedApi;
  }

  /**
   * Returns an stream of elements with lazy fetching.
   *
   * @return a {@link Stream} which lazily makes calls to the {@link #paginatedApi}.
   */
  public Stream<T> stream() {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new PaginatedIterator(), Spliterator.ORDERED), false);
  }

  @API(status = API.Status.INTERNAL)
  private class PaginatedIterator implements Iterator<T> {

    private CursorPaginatedPayload<T> currentPayload;
    private List<T> currentChunk;
    private int fetchedItems = 0;

    @Override
    public boolean hasNext() {
      if (fetchedItems == maxSize) {
        return false;
      }
      if (currentPayload == null) {
        fetchOneChunk(null);
      } else if (currentChunk.isEmpty() && currentPayload.getNext() != null) {
        fetchOneChunk(currentPayload.getNext());
      }

      return !currentChunk.isEmpty();
    }

    private void fetchOneChunk(String after) {
      try {
        currentPayload = paginatedApi.get(after, chunkSize);
        currentChunk = new ArrayList<>(currentPayload.getData());
      } catch (ApiException e) {
        throw new ApiRuntimeException(e);
      }
    }

    @Override
    public T next() {
      fetchedItems++;
      return currentChunk.remove(0);
    }
  }
}
