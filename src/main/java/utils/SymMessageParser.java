package utils;

import clients.SymBotClient;
import org.symphonyoss.symphony.messageml.MessageMLContext;
import org.symphonyoss.symphony.messageml.exceptions.InvalidInputException;
import org.symphonyoss.symphony.messageml.exceptions.ProcessingException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SymMessageParser {

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
            try {
                throw new Exception("SymMessageParser needs to be initialized at startup");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
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
        } catch (InvalidInputException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}





