package utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserPresentationTest {

  private UserPresentation userPresentation;

  @Before
  public void initPresentation(){
    userPresentation = new UserPresentation(1L, "Test 1", "test1", "test1@symphony.com");
  }

  @Test
  public void getIdSuccess() {
    assertEquals(1L, userPresentation.getId());
  }

  @Test
  public void getScreenNameSuccess() {
    assertEquals("Test 1", userPresentation.getScreenName());
  }

  @Test
  public void getPrettyNameSuccess() {
    assertEquals("test1", userPresentation.getPrettyName());
  }

  @Test
  public void getEmailSuccess() {
    assertEquals("test1@symphony.com", userPresentation.getEmail());
  }
}
