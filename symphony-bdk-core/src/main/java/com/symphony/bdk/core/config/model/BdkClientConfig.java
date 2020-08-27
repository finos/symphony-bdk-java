package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BdkClientConfig {

    private String scheme = null;
    private String host = null;
    private Integer port = null;
    private String context = null;

    private String proxyUrl = null;
    private String proxyUsername = null;
    private String proxyPassword = null;

    private Integer connectTimeout = null;
    private Integer readTimeout = null;
    private Integer connectionRequestTimeout = null;


    public String getBasePath() {
        return this.scheme + "://" + this.host + this.getPortAsString() + this.getFormattedContext();
    }

    public String getFormattedContext() {
        if (this.context == null) {
            return "";
        }
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
