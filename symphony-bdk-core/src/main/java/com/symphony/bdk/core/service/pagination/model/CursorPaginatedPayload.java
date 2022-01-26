package com.symphony.bdk.core.service.pagination.model;

import org.apiguardian.api.API;

import java.util.List;

@API(status = API.Status.INTERNAL)
public interface CursorPaginatedPayload<T> {
  String getNext();

  List<T> getData();
}
