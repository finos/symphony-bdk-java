package model;

import static org.junit.Assert.assertEquals;
import model.ApplicationEntitlementList;
import org.junit.Test;

public class ApplicationEntitlementListTest {
  @Test
  public void ApplicationEntitlementListTest() throws Exception {
    // Arrange and Act
    ApplicationEntitlementList applicationEntitlementList = new ApplicationEntitlementList();

    // Assert
    assertEquals(0, applicationEntitlementList.size());
  }
}
