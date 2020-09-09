package com.symphony.bdk.core.api.invoker.util;

import java.util.List;

@FunctionalInterface
public interface PaginatedApi<T> {
  List<T> get(int offset, int limit);
}
