package utils;

import clients.SymBotClient;
import exceptions.SymClientException;
import model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.messageml.exceptions.InvalidInputException;
import org.symphonyoss.symphony.messageml.util.IDataProvider;
import org.symphonyoss.symphony.messageml.util.IUserPresentation;

import javax.ws.rs.core.NoContentException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class DataProvider implements IDataProvider {
    private final Logger logger = LoggerFactory.getLogger(IDataProvider.class);
    private static final Set<String> STANDARD_URI_SCHEMES = new HashSet<>();
    private UserPresentation user;
    private SymBotClient botClient;

    public DataProvider(SymBotClient botClient) {
        STANDARD_URI_SCHEMES.add("http");
        STANDARD_URI_SCHEMES.add("https");
        this.botClient = botClient;
    }

    @Override
    public IUserPresentation getUserPresentation(String email) throws InvalidInputException {
        if (!email.equalsIgnoreCase(user.getEmail())) {
            throw new InvalidInputException("Failed to lookup user \"" + email + "\"");
        }

        return new UserPresentation(user.getId(), user.getScreenName(), user.getPrettyName(), email);
    }

    @Override
    public IUserPresentation getUserPresentation(Long uid) throws InvalidInputException {
        UserInfo userInfo = null;
        try {
            userInfo = botClient.getUsersClient().getUserFromId(uid, false);
        } catch (SymClientException | NoContentException e) {
            logger.error("Error with getUserPresentation", e);
            throw new InvalidInputException("User info not found for " + uid);
        }
        return new UserPresentation(uid, userInfo.getDisplayName(),userInfo.getDisplayName());
    }

    @Override
    public void validateURI(URI uri) throws InvalidInputException {
        if (!STANDARD_URI_SCHEMES.contains(uri.getScheme().toLowerCase())) {
            throw new InvalidInputException(
                    "URI scheme \"" + uri.getScheme() + "\" is not supported by the pod.");
        }
    }

    public void setUserPresentation(UserPresentation user) {
        this.user = user;
    }

    public void setUserPresentation(long id, String screenName, String prettyName, String email) {
        this.user = new UserPresentation(id, screenName, prettyName, email);
    }
}