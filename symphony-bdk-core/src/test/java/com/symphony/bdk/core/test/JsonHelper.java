package com.symphony.bdk.core.test;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JsonHelper {

    public static String readFromClasspath(String file) throws IOException {
        InputStream resourceStream = JsonHelper.class.getResourceAsStream(file);
        return IOUtils.toString(resourceStream, StandardCharsets.UTF_8.name());
    }
}
