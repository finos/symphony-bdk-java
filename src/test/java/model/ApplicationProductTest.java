package model;

import static org.junit.Assert.assertEquals;
import model.ApplicationProduct;
import org.junit.Test;

public class ApplicationProductTest {
  @Test
  public void ApplicationProductTest() throws Exception {
    // Arrange and Act
    ApplicationProduct applicationProduct = new ApplicationProduct();

    // Assert
    assertEquals(null, applicationProduct.getSku());
  }

  @Test
  public void getAppIdTest() throws Exception {
    // Arrange
    ApplicationProduct applicationProduct = new ApplicationProduct();

    // Act
    String actual = applicationProduct.getAppId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getNameTest() throws Exception {
    // Arrange
    ApplicationProduct applicationProduct = new ApplicationProduct();

    // Act
    String actual = applicationProduct.getName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getSkuTest() throws Exception {
    // Arrange
    ApplicationProduct applicationProduct = new ApplicationProduct();

    // Act
    String actual = applicationProduct.getSku();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getSubscribedTest() throws Exception {
    // Arrange
    ApplicationProduct applicationProduct = new ApplicationProduct();

    // Act
    Boolean actual = applicationProduct.getSubscribed();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTypeTest() throws Exception {
    // Arrange
    ApplicationProduct applicationProduct = new ApplicationProduct();

    // Act
    String actual = applicationProduct.getType();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAppIdTest() throws Exception {
    // Arrange
    ApplicationProduct applicationProduct = new ApplicationProduct();
    String appId = "aaaaa";

    // Act
    applicationProduct.setAppId(appId);

    // Assert
    assertEquals("aaaaa", applicationProduct.getAppId());
  }

  @Test
  public void setNameTest() throws Exception {
    // Arrange
    ApplicationProduct applicationProduct = new ApplicationProduct();
    String name = "aaaaa";

    // Act
    applicationProduct.setName(name);

    // Assert
    assertEquals("aaaaa", applicationProduct.getName());
  }

  @Test
  public void setSkuTest() throws Exception {
    // Arrange
    ApplicationProduct applicationProduct = new ApplicationProduct();
    String sku = "aaaaa";

    // Act
    applicationProduct.setSku(sku);

    // Assert
    assertEquals("aaaaa", applicationProduct.getSku());
  }

  @Test
  public void setSubscribedTest() throws Exception {
    // Arrange
    ApplicationProduct applicationProduct = new ApplicationProduct();
    Boolean subscribed = new Boolean(true);

    // Act
    applicationProduct.setSubscribed(subscribed);

    // Assert
    assertEquals(Boolean.valueOf(true), applicationProduct.getSubscribed());
  }

  @Test
  public void setTypeTest() throws Exception {
    // Arrange
    ApplicationProduct applicationProduct = new ApplicationProduct();
    String type = "aaaaa";

    // Act
    applicationProduct.setType(type);

    // Assert
    assertEquals("aaaaa", applicationProduct.getType());
  }
}
