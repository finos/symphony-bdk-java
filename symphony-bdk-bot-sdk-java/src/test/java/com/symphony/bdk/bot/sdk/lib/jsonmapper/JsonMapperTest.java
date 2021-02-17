package com.symphony.bdk.bot.sdk.lib.jsonmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.bdk.bot.sdk.event.model.UserDetails;
import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class JsonMapperTest {
  private ObjectMapper objectMapper;
  private JsonMapper jsonMapper;
  private UserDetails userDetails;
  private String userDetailJson;

  @BeforeEach
  public void init() {
    objectMapper = spy(new ObjectMapper());
    jsonMapper = new JsonMapperImpl(objectMapper);
    userDetails = new UserDetails() {{
      setUserId(123456L);
      setUsername("Test User");
      setFirstName("John");
      setLastName("Doe");
      setDisplayName("Test User");
      setEmail("testuser@email.com");
    }};
    userDetailJson = "{"
        + "\"userId\":123456,\"email\":\"testuser@email.com\",\"firstName\":\"John\","
        + "\"lastName\":\"Doe\",\"displayName\":\"Test User\",\"username\":\"Test User\""
        + "}";
  }

  @Test
  public void toJsonStringTest() {
    String result = jsonMapper.toJsonString(userDetails);
    assertEquals(result, userDetailJson);
  }

  @Test
  public void toJsonStringListTest() {
    List<UserDetails> userDetailsList = new ArrayList<>();
    userDetailsList.add(userDetails);
    userDetailsList.add(
        userDetails = new UserDetails() {{
          setUserId(456123L);
          setUsername("Bot User");
          setFirstName("Bot");
          setLastName("User");
          setDisplayName("Bot User");
          setEmail("botuser@email.com");
        }});
    userDetailsList.add(
        userDetails = new UserDetails() {{
          setUserId(654321L);
          setUsername("Test 2");
          setFirstName("Test");
          setLastName("User");
          setDisplayName("Test 2");
          setEmail("testuser2@email.com");
        }});
    String result = jsonMapper.toJsonString(userDetailsList);
    assertEquals(result, "["
        + "{\"userId\":123456,\"email\":\"testuser@email.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"displayName\":\"Test User\",\"username\":\"Test User\"},"
        + "{\"userId\":456123,\"email\":\"botuser@email.com\",\"firstName\":\"Bot\",\"lastName\":\"User\",\"displayName\":\"Bot User\",\"username\":\"Bot User\"},"
        + "{\"userId\":654321,\"email\":\"testuser2@email.com\",\"firstName\":\"Test\",\"lastName\":\"User\",\"displayName\":\"Test 2\",\"username\":\"Test 2\"}"
        + "]");
  }

  @Test
  public void toJsonStringFailedTest() throws JsonProcessingException {
    when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
    assertThrows(JsonMapperException.class, () -> jsonMapper.toJsonString(userDetails));
  }

  @Test
  public void toEnricherStringTest() {
    String result = jsonMapper.toEnricherString("User Details", userDetails, "1.0.0");
    assertEquals(result, "{"
        + "\"User Details\":{"
        + "\"type\":\"User Details\",\"version\":\"1.0.0\",\"payload\":\"{"
        + "\\\"userId\\\":123456,\\\"email\\\":\\\"testuser@email.com\\\",\\\"firstName\\\":\\\"John\\\","
        + "\\\"lastName\\\":\\\"Doe\\\",\\\"displayName\\\":\\\"Test User\\\",\\\"username\\\":\\\"Test User\\\""
        + "}\"}}");
  }

  @Test
  public void objectToMapTest() {
    Map<String, Object> result = jsonMapper.objectToMap(userDetails);
    assertEquals(result.get("userId"), 123456L);
    assertEquals(result.get("username"), "Test User");
    assertEquals(result.get("firstName"), "John");
    assertEquals(result.get("lastName"), "Doe");
    assertEquals(result.get("displayName"), "Test User");
    assertEquals(result.get("email"), "testuser@email.com");
  }

  @Test
  public void toObjectTest() {
    UserDetails result = jsonMapper.toObject(
        userDetailJson,
        UserDetails.class
    );
    assertEquals(result, userDetails);
  }

  @Test
  public void toObjectListTest() {
    String usersListJson = "["
        + "{\"userId\":123456,\"email\":\"testuser@email.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"displayName\":\"Test User\",\"username\":\"Test User\"},"
        + "{\"userId\":456123,\"email\":\"botuser@email.com\",\"firstName\":\"Bot\",\"lastName\":\"User\",\"displayName\":\"Bot User\",\"username\":\"Bot User\"},"
        + "{\"userId\":654321,\"email\":\"testuser2@email.com\",\"firstName\":\"Test\",\"lastName\":\"User\",\"displayName\":\"Test 2\",\"username\":\"Test 2\"}"
        + "]";
    UserDetails[] result = jsonMapper.toObject(
        usersListJson,
        UserDetails[].class
    );
    assertEquals(result[0].getUserId(), 123456L);
    assertEquals(result[0].getUsername(), "Test User");
    assertEquals(result[0].getFirstName(), "John");
    assertEquals(result[0].getLastName(), "Doe");
    assertEquals(result[0].getDisplayName(), "Test User");
    assertEquals(result[0].getEmail(), "testuser@email.com");

    assertEquals(result[1].getUserId(), 456123L);
    assertEquals(result[1].getUsername(), "Bot User");
    assertEquals(result[1].getFirstName(), "Bot");
    assertEquals(result[1].getLastName(), "User");
    assertEquals(result[1].getDisplayName(), "Bot User");
    assertEquals(result[1].getEmail(), "botuser@email.com");

    assertEquals(result[2].getUserId(), 654321L);
    assertEquals(result[2].getUsername(), "Test 2");
    assertEquals(result[2].getFirstName(), "Test");
    assertEquals(result[2].getLastName(), "User");
    assertEquals(result[2].getDisplayName(), "Test 2");
    assertEquals(result[2].getEmail(), "testuser2@email.com");
  }

  @Test
  public void toObjectFailedTest() throws JsonProcessingException {
    when(objectMapper.readValue(
        userDetailJson,
        UserDetails.class
    )).thenThrow(JsonProcessingException.class);
    assertThrows(JsonMapperException.class, () -> jsonMapper.toObject(
        userDetailJson,
        UserDetails.class
    ));
  }

  @Test
  public void toObjectBadJsonStringTest() {
    assertThrows(JsonMapperException.class, () -> jsonMapper.toObject(
        "I'm a bad JSON String",
        UserDetails.class
    ));
  }
}
