package com.symphony.bdk.http.jdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.Pair;

import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pure-logic unit tests for {@link ApiClientJdk} methods that don't require a network round-trip: parameter
 * formatting, header selection, and authentication scheme handling. Network-facing behavior (request building,
 * response handling, multipart, tracing, exception translation) is covered by {@link ApiClientJdkTest}.
 */
class ApiClientJdkUnitTest {

  private final ApiClientJdk client =
      new ApiClientJdk(HttpClient.newHttpClient(), "http://base", new HashMap<>(), null, null, new ArrayList<>());

  @Test
  void getBasePath_returnsConfiguredBasePath() {
    assertEquals("http://base", this.client.getBasePath());
  }

  @Test
  void parameterToPairs_returnsEmptyList_whenNameOrValueMissing() {
    assertTrue(this.client.parameterToPairs("csv", null, "value").isEmpty());
    assertTrue(this.client.parameterToPairs("csv", "", "value").isEmpty());
    assertTrue(this.client.parameterToPairs("csv", "name", null).isEmpty());
  }

  @Test
  void parameterToPairs_returnsSinglePair_forNonCollectionValue() {
    List<Pair> pairs = this.client.parameterToPairs("csv", "name", "value");

    assertEquals(1, pairs.size());
    assertEquals("value", pairs.get(0).getValue());
  }

  @Test
  void parameterToPairs_returnsEmptyList_forEmptyCollection() {
    assertTrue(this.client.parameterToPairs("csv", "name", Collections.emptyList()).isEmpty());
  }

  @Test
  void parameterToPairs_multiFormat_returnsOnePairPerElement() {
    List<Pair> pairs = this.client.parameterToPairs("multi", "name", Arrays.asList("a", "b"));

    assertEquals(2, pairs.size());
    assertEquals("a", pairs.get(0).getValue());
    assertEquals("b", pairs.get(1).getValue());
  }

  @Test
  void parameterToPairs_csvFormat_joinsWithComma() {
    List<Pair> pairs = this.client.parameterToPairs("csv", "name", Arrays.asList("a", "b"));
    assertEquals("a,b", pairs.get(0).getValue());
  }

  @Test
  void parameterToPairs_defaultFormat_joinsWithComma() {
    List<Pair> pairs = this.client.parameterToPairs(null, "name", Arrays.asList("a", "b"));
    assertEquals("a,b", pairs.get(0).getValue());
  }

  @Test
  void parameterToPairs_ssvFormat_joinsWithSpace() {
    List<Pair> pairs = this.client.parameterToPairs("ssv", "name", Arrays.asList("a", "b"));
    assertEquals("a b", pairs.get(0).getValue());
  }

  @Test
  void parameterToPairs_tsvFormat_joinsWithTab() {
    List<Pair> pairs = this.client.parameterToPairs("tsv", "name", Arrays.asList("a", "b"));
    assertEquals("a\tb", pairs.get(0).getValue());
  }

  @Test
  void parameterToPairs_pipesFormat_joinsWithPipe() {
    List<Pair> pairs = this.client.parameterToPairs("pipes", "name", Arrays.asList("a", "b"));
    assertEquals("a|b", pairs.get(0).getValue());
  }

  @Test
  void selectHeaderAccept_returnsNull_whenAcceptsEmpty() {
    assertEquals(null, this.client.selectHeaderAccept(new String[0]));
  }

  @Test
  void selectHeaderAccept_prefersJsonMime() {
    assertEquals("application/json", this.client.selectHeaderAccept(new String[] {"text/plain", "application/json"}));
  }

  @Test
  void selectHeaderAccept_joinsAllAccepts_whenNoJsonMime() {
    assertEquals("text/plain,text/html", this.client.selectHeaderAccept(new String[] {"text/plain", "text/html"}));
  }

  @Test
  void selectHeaderContentType_returnsJson_whenContentTypesEmpty() {
    assertEquals("application/json", this.client.selectHeaderContentType(new String[0]));
  }

  @Test
  void selectHeaderContentType_prefersJsonMime() {
    assertEquals("application/json",
        this.client.selectHeaderContentType(new String[] {"text/plain", "application/json"}));
  }

  @Test
  void selectHeaderContentType_returnsFirst_whenNoJsonMime() {
    assertEquals("text/plain", this.client.selectHeaderContentType(new String[] {"text/plain", "text/html"}));
  }

  @Test
  void updateParamsForAuth_appliesNamedAuthentication() throws ApiException {
    Map<String, String> headers = new HashMap<>();
    this.client.getAuthentications().put("basic", h -> h.put("Authorization", "Basic xyz"));

    this.client.updateParamsForAuth(new String[] {"basic"}, headers);

    assertEquals("Basic xyz", headers.get("Authorization"));
  }

  @Test
  void updateParamsForAuth_appliesEnforcedAuthenticationScheme_evenWhenAuthNamesNull() throws ApiException {
    Map<String, String> headers = new HashMap<>();
    this.client.getAuthentications().put("enforced", h -> h.put("X-Enforced", "true"));
    this.client.addEnforcedAuthenticationScheme("enforced");

    this.client.updateParamsForAuth(null, headers);

    assertEquals("true", headers.get("X-Enforced"));
  }

  @Test
  void updateParamsForAuth_throwsRuntimeException_forUndefinedAuthentication() {
    assertThrows(RuntimeException.class,
        () -> this.client.updateParamsForAuth(new String[] {"unknown"}, new HashMap<>()));
  }

  @Test
  void updateParamsForAuth_doesNothing_whenNoAuthNamesAndNoEnforcedSchemes() throws ApiException {
    Map<String, String> headers = new HashMap<>();

    this.client.updateParamsForAuth(null, headers);

    assertTrue(headers.isEmpty());
  }
}
