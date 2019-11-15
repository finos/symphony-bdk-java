package clients.symphony.api;

import static org.junit.Assert.assertEquals;
import authentication.ISymAuth;
import authentication.SymBotAuth;
import clients.SymOBOClient;
import clients.symphony.api.AdminClient;
import configuration.SymConfig;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import model.AdminNewUser;
import model.AdminStreamFilter;
import model.AdminUserAttributes;
import model.AdminUserInfo;
import model.ApplicationEntitlement;
import model.Avatar;
import model.FeatureEntitlement;
import model.InboundImportMessageList;
import model.OutboundImportMessage;
import model.OutboundImportMessageList;
import model.SuppressionResult;
import model.UserStatus;
import model.events.AdminStreamInfoList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AdminClientTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void AdminClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    new AdminClient(symOBOClient);

    // Assert
    assertEquals(null, symOBOClient.getSymAuth());
  }

  @Test
  public void createIMTest() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));
    ArrayList<Long> arrayList = new ArrayList<Long>();
    arrayList.add(new Long(1L));

    // Act and Assert
    thrown.expect(NullPointerException.class);
    adminClient.createIM(arrayList);
  }

  @Test
  public void createUserTest() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));
    AdminNewUser newUser = new AdminNewUser();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    adminClient.createUser(newUser);
  }

  @Test
  public void getAvatarTest() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));
    Long uid = new Long(1L);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    adminClient.getAvatar(uid);
  }

  @Test
  public void getUserApplicationEntitlementsTest() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));
    Long uid = new Long(1L);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    adminClient.getUserApplicationEntitlements(uid);
  }

  @Test
  public void getUserFeaturesTest() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));
    Long uid = new Long(1L);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    adminClient.getUserFeatures(uid);
  }

  @Test
  public void getUserStatusTest() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));
    Long uid = new Long(1L);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    adminClient.getUserStatus(uid);
  }

  @Test
  public void getUserTest() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));
    Long uid = new Long(1L);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    adminClient.getUser(uid);
  }

  @Test
  public void importMessagesTest() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));
    OutboundImportMessageList outboundImportMessageList = new OutboundImportMessageList();
    outboundImportMessageList.add(new OutboundImportMessage());

    // Act and Assert
    thrown.expect(NullPointerException.class);
    adminClient.importMessages(outboundImportMessageList);
  }

  @Test
  public void listEnterpriseStreamsTest() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));
    AdminStreamFilter filter = new AdminStreamFilter();
    int skip = 1;
    int limit = 1;

    // Act and Assert
    thrown.expect(NullPointerException.class);
    adminClient.listEnterpriseStreams(filter, skip, limit);
  }

  @Test
  public void listPodFeaturesTest() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));

    // Act and Assert
    thrown.expect(NullPointerException.class);
    adminClient.listPodFeatures();
  }

  @Test
  public void listUsersTest() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));
    int skip = 1;
    int limit = 1;

    // Act and Assert
    thrown.expect(NullPointerException.class);
    adminClient.listUsers(skip, limit);
  }

  @Test
  public void suppressMessageTest() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));
    String id = "aaaaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    adminClient.suppressMessage(id);
  }

  @Test
  public void updateAvatarTest() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));
    Long userId = new Long(1L);
    String filePath = "aakaa";

    // Act and Assert
    thrown.expect(FileNotFoundException.class);
    adminClient.updateAvatar(userId, filePath);
  }

  @Test
  public void updateUserApplicationEntitlementsTest() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));
    Long uid = new Long(1L);
    ArrayList<ApplicationEntitlement> arrayList = new ArrayList<ApplicationEntitlement>();
    arrayList.add(null);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    adminClient.updateUserApplicationEntitlements(uid, arrayList);
  }

  @Test
  public void updateUserFeaturesTest() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));
    Long uid = new Long(1L);
    ArrayList<FeatureEntitlement> arrayList = new ArrayList<FeatureEntitlement>();
    arrayList.add(null);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    adminClient.updateUserFeatures(uid, arrayList);
  }

  @Test
  public void updateUserStatusTest() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));
    Long uid = new Long(1L);
    String status = "aakaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    adminClient.updateUserStatus(uid, status);
  }

  @Test
  public void updateUserStatusTest2() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));
    Long uid = new Long(1L);
    UserStatus status = UserStatus.ENABLED;

    // Act and Assert
    thrown.expect(NullPointerException.class);
    adminClient.updateUserStatus(uid, status);
  }

  @Test
  public void updateUserTest() throws Exception {
    // Arrange
    AdminClient adminClient = new AdminClient(new SymOBOClient(new SymConfig(), null));
    Long userId = new Long(1L);
    AdminUserAttributes userAttributes = new AdminUserAttributes();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    adminClient.updateUser(userId, userAttributes);
  }
}
