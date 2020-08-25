package utils;

import org.symphonyoss.symphony.messageml.util.IUserPresentation;

public class UserPresentation implements IUserPresentation {
    private final long id;
    private final String screenName;
    private final String prettyName;
    private final String email;

    public UserPresentation(long id, String screenName, String prettyName) {
        this(id, screenName, prettyName, null);
    }

    public UserPresentation(long id, String screenName, String prettyName, String email) {
        this.id = id;
        this.screenName = screenName;
        this.prettyName = prettyName;
        this.email = email;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getScreenName() {
        return screenName;
    }

    @Override
    public String getPrettyName() {
        return prettyName;
    }

    @Override
    public String getEmail() {
        return email;
    }

}