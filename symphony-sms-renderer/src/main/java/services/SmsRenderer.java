package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;

@Slf4j
public final class SmsRenderer {
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

            Template template = handlebarsTemplateLoader.getTemplate(smsType.getName());
            Context templateContext = handlebarsTemplateLoader.getContext(jsonNode);

            return template.apply(templateContext);
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
