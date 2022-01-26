package com.symphony.bdk.core.service.pagination;

import com.symphony.bdk.core.service.pagination.model.CursorPaginatedPayload;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;

@FunctionalInterface
@API(status = API.Status.INTERNAL)
public interface CursorBasedPaginatedApi<T> {
  CursorPaginatedPayload<T> get(String after, int limit) throws ApiException;
}
