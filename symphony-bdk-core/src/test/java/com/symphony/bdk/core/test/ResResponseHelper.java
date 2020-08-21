package com.symphony.bdk.core.test;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResResponseHelper {

    public static String readResResponseFromClasspath(String file) throws IOException {
        InputStream resourceStream = ResResponseHelper.class.getResourceAsStream("/res_response/" + file);
        return IOUtils.toString(resourceStream, StandardCharsets.UTF_8.name());
    }
}
