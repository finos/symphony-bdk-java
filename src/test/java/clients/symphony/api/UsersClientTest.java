package clients.symphony.api;

import static org.junit.Assert.assertEquals;
import authentication.ISymAuth;
import authentication.SymBotAuth;
import clients.SymOBOClient;
import clients.symphony.api.UsersClient;
import configuration.SymConfig;
import java.util.ArrayList;
import java.util.List;
import model.UserFilter;
import model.UserInfo;
import model.UserSearchResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UsersClientTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void UsersClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    new UsersClient(symOBOClient);

    // Assert
    assertEquals(null, symOBOClient.getSymAuth());
  }

  @Test
  public void getSessionUserTest() throws Exception {
    // Arrange
    UsersClient usersClient = new UsersClient(new SymOBOClient(new SymConfig(), null));

    // Act and Assert
    thrown.expect(NullPointerException.class);
    usersClient.getSessionUser();
  }

  @Test
  public void getUserFromEmailTest() throws Exception {
    // Arrange
    UsersClient usersClient = new UsersClient(new SymOBOClient(new SymConfig(), null));
    String email = "aaaaa";
    Boolean local = new Boolean(true);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    usersClient.getUserFromEmail(email, local);
  }

  @Test
  public void getUserFromIdTest() throws Exception {
    // Arrange
    UsersClient usersClient = new UsersClient(new SymOBOClient(new SymConfig(), null));
    Long id = new Long(1L);
    Boolean local = new Boolean(true);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    usersClient.getUserFromId(id, local);
  }

  @Test
  public void getUserFromUsernameTest() throws Exception {
    // Arrange
    UsersClient usersClient = new UsersClient(new SymOBOClient(new SymConfig(), null));
    String username = "aaaaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    usersClient.getUserFromUsername(username);
  }

  @Test
  public void getUsersFromEmailListTest() throws Exception {
    // Arrange
    UsersClient usersClient = new UsersClient(new SymOBOClient(new SymConfig(), null));
    ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.add("aaaaa");
    Boolean local = new Boolean(true);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    usersClient.getUsersFromEmailList(arrayList, local);
  }

  @Test
  public void getUsersFromIdListTest() throws Exception {
    // Arrange
    UsersClient usersClient = new UsersClient(new SymOBOClient(new SymConfig(), null));
    ArrayList<Long> arrayList = new ArrayList<Long>();
    arrayList.add(new Long(1L));
    Boolean local = null;

    // Act and Assert
    thrown.expect(NullPointerException.class);
    usersClient.getUsersFromIdList(arrayList, local);
  }

  @Test
  public void getUsersV3Test() throws Exception {
    // Arrange
    UsersClient usersClient = new UsersClient(new SymOBOClient(new SymConfig(), null));
    ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.add("aaaaa");
    ArrayList<Long> arrayList1 = new ArrayList<Long>(1);
    arrayList1.add(new Long(1L));
    Boolean local = new Boolean(true);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    usersClient.getUsersV3(arrayList, arrayList1, local);
  }

  @Test
  public void searchUsersTest() throws Exception {
    // Arrange
    UsersClient usersClient = new UsersClient(new SymOBOClient(new SymConfig(), null));
    String query = "aaaaa";
    boolean local = true;
    int skip = 1;
    int limit = 11;
    UserFilter filter = new UserFilter();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    usersClient.searchUsers(query, local, skip, limit, filter);
  }
}
