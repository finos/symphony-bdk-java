package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class SmsRenderer {

    private static final Logger log = LoggerFactory.getLogger(SmsRenderer.class);

    public static String renderInBot(String messageContext, SmsTypes smsType) {
        String wrappedContext = "{\"message\":" + messageContext + "}";

        return render(wrappedContext, smsType);
    }

    public static String renderInBot(JSONObject messageContext, SmsTypes smsType) {
        JSONObject wrappedContext = new JSONObject();
        wrappedContext.put("message", messageContext);

        return render(wrappedContext.toString(), smsType);
    }

    public static String renderInBot(JSONArray messageContext, SmsTypes smsType) {
        JSONObject wrappedContext = new JSONObject();
        wrappedContext.put("message", messageContext);

        return render(wrappedContext.toString(), smsType);
    }

    private static String render(String wrappedContext, SmsTypes smsType) {
        try {
            HandlebarsTemplateLoader handlebarsTemplateLoader = new HandlebarsTemplateLoader();

            JsonNode jsonNode = new ObjectMapper().readValue(wrappedContext, JsonNode.class);

            return handlebarsTemplateLoader.apply(smsType.getName(), jsonNode);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public enum SmsTypes {
        SIMPLE("simple"),
        ALERT("alert"),
        NOTIFICATION("notification"),
        INFORMATION("information"),
        TABLE("table"),
        LIST("list");

        private String name;

        SmsTypes(String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
