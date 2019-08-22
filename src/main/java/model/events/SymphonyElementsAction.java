package model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SymphonyElementsAction {
    private Map<String, String> formStream;
    private Map<String, String> stream;
    private String formId;
    private Map<String, Object> formValues;

    public String getStreamId() {
        if (stream != null && stream.get("id") != null) {
            return stream.get("id");
        }
        if (formStream != null && formStream.get("streamId") != null) {
            return formStream.get("streamId")
                .replaceAll("=", "")
                .replaceAll("/", "_")
                .replaceAll("\\+", "-");
        }
        return null;
    }

    public String getStreamType() {
        if (stream != null && stream.get("type") != null) {
            return stream.get("type");
        }
        return null;
    }

    public void setFormStream(Map<String, String> formStream) {
        this.formStream = formStream;
    }

    public void setStream(Map<String, String> stream) {
        this.stream = stream;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Map<String, Object> getFormValues() {
        return formValues;
    }

    public void setFormValues(Map<String, Object> formValues) {
        this.formValues = formValues;
    }
}
