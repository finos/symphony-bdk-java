package authentication;

import exceptions.AuthenticationException;

public interface ISymAuth {
    void authenticate() throws AuthenticationException;
    void sessionAuthenticate() throws AuthenticationException;
    void kmAuthenticate() throws AuthenticationException;
    String getSessionToken();
    void setSessionToken(String sessionToken);
    String getKmToken();
    void setKmToken(String kmToken);
    void logout();
}
