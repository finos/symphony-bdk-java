package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class BdkDatafeedConfig {

    private String version = "v1";
    private String datafeedIdFilePath;
    private BdkRetryConfig retry;

    public String getDatafeedIdFilePath() {
        if (datafeedIdFilePath == null || datafeedIdFilePath.isEmpty()) {
            return "." + File.separator;
        }
        if (!datafeedIdFilePath.endsWith(File.separator)) {
            return datafeedIdFilePath + File.separator;
        }
        return datafeedIdFilePath;
    }
}
