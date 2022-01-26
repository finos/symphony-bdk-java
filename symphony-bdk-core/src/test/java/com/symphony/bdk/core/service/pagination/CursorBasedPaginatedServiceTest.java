package com.symphony.bdk.core.service.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.symphony.bdk.http.api.ApiException;

import com.symphony.bdk.http.api.ApiRuntimeException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class CursorBasedPaginatedServiceTest {

  @Mock
  private CursorBasedPaginatedApi<String> paginatedApi;

  @Test
  void testNegativeMaxSize() {
    assertThrows(IllegalArgumentException.class, () -> new CursorBasedPaginatedService<>(paginatedApi, 1, -1));
  }

  @Test
  void testZeroChunkSize() {
    assertThrows(IllegalArgumentException.class, () -> new CursorBasedPaginatedService<>(paginatedApi, 0, 1));
  }

  @Test
  void testZeroMaxSize() {
    assertEquals(0, getAllItems(1, 0).size());
  }

  @Test
  void testApiReturnsEmpty() throws ApiException {
    when(paginatedApi.get(any(), anyInt())).thenReturn(new TestCursorPaginatedPayload(null, Collections.emptyList()));

    final int chunkSize = 2;
    final List<String> items = getAllItems(chunkSize, 10);

    assertEquals(0, items.size());
    verify(paginatedApi, times(1)).get(null, chunkSize);
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testApiReturnsOneItem() throws ApiException {
    final List<String> data = Collections.singletonList("a");
    when(paginatedApi.get(any(), anyInt())).thenReturn(new TestCursorPaginatedPayload(null, data));

    final int chunkSize = 2;
    final List<String> result = getAllItems(chunkSize, 10);

    assertEquals(data, result);
    verify(paginatedApi, times(1)).get(null, chunkSize);
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testApiReturnsTwoItems() throws ApiException {
    final List<String> data = Arrays.asList("a", "b");
    when(paginatedApi.get(any(), anyInt())).thenReturn(new TestCursorPaginatedPayload(null, data));

    final int chunkSize = 2;
    final List<String> result = getAllItems(chunkSize, 10);

    assertEquals(data, result);
    verify(paginatedApi, times(1)).get(null, chunkSize);
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testApiReturnsTwoItemsSameValueAsMaxSize() throws ApiException {
    final List<String> data = Arrays.asList("a", "b");
    when(paginatedApi.get(any(), anyInt())).thenReturn(new TestCursorPaginatedPayload(null, data));

    final int chunkSize = 2;
    final List<String> result = getAllItems(chunkSize, 2);

    assertEquals(data, result);
    verify(paginatedApi, times(1)).get(null, chunkSize);
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testServiceReturnsAsManyItemsAsMaxSizeWithOneChunk() throws ApiException {
    final String value = "a";
    final List<String> data = Arrays.asList(value, "b");
    when(paginatedApi.get(any(), anyInt())).thenReturn(new TestCursorPaginatedPayload(null, data));

    final int chunkSize = 2;
    final List<String> result = getAllItems(chunkSize, 1);

    assertEquals(Collections.singletonList(value), result);
    verify(paginatedApi, times(1)).get(null, chunkSize);
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testServiceCallsSeveralChunks() throws ApiException {
    final String next = "next";
    when(paginatedApi.get(any(), anyInt()))
        .thenReturn(new TestCursorPaginatedPayload(next, Arrays.asList("a", "b")))
        .thenReturn(new TestCursorPaginatedPayload(null, Arrays.asList("c", "d")));

    final int chunkSize = 2;
    final List<String> result = getAllItems(chunkSize, 3);

    assertEquals(Arrays.asList("a", "b", "c"), result);
    verify(paginatedApi, times(1)).get(null, chunkSize);
    verify(paginatedApi, times(1)).get(next, chunkSize);
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testServiceCallsSeveralChunksNotAtTheEnd() throws ApiException {
    final String next = "next";
    when(paginatedApi.get(any(), anyInt()))
        .thenReturn(new TestCursorPaginatedPayload(next, Arrays.asList("a", "b")))
        .thenReturn(new TestCursorPaginatedPayload("", Arrays.asList("c", "d")));

    final int chunkSize = 2;
    final List<String> result = getAllItems(chunkSize, 4);

    assertEquals(Arrays.asList("a", "b", "c", "d"), result);
    verify(paginatedApi, times(1)).get(null, chunkSize);
    verify(paginatedApi, times(1)).get(next, chunkSize);
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testServiceCallsSeveralChunksWithLessItemsThanMaxSize() throws ApiException {
    final String next = "next";
    when(paginatedApi.get(any(), anyInt()))
        .thenReturn(new TestCursorPaginatedPayload(next, Arrays.asList("a", "b")))
        .thenReturn(new TestCursorPaginatedPayload(null, Arrays.asList("c", "d")));

    final int chunkSize = 2;
    final List<String> result = getAllItems(chunkSize, 5);

    assertEquals(Arrays.asList("a", "b", "c", "d"), result);
    verify(paginatedApi, times(1)).get(null, chunkSize);
    verify(paginatedApi, times(1)).get(next, chunkSize);
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testApiThrowsApiException() throws ApiException {
    when(paginatedApi.get(any(), anyInt())).thenThrow(new ApiException(500, "error"));

    final int chunkSize = 2;
    assertThrows(ApiRuntimeException.class, () -> getAllItems(chunkSize, 5));
    verify(paginatedApi, times(1)).get(null, chunkSize);
    verifyNoMoreInteractions(paginatedApi);
  }


  private List<String> getAllItems(int chunkSize, int maxSize) {
    return new CursorBasedPaginatedService<>(paginatedApi, chunkSize, maxSize)
        .stream()
        .collect(Collectors.toList());
  }
}
