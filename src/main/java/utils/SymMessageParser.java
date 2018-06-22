package utils;

import org.symphonyoss.symphony.messageml.MessageMLContext;
import org.symphonyoss.symphony.messageml.exceptions.InvalidInputException;
import org.symphonyoss.symphony.messageml.exceptions.ProcessingException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SymMessageParser {

    final static String regex = "(\\<div.*\\>)(.*)(\\<\\/div\\>)";
    final static Pattern pattern = Pattern.compile(regex);

    public static String parseContent(String presentationML){
        Matcher matcher = pattern.matcher(presentationML);
        String content = null;
        while (matcher.find()) {
            content = matcher.group(2);
        }
        return content;
    }

    public static String messageToText(String message, String entityJSON){
        MessageMLContext context = new MessageMLContext(/*IDataProvider*/ new DataProvider());

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





