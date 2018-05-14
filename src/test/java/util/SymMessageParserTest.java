package util;

import org.junit.Assert;
import org.junit.Test;
import utils.SymMessageParser;

public class SymMessageParserTest {

    @Test
    public void parseMessageTest(){
        String presentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\"> " +
                "  <br/> " +
                "  <br/> " +
                "  <h2>Content grouping</h2> " +
                "  <hr/> " +
                "  <p>This is a paragraph, also, the line above is a Horizontal rule.</p> " +
                "  <h5>Unordered list</h5> " +
                "  <ul> " +
                "    <li>This is a list</li> " +
                "    <li>This is unordered</li> " +
                "  </ul> " +
                "  <h5>Ordered list</h5> " +
                "  <ol> " +
                "    <li>First do a list</li> " +
                "    <li>Then add numbers to it</li> " +
                "    <li>You have an ordered list</li> " +
                "  </ol> " +
                "  <h1>Header 1</h1> " +
                "  <h2>Header 2</h2> " +
                "  <h3>Header 3</h3> " +
                "  <h4>Header 4</h4> " +
                "  <h5>Header 5</h5> " +
                "  <h6>Header 6</h6> " +
                "  <br/> " +
                "  <div>This is a div tag, it creates a block of text</div> " +
                "</div> ";
        String entityJSON = "{}";
        String text = SymMessageParser.messageToText(presentationML,entityJSON);
        Assert.assertNotNull(text);
    }
}
