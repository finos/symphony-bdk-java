package configuration;

import clients.symphony.api.constants.CommonConstants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Objects;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SymConfig {

    private static final int DEFAULT_CONNECTION_TIMEOUT = 35000;
    private static final int DEFAULT_READ_TIMEOUT = 60000;

    // ---------------------------------------------------------------------------------------------------------------//
    // NETWORK
    //
    private String sessionAuthHost;
    private int sessionAuthPort;

    private String keyAuthHost;
    private int keyAuthPort;
    private String keyManagerProxyURL;
    private String keyManagerProxyUsername;
    private String keyManagerProxyPassword;

    private String podHost;
    private int podPort;
    private String podProxyURL;
    private String podProxyUsername;
    private String podProxyPassword;

    private String agentHost;
    private int agentPort;
    private String agentProxyURL;
    private String agentProxyUsername;
    private String agentProxyPassword;

    private String proxyURL;
    private String proxyUsername;
    private String proxyPassword;

    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    private int readTimeout = DEFAULT_READ_TIMEOUT;

    // ---------------------------------------------------------------------------------------------------------------//
    // AUTHENTICATION
    //
    private String botUsername;
    private String botEmailAddress;
    // rsa
    private String botPrivateKeyPath;
    private String botPrivateKeyName;
    // cert
    private String botCertPath;
    private String botCertName;
    private String botCertPassword;

    private String appId;
    // rsa
    private String appPrivateKeyPath;
    private String appPrivateKeyName;
    // cert
    private String appCertPath;
    private String appCertName;
    private String appCertPassword;

    // ---------------------------------------------------------------------------------------------------------------//
    // SSL
    //
    private String truststorePath;
    private String truststorePassword;

    // ---------------------------------------------------------------------------------------------------------------//
    // DATAFEED
    private int datafeedEventsThreadpoolSize;
    private int datafeedEventsErrorTimeout;
    private Boolean reuseDatafeedID;

    // ---------------------------------------------------------------------------------------------------------------//
    // MISC
    //
    private String authenticationFilterUrlPattern;
    private boolean showFirehoseErrors;
    private ArrayList<String> supportedUriSchemes = new ArrayList<>();
    private RetryConfiguration retry = new RetryConfiguration();

    public String getAgentUrl() {
        String port = (this.getAgentPort() == 443) ? "" : ":" + this.getAgentPort();
        return CommonConstants.HTTPS_PREFIX + this.getAgentHost() + port;
    }

    public String getPodUrl() {
        String port = (this.getPodPort() == 443) ? "" : ":" + this.getPodPort();
        return CommonConstants.HTTPS_PREFIX + this.getPodHost() + port;
    }

    public String getKeyAuthUrl() {
        String port = (this.getKeyAuthPort() == 443) ? "" : ":" + this.getKeyAuthPort();
        return CommonConstants.HTTPS_PREFIX + this.getKeyAuthHost() + port;
    }

    public String getSessionAuthUrl() {
        String port = (this.getSessionAuthPort() == 443) ? "" : ":" + this.getSessionAuthPort();
        return CommonConstants.HTTPS_PREFIX + this.getSessionAuthHost() + port;
    }
}
