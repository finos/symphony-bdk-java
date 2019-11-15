package model;

import static org.junit.Assert.assertEquals;
import model.HealthcheckResponse;
import org.junit.Test;

public class HealthcheckResponseTest {
  @Test
  public void HealthcheckResponseTest() throws Exception {
    // Arrange and Act
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();

    // Assert
    assertEquals(null, healthcheckResponse.getAgentVersion());
  }

  @Test
  public void getAgentServiceUserErrorTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();

    // Act
    String actual = healthcheckResponse.getAgentServiceUserError();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAgentServiceUserTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();

    // Act
    Boolean actual = healthcheckResponse.getAgentServiceUser();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAgentVersionTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();

    // Act
    String actual = healthcheckResponse.getAgentVersion();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCeServiceUserErrorTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();

    // Act
    String actual = healthcheckResponse.getCeServiceUserError();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCeServiceUserTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();

    // Act
    Boolean actual = healthcheckResponse.getCeServiceUser();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getEncryptDecryptErrorTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();

    // Act
    String actual = healthcheckResponse.getEncryptDecryptError();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getEncryptDecryptSuccessTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();

    // Act
    Boolean actual = healthcheckResponse.getEncryptDecryptSuccess();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getFirehoseConnectivityErrorTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();

    // Act
    String actual = healthcheckResponse.getFirehoseConnectivityError();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getFirehoseConnectivityTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();

    // Act
    Boolean actual = healthcheckResponse.getFirehoseConnectivity();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getKeyManagerConnectivityErrorTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();

    // Act
    String actual = healthcheckResponse.getKeyManagerConnectivityError();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getKeyManagerConnectivityTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();

    // Act
    Boolean actual = healthcheckResponse.getKeyManagerConnectivity();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getPodConnectivityErrorTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();

    // Act
    String actual = healthcheckResponse.getPodConnectivityError();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getPodConnectivityTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();

    // Act
    Boolean actual = healthcheckResponse.getPodConnectivity();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getPodVersionTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();

    // Act
    String actual = healthcheckResponse.getPodVersion();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAgentServiceUserErrorTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();
    String agentServiceUserError = "aaaaa";

    // Act
    healthcheckResponse.setAgentServiceUserError(agentServiceUserError);

    // Assert
    assertEquals("aaaaa", healthcheckResponse.getAgentServiceUserError());
  }

  @Test
  public void setAgentServiceUserTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();
    Boolean agentServiceUser = new Boolean(true);

    // Act
    healthcheckResponse.setAgentServiceUser(agentServiceUser);

    // Assert
    assertEquals(Boolean.valueOf(true), healthcheckResponse.getAgentServiceUser());
  }

  @Test
  public void setAgentVersionTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();
    String agentVersion = "aaaaa";

    // Act
    healthcheckResponse.setAgentVersion(agentVersion);

    // Assert
    assertEquals("aaaaa", healthcheckResponse.getAgentVersion());
  }

  @Test
  public void setCeServiceUserErrorTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();
    String ceServiceUserError = "aaaaa";

    // Act
    healthcheckResponse.setCeServiceUserError(ceServiceUserError);

    // Assert
    assertEquals("aaaaa", healthcheckResponse.getCeServiceUserError());
  }

  @Test
  public void setCeServiceUserTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();
    Boolean ceServiceUser = new Boolean(true);

    // Act
    healthcheckResponse.setCeServiceUser(ceServiceUser);

    // Assert
    assertEquals(Boolean.valueOf(true), healthcheckResponse.getCeServiceUser());
  }

  @Test
  public void setEncryptDecryptErrorTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();
    String encryptDecryptError = "aaaaa";

    // Act
    healthcheckResponse.setEncryptDecryptError(encryptDecryptError);

    // Assert
    assertEquals("aaaaa", healthcheckResponse.getEncryptDecryptError());
  }

  @Test
  public void setEncryptDecryptSuccessTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();
    Boolean encryptDecryptSuccess = new Boolean(true);

    // Act
    healthcheckResponse.setEncryptDecryptSuccess(encryptDecryptSuccess);

    // Assert
    assertEquals(Boolean.valueOf(true), healthcheckResponse.getEncryptDecryptSuccess());
  }

  @Test
  public void setFirehoseConnectivityErrorTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();
    String firehoseConnectivityError = "aaaaa";

    // Act
    healthcheckResponse.setFirehoseConnectivityError(firehoseConnectivityError);

    // Assert
    assertEquals("aaaaa", healthcheckResponse.getFirehoseConnectivityError());
  }

  @Test
  public void setFirehoseConnectivityTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();
    Boolean firehoseConnectivity = new Boolean(true);

    // Act
    healthcheckResponse.setFirehoseConnectivity(firehoseConnectivity);

    // Assert
    assertEquals(Boolean.valueOf(true), healthcheckResponse.getFirehoseConnectivity());
  }

  @Test
  public void setKeyManagerConnectivityErrorTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();
    String keyManagerConnectivityError = "aaaaa";

    // Act
    healthcheckResponse.setKeyManagerConnectivityError(keyManagerConnectivityError);

    // Assert
    assertEquals("aaaaa", healthcheckResponse.getKeyManagerConnectivityError());
  }

  @Test
  public void setKeyManagerConnectivityTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();
    Boolean keyManagerConnectivity = new Boolean(true);

    // Act
    healthcheckResponse.setKeyManagerConnectivity(keyManagerConnectivity);

    // Assert
    assertEquals(Boolean.valueOf(true), healthcheckResponse.getKeyManagerConnectivity());
  }

  @Test
  public void setPodConnectivityErrorTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();
    String podConnectivityError = "aaaaa";

    // Act
    healthcheckResponse.setPodConnectivityError(podConnectivityError);

    // Assert
    assertEquals("aaaaa", healthcheckResponse.getPodConnectivityError());
  }

  @Test
  public void setPodConnectivityTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();
    Boolean podConnectivity = new Boolean(true);

    // Act
    healthcheckResponse.setPodConnectivity(podConnectivity);

    // Assert
    assertEquals(Boolean.valueOf(true), healthcheckResponse.getPodConnectivity());
  }

  @Test
  public void setPodVersionTest() throws Exception {
    // Arrange
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();
    String podVersion = "aaaaa";

    // Act
    healthcheckResponse.setPodVersion(podVersion);

    // Assert
    assertEquals("aaaaa", healthcheckResponse.getPodVersion());
  }
}
