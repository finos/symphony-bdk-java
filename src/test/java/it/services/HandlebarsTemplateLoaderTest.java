package it.services;

import org.json.simple.JSONArray;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONObject;
import services.SmsRenderer;

public class HandlebarsTemplateLoaderTest {
    private static final Logger logger = LoggerFactory.getLogger(SmsRenderer.class);

    @Test
    public void passMessageContextAsString() {
        String messageContext = "{\"title\":\"Message Title\",\"content\":\"Message content\"}";

        String compiledTemplate = SmsRenderer.renderInBot(messageContext, SmsRenderer.SmsTypes.ALERT);
        logger.debug(compiledTemplate);

        assertNotNull(compiledTemplate);
    }

    @Test
    public void passMessageContextAsJSONObject() {
        JSONObject messageContext = new JSONObject();

        messageContext.put("title", "Message Title");
        messageContext.put("content", "Message content");
        messageContext.put("description", "Message description");

        String compiledTemplate = SmsRenderer.renderInBot(messageContext, SmsRenderer.SmsTypes.INFORMATION);
        logger.debug(compiledTemplate);

        assertNotNull(compiledTemplate);
    }

    @Test
    public void passListMessageContextAsJSONObject() {
        JSONObject workflows = new JSONObject();
        workflows.put("title", "workflows");
        workflows.put("content", " gives the list of workflows");

        JSONArray commands = new JSONArray();
        commands.add(workflows);

        JSONObject help = new JSONObject();
        help.put("title", "Here is a list of commands");
        help.put("content", commands);

        String compiledTemplate = SmsRenderer.renderInBot(help, SmsRenderer.SmsTypes.INFORMATION);
        logger.debug(compiledTemplate);

        assertNotNull(compiledTemplate);
    }
}