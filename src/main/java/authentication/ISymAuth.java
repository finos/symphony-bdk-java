package authentication;

import exceptions.AuthenticationException;

public interface ISymAuth {

    void authenticate() throws AuthenticationException;
    void sessionAuthenticate() throws AuthenticationException;
    void kmAuthenticate() throws AuthenticationException;

    String getSessionToken();
    @Deprecated void setSessionToken(String sessionToken);

    String getKmToken();
    @Deprecated void setKmToken(String kmToken);

    void logout();
}
