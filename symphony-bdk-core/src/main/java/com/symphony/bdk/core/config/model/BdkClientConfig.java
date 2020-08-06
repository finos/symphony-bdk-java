package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BdkClientConfig {

    private static final String DEFAULT_SCHEME = "https";
    private static final int DEFAULT_HTTPS_PORT = 443;

    private String scheme = DEFAULT_SCHEME;
    private String host;
    private Integer port = DEFAULT_HTTPS_PORT;
    private String context = "";

    private String proxyUrl = null;
    private String proxyUsername = null;
    private String proxyPassword = null;

    private Integer connectTimeout = null;
    private Integer readTimeout = null;
    private Integer connectionRequestTimeout = null;


    public String getBasePath() {
        return this.scheme + "://" + this.host + this.getPortAsString() + this.getContext();
    }

    public String getContext() {
        if (!this.context.equals("") && this.context.charAt(0) != '/') {
            this.context =  "/" + this.context;
        }
        if (!this.context.equals("") && this.context.endsWith("/")) {
            this.context = this.context.substring(0, this.context.length() - 1);
        }

        return this.context;
    }

    private String getPortAsString() {
        return this.port != null ? ":" + this.port : "";
    }
}
