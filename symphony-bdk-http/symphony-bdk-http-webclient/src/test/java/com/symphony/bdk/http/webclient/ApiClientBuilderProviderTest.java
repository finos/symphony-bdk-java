package com.symphony.bdk.http.webclient;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.symphony.bdk.http.api.ApiClientBuilder;

import org.junit.jupiter.api.Test;

public class ApiClientBuilderProviderTest {

  private final ApiClientBuilderProviderWebClient provider = new ApiClientBuilderProviderWebClient();

  @Test
  void newInstanceTest() {
    ApiClientBuilder builder = provider.newInstance();

    assertEquals(builder.getClass(), ApiClientBuilderWebClient.class);
  }
}
