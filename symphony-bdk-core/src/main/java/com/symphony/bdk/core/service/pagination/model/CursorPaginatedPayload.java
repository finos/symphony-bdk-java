package com.symphony.bdk.core.service.pagination.model;

import org.apiguardian.api.API;

import java.util.List;

/**
 * Interface describing a given page of data returned by a cursor-based paginated endpoint.
 *
 * @param <T> the type of a data item in a page
 */
@API(status = API.Status.INTERNAL)
public interface CursorPaginatedPayload<T> {

  /**
   *
   * @return the reference to the next page of results
   */
  String getNext();

  /**
   *
   * @return the list of items in the page
   */
  List<T> getData();
}
