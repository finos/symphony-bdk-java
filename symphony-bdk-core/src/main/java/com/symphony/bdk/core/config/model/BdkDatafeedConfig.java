package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

import java.io.File;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkDatafeedConfig {

    private String version = "v1";
    private String idFilePath;
    private BdkRetryConfig retry;

    public String getIdFilePath() {
        if (idFilePath == null || idFilePath.isEmpty()) {
            return "." + File.separator;
        }
        if (!idFilePath.endsWith(File.separator)) {
            return idFilePath + File.separator;
        }
        return idFilePath;
    }
}
