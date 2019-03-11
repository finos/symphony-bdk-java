package utils;

import clients.SymBotClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.messageml.MessageMLContext;
import org.symphonyoss.symphony.messageml.exceptions.InvalidInputException;
import org.symphonyoss.symphony.messageml.exceptions.ProcessingException;
import services.DatafeedEventsService;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SymMessageParser {

    private final Logger logger = LoggerFactory.getLogger(SymMessageParser.class);

    final static String regex = "(\\<div.*\\>)(.*)(\\<\\/div\\>)";
    final static Pattern pattern = Pattern.compile(regex);
    private static SymMessageParser instance;
    private SymBotClient botClient;

    protected SymMessageParser(SymBotClient botClient) {
        this.botClient = botClient;
    }

    public static SymMessageParser getInstance() {
        if(instance!=null){
            return instance;
        } else {
            throw new RuntimeException("SymMessageParser needs to be initialized at startup");
        }
    }

    public static SymMessageParser createInstance(SymBotClient botClient) {
        if(instance==null){
            instance = new SymMessageParser(botClient);
            return instance;
        } else {
            return instance;
        }
    }

    public String messageToText(String message, String entityJSON){
        MessageMLContext context = new MessageMLContext(/*IDataProvider*/ new DataProvider(botClient));
        /* Parse the message and entity data */
        try {
            context.parseMessageML(/*String*/ message, /*String*/ entityJSON, /*String*/ "2.0");
            return context.getText();
        } catch (InvalidInputException | IOException | ProcessingException e) {
            logger.error("Error trying to parse MessageMl");
        }
        return null;
    }
}





