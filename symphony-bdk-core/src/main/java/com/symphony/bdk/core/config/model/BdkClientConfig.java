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
    private int port;
    private String context = "";

    public String getBasePath() {
        return this.scheme + "://" + this.host + ":" + this.port + this.getContext();
    }

    public String getContext() {
        if (!context.equals("") && context.charAt(0) != '/') {
            context =  "/" + context;
        }
        if (!context.equals("") && context.endsWith("/")) {
            context = context.substring(0, context.length() - 1);
        }
        return context;
    }
}
