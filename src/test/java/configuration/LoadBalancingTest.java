package configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import configuration.LoadBalancing;
import configuration.LoadBalancingMethod;
import org.junit.Test;

public class LoadBalancingTest {
  @Test
  public void LoadBalancingTest() throws Exception {
    // Arrange and Act
    LoadBalancing loadBalancing = new LoadBalancing();

    // Assert
    assertEquals(null, loadBalancing.getMethod());
  }

  @Test
  public void getMethodTest() throws Exception {
    // Arrange
    LoadBalancing loadBalancing = new LoadBalancing();

    // Act
    LoadBalancingMethod actual = loadBalancing.getMethod();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void isStickySessionsTest() throws Exception {
    // Arrange
    LoadBalancing loadBalancing = new LoadBalancing();

    // Act
    boolean actual = loadBalancing.isStickySessions();

    // Assert
    assertFalse(actual);
  }

  @Test
  public void setMethodTest() throws Exception {
    // Arrange
    LoadBalancing loadBalancing = new LoadBalancing();
    LoadBalancingMethod method = LoadBalancingMethod.random;

    // Act
    loadBalancing.setMethod(method);

    // Assert
    assertEquals(LoadBalancingMethod.random, loadBalancing.getMethod());
  }

  @Test
  public void setStickySessionsTest() throws Exception {
    // Arrange
    LoadBalancing loadBalancing = new LoadBalancing();
    boolean stickySessions = true;

    // Act
    loadBalancing.setStickySessions(stickySessions);

    // Assert
    assertTrue(loadBalancing.isStickySessions());
  }
}
