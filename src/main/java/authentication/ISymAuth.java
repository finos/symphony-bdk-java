package authentication;

public interface ISymAuth {
    void authenticate();
    void sessionAuthenticate();
    void kmAuthenticate();
    String getSessionToken();
    void setSessionToken(String sessionToken);
    String getKmToken();
    void setKmToken(String kmToken);
    void logout();
}
