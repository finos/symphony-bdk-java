package model;

import static org.junit.Assert.assertEquals;
import model.FeatureEntitlement;
import org.junit.Test;

public class FeatureEntitlementTest {
  @Test
  public void FeatureEntitlementTest() throws Exception {
    // Arrange and Act
    FeatureEntitlement featureEntitlement = new FeatureEntitlement();

    // Assert
    assertEquals(null, featureEntitlement.getEntitlment());
  }

  @Test
  public void getEnabledTest() throws Exception {
    // Arrange
    FeatureEntitlement featureEntitlement = new FeatureEntitlement();

    // Act
    Boolean actual = featureEntitlement.getEnabled();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getEntitlmentTest() throws Exception {
    // Arrange
    FeatureEntitlement featureEntitlement = new FeatureEntitlement();

    // Act
    String actual = featureEntitlement.getEntitlment();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setEnabledTest() throws Exception {
    // Arrange
    FeatureEntitlement featureEntitlement = new FeatureEntitlement();
    Boolean enabled = new Boolean(true);

    // Act
    featureEntitlement.setEnabled(enabled);

    // Assert
    assertEquals(Boolean.valueOf(true), featureEntitlement.getEnabled());
  }

  @Test
  public void setEntitlmentTest() throws Exception {
    // Arrange
    FeatureEntitlement featureEntitlement = new FeatureEntitlement();
    String entitlment = "aaaaa";

    // Act
    featureEntitlement.setEntitlment(entitlment);

    // Assert
    assertEquals("aaaaa", featureEntitlement.getEntitlment());
  }
}
