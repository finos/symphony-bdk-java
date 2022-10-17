package com.symphony.bdk.core.service.pagination;

import com.symphony.bdk.core.service.pagination.model.CursorPaginatedPayload;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;

/**
 * Interface which abstracts a cursor-based paginated endpoint.
 *
 * @param <T> the type of a single item in the page returned by the endpoint.
 */
@FunctionalInterface
@API(status = API.Status.INTERNAL)
public interface CursorBasedPaginatedApi<T> {

  /**
   * Retrieves next page of items
   *
   * @param after the value of next page
   * @param limit the max number of items to fetch in the next page
   * @return the payload containing pagination information and the list of items in the next page
   * @throws ApiException
   */
  CursorPaginatedPayload<T> get(String after, int limit) throws ApiException;
}
