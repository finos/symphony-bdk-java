package model;

public class HealthcheckResponse {
  private Boolean podConnectivity;
  private String podConnectivityError;
  private Boolean keyManagerConnectivity;
  private String keyManagerConnectivityError;
  private Boolean firehoseConnectivity;
  private String firehoseConnectivityError;
  private Boolean encryptDecryptSuccess;
  private String encryptDecryptError;
  private String podVersion;
  private String agentVersion;
  private Boolean agentServiceUser;
  private String agentServiceUserError;
  private Boolean ceServiceUser;
  private String ceServiceUserError;

  public HealthcheckResponse() {
  }

  public Boolean getPodConnectivity() {
    return podConnectivity;
  }

  public void setPodConnectivity(Boolean podConnectivity) {
    this.podConnectivity = podConnectivity;
  }

  public String getPodConnectivityError() {
    return podConnectivityError;
  }

  public void setPodConnectivityError(String podConnectivityError) {
    this.podConnectivityError = podConnectivityError;
  }

  public Boolean getKeyManagerConnectivity() {
    return keyManagerConnectivity;
  }

  public void setKeyManagerConnectivity(Boolean keyManagerConnectivity) {
    this.keyManagerConnectivity = keyManagerConnectivity;
  }

  public String getKeyManagerConnectivityError() {
    return keyManagerConnectivityError;
  }

  public void setKeyManagerConnectivityError(String keyManagerConnectivityError) {
    this.keyManagerConnectivityError = keyManagerConnectivityError;
  }

  public Boolean getFirehoseConnectivity() {
    return firehoseConnectivity;
  }

  public void setFirehoseConnectivity(Boolean firehoseConnectivity) {
    this.firehoseConnectivity = firehoseConnectivity;
  }

  public String getFirehoseConnectivityError() {
    return firehoseConnectivityError;
  }

  public void setFirehoseConnectivityError(String firehoseConnectivityError) {
    this.firehoseConnectivityError = firehoseConnectivityError;
  }

  public Boolean getEncryptDecryptSuccess() {
    return encryptDecryptSuccess;
  }

  public void setEncryptDecryptSuccess(Boolean encryptDecryptSuccess) {
    this.encryptDecryptSuccess = encryptDecryptSuccess;
  }

  public String getEncryptDecryptError() {
    return encryptDecryptError;
  }

  public void setEncryptDecryptError(String encryptDecryptError) {
    this.encryptDecryptError = encryptDecryptError;
  }

  public String getPodVersion() {
    return podVersion;
  }

  public void setPodVersion(String podVersion) {
    this.podVersion = podVersion;
  }

  public String getAgentVersion() {
    return agentVersion;
  }

  public void setAgentVersion(String agentVersion) {
    this.agentVersion = agentVersion;
  }

  public Boolean getAgentServiceUser() {
    return agentServiceUser;
  }

  public void setAgentServiceUser(Boolean agentServiceUser) {
    this.agentServiceUser = agentServiceUser;
  }

  public String getAgentServiceUserError() {
    return agentServiceUserError;
  }

  public void setAgentServiceUserError(String agentServiceUserError) {
    this.agentServiceUserError = agentServiceUserError;
  }

  public Boolean getCeServiceUser() {
    return ceServiceUser;
  }

  public void setCeServiceUser(Boolean ceServiceUser) {
    this.ceServiceUser = ceServiceUser;
  }

  public String getCeServiceUserError() {
    return ceServiceUserError;
  }

  public void setCeServiceUserError(String ceServiceUserError) {
    this.ceServiceUserError = ceServiceUserError;
  }
}
