package com.symphony.ms.songwriter.internal.lib.restclient;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.symphony.ms.songwriter.internal.lib.restclient.model.RestResponse;

public class RestClientImpl implements RestClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(RestClientImpl.class);

  private RestTemplate restTemplate;

  public RestClientImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public <T> RestResponse<T> getRequest(String url) {
    return doRequest(url, HttpMethod.GET, null);
  }

  @Override
  public <T> RestResponse<T> getRequest(String url, Map<String, String> headers) {
    return doRequest(url, HttpMethod.GET, headers);
  }

  @Override
  public <T, U> RestResponse<T> postRequest(String url, U body) {
    return doRequestWithBody(url, HttpMethod.POST, null, body);
  }

  @Override
  public <T, U> RestResponse<T> postRequest(String url, U body, Map<String, String> headers) {
    return doRequestWithBody(url, HttpMethod.POST, headers, body);
  }

  @Override
  public <T, U> RestResponse<T> putRequest(String url, U body) {
    return doRequestWithBody(url, HttpMethod.PUT, null, body);
  }

  @Override
  public <T, U> RestResponse<T> putRequest(String url, U body, Map<String, String> headers) {
    return doRequestWithBody(url, HttpMethod.PUT, headers, body);
  }

  @Override
  public <T> RestResponse<T> deleteRequest(String url) {
    return doRequest(url, HttpMethod.DELETE, null);
  }

  @Override
  public <T> RestResponse<T> deleteRequest(String url, Map<String, String> headers) {
    return doRequest(url, HttpMethod.DELETE, headers);
  }

  private <T> RestResponse<T> doRequest(String url, HttpMethod httpMethod,
     Map<String, String> headers) {
    HttpEntity<?> httpEntity = new HttpEntity<>(convertHeaders(headers));

    return internalDoRequest(url, httpMethod, httpEntity);
  }

  private <T, U> RestResponse<T> doRequestWithBody(String url,
      HttpMethod httpMethod, Map<String, String> headers, U body) {
    HttpEntity<?> httpEntity = new HttpEntity<>(body, convertHeaders(headers));

    return internalDoRequest(url, httpMethod, httpEntity);
  }

  // TODO: add circuit breaker support here
  private <T> RestResponse<T> internalDoRequest(String url, HttpMethod httpMethod,
      HttpEntity<?> httpEntity) {
    LOGGER.debug("Performing request {} {}", httpMethod, url);

    RestResponse<T> response = null;
    try {
      ResponseEntity<T> re = restTemplate.exchange(url, httpMethod, httpEntity,
          new ParameterizedTypeReference<T>() {});

      T body = re.hasBody() ? re.getBody() : null;
      response = new RestResponse<>(body,
          re.getHeaders().toSingleValueMap(), re.getStatusCodeValue());
    } catch (HttpClientErrorException | HttpServerErrorException reqEx) {
      LOGGER.error("Error executing request {} {}\n{}", httpMethod, url, reqEx);
      response = new RestResponse<>(
          reqEx.getResponseHeaders().toSingleValueMap(),
          reqEx.getRawStatusCode());
    } catch (RestClientException rce) {
      LOGGER.error("Unexpected error executing request{} {}\n{}", httpMethod, url, rce);
      throw new RestClientConnectionException();
    }

    return response;
  }

  private MultiValueMap<String, String> convertHeaders(Map<String, String> headers) {
    if (headers != null) {
      MultiValueMap<String, String> resttemplateHeaders = new LinkedMultiValueMap<>();

      headers.entrySet().stream()
      .forEach(e -> resttemplateHeaders.add(e.getKey(), e.getValue()));
      return resttemplateHeaders;
    }

    return null;
  }
}
