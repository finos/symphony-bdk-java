package com.symphony.bdk.core.service.pagination;

import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;

import java.util.List;

/**
 * Functional Interface to accommodate for API endpoints with pagination.
 * @param <T> the type of objects to retrieve
 */
@FunctionalInterface
@API(status = API.Status.INTERNAL)
public interface PaginatedApi<T> {
  /**
   * This makes a call with the provided offset and limit parameters.
   * @param offset the number of elements to skip.
   * @param limit the maximum number of elements to retrieve in one call
   * @return the list of retrieved objects
   * @throws ApiException
   */
  List<T> get(int offset, int limit) throws ApiException;
}
