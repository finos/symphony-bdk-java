package utils;

import model.InboundMessage;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import java.util.List;


public class SymMessageParserTest {

    private static InboundMessage mMessage;
    private static List<Long> mentions;

    @BeforeClass
    public static void setUp() {
        mMessage = new InboundMessage();
    }

    @Test
    public void testNormalGetMentions() {
        mMessage.setData("{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103609\"}],\"type\":\"com.symphony.user.mention\"}}");
        mentions = SymMessageParser.getMentions(mMessage);
        assertEquals(mentions.size(), 1);
        assertEquals(mentions.get(0).longValue(), 12987981103609L);
        mMessage.setData("{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103609\"}],\"type\":\"com.symphony.user.mention\"},"
                + "\"1\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103610\"}],\"type\":\"com.symphony.user.mention\"}}");
        mentions = SymMessageParser.getMentions(mMessage);
        assertEquals(mentions.size(), 2);
        assertEquals(mentions.get(0).longValue(), 12987981103609L);
        assertEquals(mentions.get(1).longValue(), 12987981103610L);
    }

    @Test
    public void testGetMentionsEmptyData() {
        mMessage.setData("");
        mentions = SymMessageParser.getMentions(mMessage);
        assertEquals(mentions.size(), 0);
    }

    @Test
    public void testGetMentionsNullData() {
        mMessage.setData(null);
        mentions = SymMessageParser.getMentions(mMessage);
        assertEquals(mentions.size(), 0);
    }

    @Test
    public void testGetMentionsBadFormatData() {
        mMessage.setData("{\"0\":{\"id\":[{\"bad-key-type\":\"com.symphony.user.userId\",\"value\":\"12987981103609\"}],\"type\":\"com.symphony.user.mention\"}}");
        mentions = SymMessageParser.getMentions(mMessage);
        assertEquals(mentions.size(), 0);
        mMessage.setData("{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"bad-key-value\":\"12987981103609\"}],\"type\":\"com.symphony.user.mention\"}}");
        mentions = SymMessageParser.getMentions(mMessage);
        assertEquals(mentions.size(), 0);
        mMessage.setData("{\"0\":{\"id\":[{\"bad-key-type\":\"com.symphony.user.userId\",\"value\":\"12987981103609\"}],\"type\":\"com.symphony.user.mention\"},"
                + "\"1\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103610\"}],\"type\":\"com.symphony.user.mention\"}}");
        mentions = SymMessageParser.getMentions(mMessage);
        assertEquals(mentions.size(), 1);
        assertEquals(mentions.get(0).longValue(), 12987981103610L);
    }

    @Test
    public void testGetMentionsUnmatchedType() {
        mMessage.setData("{\"0\":{\"id\":[{\"type\":\"unknown-type\",\"value\":\"12987981103609\"}],\"type\":\"com.symphony.user.mention\"}}");
        mentions = SymMessageParser.getMentions(mMessage);
        assertEquals(mentions.size(), 0);
        mMessage.setData("{\"0\":{\"id\":[{\"type\":\"unknown-type\",\"value\":\"12987981103609\"}],\"type\":\"com.symphony.user.mention\"},"
                + "\"1\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103610\"}],\"type\":\"com.symphony.user.mention\"}}");
        mentions = SymMessageParser.getMentions(mMessage);
        assertEquals(mentions.size(), 1);
        assertEquals(mentions.get(0).longValue(), 12987981103610L);
    }
}
