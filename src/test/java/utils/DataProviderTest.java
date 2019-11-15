package utils;

import static org.junit.Assert.assertEquals;
import clients.SymBotClient;
import java.net.URI;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.symphonyoss.symphony.messageml.util.IUserPresentation;
import utils.DataProvider;

public class DataProviderTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void getUserPresentationTest() throws Exception {
    // Arrange
    DataProvider dataProvider = new DataProvider(null);
    Long uid = new Long(1L);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    dataProvider.getUserPresentation(uid);
  }

  @Test
  public void getUserPresentationTest2() throws Exception {
    // Arrange
    DataProvider dataProvider = new DataProvider(null);
    String email = "aaaaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    dataProvider.getUserPresentation(email);
  }

  @Test
  public void setUserPresentationTest() throws Exception {
    // Arrange
    DataProvider dataProvider = new DataProvider(null);
    long id = 1L;
    String screenName = "aaaaa";
    String prettyName = "aaaaa";
    String email = "aaaaa";

    // Act
    dataProvider.setUserPresentation(id, screenName, prettyName, email);
  }

  @Test
  public void setUserPresentationTest2() throws Exception {
    // Arrange
    DataProvider dataProvider = new DataProvider(null);
    UserPresentation userPresentation = new UserPresentation(1L, "aaaaa", "aaaaa");

    // Act
    dataProvider.setUserPresentation(userPresentation);

    // Assert
    assertEquals(1L, userPresentation.getId());
  }

  @Test
  public void validateURITest() throws Exception {
    // Arrange
    DataProvider dataProvider = new DataProvider(null);
    URI uri = new URI("aaaaa");

    // Act and Assert
    thrown.expect(NullPointerException.class);
    dataProvider.validateURI(uri);
  }
}
