package clients.symphony.api;

import static org.junit.Assert.assertEquals;
import authentication.ISymAuth;
import authentication.SymBotAuth;
import clients.SymOBOClient;
import clients.symphony.api.InformationBarriersClient;
import configuration.SymConfig;
import java.util.ArrayList;
import java.util.List;
import model.InformationBarrierGroup;
import model.InformationBarrierGroupStatus;
import model.Policy;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class InformationBarriersClientTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void InformationBarriersClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    new InformationBarriersClient(symOBOClient);

    // Assert
    assertEquals(null, symOBOClient.getSymAuth());
  }

  @Test
  public void addGroupMembersTest() throws Exception {
    // Arrange
    InformationBarriersClient informationBarriersClient = new InformationBarriersClient(
        new SymOBOClient(new SymConfig(), null));
    String groupId = "aaaaa";
    ArrayList<Long> arrayList = new ArrayList<Long>();
    arrayList.add(new Long(1L));

    // Act and Assert
    thrown.expect(NullPointerException.class);
    informationBarriersClient.addGroupMembers(groupId, arrayList);
  }

  @Test
  public void listGroupMembersTest() throws Exception {
    // Arrange
    InformationBarriersClient informationBarriersClient = new InformationBarriersClient(
        new SymOBOClient(new SymConfig(), null));
    String groupId = "aaaaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    informationBarriersClient.listGroupMembers(groupId);
  }

  @Test
  public void listGroupsTest() throws Exception {
    // Arrange
    InformationBarriersClient informationBarriersClient = new InformationBarriersClient(
        new SymOBOClient(new SymConfig(), null));

    // Act and Assert
    thrown.expect(NullPointerException.class);
    informationBarriersClient.listGroups();
  }

  @Test
  public void listPoliciesTest() throws Exception {
    // Arrange
    InformationBarriersClient informationBarriersClient = new InformationBarriersClient(
        new SymOBOClient(new SymConfig(), null));

    // Act and Assert
    thrown.expect(NullPointerException.class);
    informationBarriersClient.listPolicies();
  }

  @Test
  public void removeGroupMembersTest() throws Exception {
    // Arrange
    InformationBarriersClient informationBarriersClient = new InformationBarriersClient(
        new SymOBOClient(new SymConfig(), null));
    String groupId = "aaaaa";
    ArrayList<Long> arrayList = new ArrayList<Long>();
    arrayList.add(new Long(1L));

    // Act and Assert
    thrown.expect(NullPointerException.class);
    informationBarriersClient.removeGroupMembers(groupId, arrayList);
  }
}
