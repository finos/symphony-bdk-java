package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FqdnHost {
    private String serverFqdn;

    public String getServerFqdn() {
        return serverFqdn;
    }

    public void setServerFqdn(String serverFqdn) {
        this.serverFqdn = serverFqdn;
    }
}
