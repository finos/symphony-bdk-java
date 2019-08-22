package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TagBuilder {
    private String tagName;
    private Map<String, Object> fields;
    private String contents;

    public TagBuilder(String tagName) {
        this.tagName = tagName;
        this.fields = new HashMap<>();
    }

    public static TagBuilder builder(String tagName) {
        return new TagBuilder(tagName);
    }

    public TagBuilder addField(String fieldName, Object fieldValue) {
        this.fields.put(fieldName, fieldValue);
        return this;
    }

    public TagBuilder setContents(String contents) {
        this.contents = contents;
        return this;
    }

    private String getFieldsMarkup() {
        if (fields.size() == 0) {
            return "";
        }
        return fields.entrySet().stream()
            .map(set -> String.format(" %s=\"%s\"", set.getKey(), set.getValue()))
            .collect(Collectors.joining(""));
    }

    public String build() {
        return String.format("<%s%s>%s</%s>", tagName, getFieldsMarkup(), contents, tagName);
    }

    public String buildSelfClosing() {
        return String.format("<%s%s />", tagName, getFieldsMarkup());
    }
}
