package com.symphony.bdk.http.jersey2;

import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.tracing.DistributedTracingContext;
import com.symphony.bdk.http.api.util.TypeReference;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CancellationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiClientJersey2Test {

  private ApiClientJersey2 apiClient;

  @Mock Client client;
  @Mock WebTarget target;
  @Mock Invocation.Builder builder;
  @Mock Response response;
  @Mock Response.StatusType statusInfo;

  @BeforeEach
  void init() {
    lenient().when(client.target(anyString())).thenReturn(target);
    lenient().when(target.request()).thenReturn(builder);
    lenient().when(builder.accept(anyString())).thenReturn(builder);
    lenient().when(builder.header(anyString(), any())).thenReturn(builder);
    lenient().when(builder.post(any(Entity.class))).thenReturn(response);
    lenient().when(response.getStatusInfo()).thenReturn(statusInfo);
    lenient().when(statusInfo.getStatusCode()).thenReturn(200);
    lenient().when(statusInfo.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);
    lenient().when(response.getHeaders()).thenReturn(new MultivaluedHashMap<>());
    this.apiClient = new ApiClientJersey2(client, "", Collections.emptyMap(), "");
    this.apiClient.getAuthentications().put("testAuth", headerParams -> headerParams.put("Authorization", "test"));
  }

  @Test
  void shouldClearTraceIdIfNotSet() throws ApiException {
    DistributedTracingContext.clear();
    this.doInvokeAPI();
    assertTrue(DistributedTracingContext.getTraceId().isEmpty());
  }

  @Test
  void shouldPreserveExistingTraceId() throws ApiException {
    String traceId = UUID.randomUUID().toString();
    DistributedTracingContext.setTraceId(traceId);
    this.doInvokeAPI();
    assertEquals(traceId, DistributedTracingContext.getTraceId());
  }

  @Test
  void shouldThrowCancellationExceptionIfThreadAlreadyInterrupted() {
    Thread.currentThread().interrupt();
    try {
      assertThrows(
          CancellationException.class,
          () -> this.doInvokeAPI()
      );
      assertTrue(Thread.currentThread().isInterrupted());
    } finally {
      Thread.interrupted(); // Clear interrupted status
    }
  }

  @Test
  void shouldThrowCancellationExceptionWhenProcessingExceptionWrapsInterruptedException() throws ApiException {
    lenient().doThrow(new jakarta.ws.rs.ProcessingException(new InterruptedException("Interrupted!")))
        .when(builder).post(any(Entity.class));

    try {
      assertThrows(
          CancellationException.class,
          () -> this.doInvokeAPI()
      );
      assertTrue(Thread.currentThread().isInterrupted());
    } finally {
      Thread.interrupted(); // Clear interrupted status
    }
  }

  @Test
  void shouldThrowCancellationExceptionWhenProcessingExceptionWrapsCancellationException() throws ApiException {
    lenient().doThrow(new jakarta.ws.rs.ProcessingException(new CancellationException("Cancelled!")))
        .when(builder).post(any(Entity.class));

    assertThrows(
        CancellationException.class,
        () -> this.doInvokeAPI()
    );
  }

  private void doInvokeAPI() throws ApiException {
    this.apiClient.invokeAPI(
        "/hello",
        HttpMethod.POST,
        Collections.emptyList(),
        null,
        new HashMap<>(),
        new HashMap<>(),
        new HashMap<>(),
        "application/json",
        "application/json",
        new String[] { "testAuth" },
        new TypeReference<String>() {}
    );
  }
}
