package utils;

import org.symphonyoss.symphony.messageml.exceptions.InvalidInputException;
import org.symphonyoss.symphony.messageml.util.IDataProvider;
import org.symphonyoss.symphony.messageml.util.IUserPresentation;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class DataProvider implements IDataProvider {
    private static final Set<String> STANDARD_URI_SCHEMES = new HashSet<>();
    private UserPresentation user;

    public DataProvider() {
        STANDARD_URI_SCHEMES.add("http");
        STANDARD_URI_SCHEMES.add("https");
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
        if (uid == null || user.getId() != uid) {
            throw new InvalidInputException("Failed to lookup user \"" + uid + "\"");
        }

        return new UserPresentation(uid, user.getScreenName(), user.getPrettyName());
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