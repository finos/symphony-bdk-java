package model;

import static org.junit.Assert.assertEquals;
import model.FeatureEntitlementList;
import org.junit.Test;

public class FeatureEntitlementListTest {
  @Test
  public void FeatureEntitlementListTest() throws Exception {
    // Arrange and Act
    FeatureEntitlementList featureEntitlementList = new FeatureEntitlementList();

    // Assert
    assertEquals(0, featureEntitlementList.size());
  }
}
