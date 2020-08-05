package com.symphony.bdk.core.config.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BdkSSLConfig {

    private String truststorePath;
    private String truststorePassword;
}
