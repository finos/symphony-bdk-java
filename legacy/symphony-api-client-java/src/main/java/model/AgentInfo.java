package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentInfo {

    private String ipAddress;
    private String serverFqdn;
    private String hostname;
    private String version;
    private String url;
    private boolean onPrem;
}
