package util;

import org.junit.Assert;
import org.junit.Test;
import utils.SymMessageParser;

public class SymMessageParserTest {

    @Test
    public void parseMessageTest(){
        String presentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">hi</div>";
        String entityJSON = "{}";
        String text = SymMessageParser.getInstance().messageToText(presentationML,entityJSON);
        Assert.assertNotNull(text);
    }
}
