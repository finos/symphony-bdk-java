package utils;

import clients.SymBotClient;
import clients.symphony.api.UsersClient;
import configuration.SymConfig;
import lombok.SneakyThrows;
import model.UserInfo;
import org.junit.Before;
import org.junit.Test;
import org.symphonyoss.symphony.messageml.exceptions.InvalidInputException;
import org.symphonyoss.symphony.messageml.util.IUserPresentation;

import javax.ws.rs.core.NoContentException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataProviderTest {
  private SymBotClient symBotClient;
  private SymConfig config;

  @Before
  public void initClient() {
    symBotClient = mock(SymBotClient.class);
    config = mock(SymConfig.class);
    when(symBotClient.getConfig()).thenReturn(config);
  }

  @Test
  public void getUserPresentationByEmailTest() throws InvalidInputException {
    when(config.getSupportedUriSchemes()).thenReturn(new ArrayList<>());
    DataProvider dataProvider = new DataProvider(symBotClient);
    dataProvider.setUserPresentation(12345L, "Test User", "testUser", "testuser@email.com");
    IUserPresentation user = dataProvider.getUserPresentation("testuser@email.com");
    assertEquals(user.getId(), 12345L);
    assertEquals(user.getScreenName(), "Test User");
    assertEquals(user.getPrettyName(), "testUser");
    assertEquals(user.getEmail(), "testuser@email.com");
  }

  @Test(expected = InvalidInputException.class)
  public void getUserPresentationByEmailFailTest() throws InvalidInputException {
    when(config.getSupportedUriSchemes()).thenReturn(new ArrayList<>());

    DataProvider dataProvider = new DataProvider(symBotClient);
    UserPresentation newUser = new UserPresentation(12345L, "Test User", "testUser", "testuser2@email.com");
    dataProvider.setUserPresentation(newUser);
    dataProvider.getUserPresentation("testuser@email.com");
  }

  @SneakyThrows
  @Test
  public void getUserPresentationByIdTest() {
    ArrayList<String> supportedUriSchemes = new ArrayList<>();
    supportedUriSchemes.add("http");
    supportedUriSchemes.add("https");
    UsersClient usersClient = mock(UsersClient.class);
    UserInfo userInfo = new UserInfo();
    userInfo.setId(12345L);
    userInfo.setDisplayName("testUser");
    when(config.getSupportedUriSchemes()).thenReturn(supportedUriSchemes);
    when(usersClient.getUserFromId(anyLong(), anyBoolean())).thenReturn(userInfo);
    when(symBotClient.getUsersClient()).thenReturn(usersClient);

    DataProvider dataProvider = new DataProvider(symBotClient);
    IUserPresentation user = dataProvider.getUserPresentation(12345L);
    assertEquals(user.getId(), 12345L);
    assertEquals(user.getScreenName(), "testUser");
    assertEquals(user.getPrettyName(), "testUser");
  }

  @Test(expected = InvalidInputException.class)
  public void getUserPresentationByIdFailTest() throws InvalidInputException, NoContentException {
    UsersClient usersClient = mock(UsersClient.class);
    UserInfo userInfo = new UserInfo();
    userInfo.setId(12345L);
    userInfo.setDisplayName("testUser");
    when(usersClient.getUserFromId(anyLong(), anyBoolean())).thenThrow(NoContentException.class);
    when(symBotClient.getUsersClient()).thenReturn(usersClient);
    when(config.getSupportedUriSchemes()).thenReturn(new ArrayList<>());

    DataProvider dataProvider = new DataProvider(symBotClient);
    dataProvider.getUserPresentation(123456789L);
  }

  @Test(expected = InvalidInputException.class)
  public void validateUriTest() throws URISyntaxException, InvalidInputException {
    when(config.getSupportedUriSchemes()).thenReturn(new ArrayList<>());

    DataProvider dataProvider = new DataProvider(symBotClient);
    URI uri = new URI("ftp://testuri/path/to/test");
    dataProvider.validateURI(uri);
  }
}
