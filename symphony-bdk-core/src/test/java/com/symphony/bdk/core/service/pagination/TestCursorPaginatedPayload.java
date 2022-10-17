package com.symphony.bdk.core.service.pagination;

import com.symphony.bdk.core.service.pagination.model.CursorPaginatedPayload;

import java.util.List;

public class TestCursorPaginatedPayload implements CursorPaginatedPayload<String> {
  private String next;
  private List<String> data;

  public TestCursorPaginatedPayload(String next, List<String> data) {
    this.next = next;
    this.data = data;
  }

  @Override
  public String getNext() {
    return next;
  }

  @Override
  public List<String> getData() {
    return data;
  }
}
