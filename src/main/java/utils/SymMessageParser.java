package utils;

import clients.SymBotClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import model.InboundMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.messageml.MessageMLContext;
import org.symphonyoss.symphony.messageml.exceptions.InvalidInputException;
import org.symphonyoss.symphony.messageml.exceptions.ProcessingException;

public class SymMessageParser {
    private final Logger logger = LoggerFactory.getLogger(SymMessageParser.class);
    private static SymMessageParser instance;
    private SymBotClient botClient;
    private static final String HASHTAG_TYPE = "org.symphonyoss.taxonomy.hashtag";
    private static final String CASHTAG_TYPE = "org.symphonyoss.fin.security.id.ticker";
    private static final String MENTION_TYPE = "com.symphony.user.userId";
    private static final String EMOJI_TYPE = "com.symphony.emoji";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SymMessageParser(SymBotClient botClient) {
        this.botClient = botClient;
    }

    public static SymMessageParser getInstance() {
        if (instance != null) {
            return instance;
        } else {
            throw new RuntimeException("SymMessageParser needs to be initialized at startup");
        }
    }

    public static SymMessageParser createInstance(SymBotClient botClient) {
        if (instance == null) {
            instance = new SymMessageParser(botClient);
            return instance;
        } else {
            return instance;
        }
    }

    public String messageToText(String message, String entityJSON) {
        MessageMLContext context = new MessageMLContext(new DataProvider(botClient));
        try {
            context.parseMessageML(message, entityJSON, "2.0");
            return context.getText(true);
        } catch (InvalidInputException | IOException | ProcessingException e) {
            logger.error("Error trying to parse MessageMl");
        }
        return null;
    }

    private static JsonNode readTree(String entityData) {
        try {
            if (entityData == null) {
                return MAPPER.createArrayNode();
            }
            return MAPPER.readTree(entityData);
        } catch (IOException e) {
            return MAPPER.createArrayNode();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> getTags(InboundMessage message, String type, Class<T> clazz) {
        if (clazz != Long.class && clazz != String.class) {
            return null;
        }
        return (List<T>) StreamSupport.stream(readTree(message.getData()).spliterator(), false)
            .filter(node -> node.has("id") && node.get("id").get(0).get("type").asText().equals(type))
            .map(node -> node.get("id").get(0).get("value"))
            .map(node -> clazz == Long.class ? node.asLong() : node.asText())
            .distinct()
            .collect(Collectors.toList());
    }

    public static List<Long> getMentions(InboundMessage message) {
        return getTags(message, MENTION_TYPE, Long.class);
    }

    public static List<String> getHashtags(InboundMessage message) {
        return getTags(message, HASHTAG_TYPE, String.class);
    }

    public static List<String> getCashtags(InboundMessage message) {
        return getTags(message, CASHTAG_TYPE, String.class);
    }

    public static Map<String, String> getEmojis(InboundMessage message) {
        return StreamSupport.stream(readTree(message.getData()).spliterator(), false)
            .filter(node -> node.has("type") && node.get("type").asText().equals(EMOJI_TYPE))
            .map(node -> node.get("data"))
            .distinct()
            .collect(Collectors.toMap(e -> e.get("annotation").asText(), e -> e.get("unicode").asText()));
    }
}
