package com.symphony.bdk.core.api.invoker;

import lombok.Getter;
import org.apiguardian.api.API;

@Getter
@API(status = API.Status.INTERNAL)
public class Pair {

    private String name = "";
    private String value = "";

    public Pair(String name, String value) {
        this.setName(name);
        this.setValue(value);
    }

    private void setName(String name) {
        if (isInvalidString(name)) {
            return;
        }

        this.name = name;
    }

    private void setValue(String value) {
        if (isInvalidString(value)) {
            return;
        }

        this.value = value;
    }

    private static boolean isInvalidString(String arg) {
        if (arg == null) {
            return true;
        }

        return arg.trim().isEmpty();
    }
}
