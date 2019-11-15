package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.ApplicationEntitlement;
import model.ApplicationProduct;
import org.junit.Test;

public class ApplicationEntitlementTest {
  @Test
  public void ApplicationEntitlementTest() throws Exception {
    // Arrange and Act
    ApplicationEntitlement applicationEntitlement = new ApplicationEntitlement();

    // Assert
    assertEquals(null, applicationEntitlement.getInstall());
  }

  @Test
  public void getAppIdTest() throws Exception {
    // Arrange
    ApplicationEntitlement applicationEntitlement = new ApplicationEntitlement();

    // Act
    String actual = applicationEntitlement.getAppId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAppNameTest() throws Exception {
    // Arrange
    ApplicationEntitlement applicationEntitlement = new ApplicationEntitlement();

    // Act
    String actual = applicationEntitlement.getAppName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getInstallTest() throws Exception {
    // Arrange
    ApplicationEntitlement applicationEntitlement = new ApplicationEntitlement();

    // Act
    Boolean actual = applicationEntitlement.getInstall();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getListedTest() throws Exception {
    // Arrange
    ApplicationEntitlement applicationEntitlement = new ApplicationEntitlement();

    // Act
    Boolean actual = applicationEntitlement.getListed();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getProductsTest() throws Exception {
    // Arrange
    ApplicationEntitlement applicationEntitlement = new ApplicationEntitlement();

    // Act
    List<ApplicationProduct> actual = applicationEntitlement.getProducts();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAppIdTest() throws Exception {
    // Arrange
    ApplicationEntitlement applicationEntitlement = new ApplicationEntitlement();
    String appId = "aaaaa";

    // Act
    applicationEntitlement.setAppId(appId);

    // Assert
    assertEquals("aaaaa", applicationEntitlement.getAppId());
  }

  @Test
  public void setAppNameTest() throws Exception {
    // Arrange
    ApplicationEntitlement applicationEntitlement = new ApplicationEntitlement();
    String appName = "aaaaa";

    // Act
    applicationEntitlement.setAppName(appName);

    // Assert
    assertEquals("aaaaa", applicationEntitlement.getAppName());
  }

  @Test
  public void setInstallTest() throws Exception {
    // Arrange
    ApplicationEntitlement applicationEntitlement = new ApplicationEntitlement();
    Boolean install = new Boolean(true);

    // Act
    applicationEntitlement.setInstall(install);

    // Assert
    assertEquals(Boolean.valueOf(true), applicationEntitlement.getInstall());
  }

  @Test
  public void setListedTest() throws Exception {
    // Arrange
    ApplicationEntitlement applicationEntitlement = new ApplicationEntitlement();
    Boolean listed = new Boolean(true);

    // Act
    applicationEntitlement.setListed(listed);

    // Assert
    assertEquals(Boolean.valueOf(true), applicationEntitlement.getListed());
  }

  @Test
  public void setProductsTest() throws Exception {
    // Arrange
    ApplicationEntitlement applicationEntitlement = new ApplicationEntitlement();
    ArrayList<ApplicationProduct> arrayList = new ArrayList<ApplicationProduct>();
    arrayList.add(new ApplicationProduct());

    // Act
    applicationEntitlement.setProducts(arrayList);

    // Assert
    assertSame(arrayList, applicationEntitlement.getProducts());
  }
}
