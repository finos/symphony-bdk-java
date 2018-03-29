package utils;

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
}





