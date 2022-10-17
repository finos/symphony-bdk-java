package com.symphony.bdk.core.service.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.symphony.bdk.http.api.ApiException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OffsetBasedPaginatedServiceTest {

  @Mock
  private OffsetBasedPaginatedApi<String> paginatedApi;

  @Test
  void testNegativeSizes() {
    assertThrows(IllegalArgumentException.class, () -> new OffsetBasedPaginatedService<>(paginatedApi, 1, -1));
    assertThrows(IllegalArgumentException.class, () -> new OffsetBasedPaginatedService<>(paginatedApi, 0, 0));
  }

  @Test
  void testNullChunkSize() {
    final int maxSize = 10;
    final OffsetBasedPaginatedService<String> paginatedService =
        new OffsetBasedPaginatedService<>(paginatedApi, null, maxSize);
    assertEquals(PaginatedService.DEFAULT_PAGINATION_CHUNK_SIZE, paginatedService.chunkSize);
    assertEquals(maxSize, paginatedService.maxSize);
  }

  @Test
  void testNullMaxSize() {
    final int chunkSize = 10;
    final OffsetBasedPaginatedService<String> paginatedService =
        new OffsetBasedPaginatedService<>(paginatedApi, chunkSize, null);
    assertEquals(chunkSize, paginatedService.chunkSize);
    assertEquals(PaginatedService.DEFAULT_PAGINATION_TOTAL_SIZE, paginatedService.maxSize);
  }

  @Test
  void testNonNullChunkSizeAndMaxSize() {
    final int chunkSize = 10;
    final int maxSize = 125;
    final OffsetBasedPaginatedService<String> paginatedService =
        new OffsetBasedPaginatedService<>(paginatedApi, chunkSize, maxSize);
    assertEquals(chunkSize, paginatedService.chunkSize);
    assertEquals(maxSize, paginatedService.maxSize);
  }

  @Test
  void testZeroMaxSize() {
    assertServiceProducesList(1, 0, Collections.emptyList());
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testApiReturnsEmpty() throws ApiException {
    when(paginatedApi.get(anyInt(), anyInt())).thenReturn(Collections.emptyList());

    assertServiceProducesList(1, 1, Collections.emptyList());
    verify(paginatedApi).get(0, 1);
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testApiReturnsOneElementWithChunkSizeOneAndMaxSizeOne() throws ApiException {
    when(paginatedApi.get(anyInt(), anyInt()))
        .thenReturn(Collections.singletonList("a"))
        .thenReturn(Collections.emptyList());

    final List<String> list = getList(new OffsetBasedPaginatedService<>(paginatedApi, 1, 1));

    assertEquals(Arrays.asList("a"), list);
    verify(paginatedApi).get(0, 1);
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testApiReturnsOneElementWithChunkSizeOneAndMaxSizeTwo() throws ApiException {
    when(paginatedApi.get(anyInt(), anyInt()))
        .thenReturn(Collections.singletonList("a"))
        .thenReturn(Collections.emptyList());

    assertServiceProducesList(1, 2, Arrays.asList("a"));
    verify(paginatedApi).get(0, 1);
    verify(paginatedApi).get(1, 1);
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testApiReturnsTwoElementsWithChunkSizeOneAndMaxSizeThree() throws ApiException {
    when(paginatedApi.get(anyInt(), anyInt()))
        .thenReturn(Collections.singletonList("a"))
        .thenReturn(Collections.singletonList("b"))
        .thenReturn(Collections.emptyList());

    assertServiceProducesList(1, 3, Arrays.asList("a", "b"));
    verify(paginatedApi).get(0, 1);
    verify(paginatedApi).get(1, 1);
    verify(paginatedApi).get(2, 1);
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testApiReturnsTwoElementsWithNullChunkSizeOneAndMaxSizeThree() throws ApiException {
    when(paginatedApi.get(anyInt(), anyInt()))
        .thenReturn(Collections.singletonList("a"))
        .thenReturn(Collections.singletonList("b"))
        .thenReturn(null);

    assertServiceProducesList(1, 3, Arrays.asList("a", "b"));
    verify(paginatedApi).get(0, 1);
    verify(paginatedApi).get(1, 1);
    verify(paginatedApi).get(2, 1);
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testApiReturnsOneElementWithChunkSizeTwoAndMaxSizeOne() throws ApiException {
    when(paginatedApi.get(anyInt(), anyInt()))
        .thenReturn(Collections.singletonList("a"))
        .thenReturn(Collections.emptyList());

    assertServiceProducesList(2, 1, Arrays.asList("a"));
    verify(paginatedApi).get(0, 2);
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testApiReturnsTwoElementsWithChunkSizeTwoAndMaxSizeOne() throws ApiException {
    when(paginatedApi.get(anyInt(), anyInt()))
        .thenReturn(Arrays.asList("a", "b"))
        .thenReturn(Collections.emptyList());

    assertServiceProducesList(2, 1, Arrays.asList("a"));
    verify(paginatedApi).get(0, 2);
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testApiReturnsThreeElementsWithChunkSizeTwoAndMaxSizeFour() throws ApiException {
    when(paginatedApi.get(anyInt(), anyInt()))
        .thenReturn(Arrays.asList("a", "b"))
        .thenReturn(Arrays.asList("c"))
        .thenReturn(Collections.emptyList());

    assertServiceProducesList(2, 4, Arrays.asList("a", "b", "c"));
    verify(paginatedApi).get(0, 2);
    verify(paginatedApi).get(2, 2);
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testApiReturnsThreeElementsWithChunkSizeTwoAndMaxSizeFive() throws ApiException {
    when(paginatedApi.get(anyInt(), anyInt()))
        .thenReturn(Arrays.asList("a", "b"))
        .thenReturn(Arrays.asList("c"))
        .thenReturn(Collections.emptyList());

    assertServiceProducesList(2, 5, Arrays.asList("a", "b", "c"));
    verify(paginatedApi).get(0, 2);
    verify(paginatedApi).get(2, 2);
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testNoElementFetched() throws ApiException {
    when(paginatedApi.get(anyInt(), anyInt()))
        .thenReturn(Arrays.asList("a"))
        .thenReturn(Arrays.asList("b"));

    final Stream<String> stream = new OffsetBasedPaginatedService<>(paginatedApi, 1, 2).stream();
    verifyNoMoreInteractions(paginatedApi);
  }

  @Test
  void testOneElementFetched() throws ApiException {
    when(paginatedApi.get(anyInt(), anyInt()))
        .thenReturn(Arrays.asList("a"))
        .thenReturn(Arrays.asList("b"));

    final Stream<String> stream = new OffsetBasedPaginatedService<>(paginatedApi, 1, 2).stream();
    stream.findFirst();

    verify(paginatedApi).get(0, 1);
    verifyNoMoreInteractions(paginatedApi);
  }

  private void assertServiceProducesList(int chunkSize, int maxSize, List<String> expected) {
    final List<String> list = getList(new OffsetBasedPaginatedService<>(paginatedApi, chunkSize, maxSize));

    assertEquals(expected, list);
  }

  private List<String> getList(OffsetBasedPaginatedService<String> paginatedService) {
    return paginatedService.stream().collect(Collectors.toList());
  }
}
