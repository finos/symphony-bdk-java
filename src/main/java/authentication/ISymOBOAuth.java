package authentication;

public interface ISymOBOAuth {
    ISymAuth getUserAuth(final String username);

    ISymAuth getUserAuth(final Long uid);

    void sessionAppAuthenticate();

    String getSessionToken();

    void setSessionToken(final String sessionToken);
}
