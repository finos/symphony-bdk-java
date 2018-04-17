package authentication;

public interface ISymBotAuth {


    public void authenticate();

    public void sessionAuthenticate();

    public void kmAuthenticate();

    public String getSessionToken();

    public void setSessionToken(String sessionToken);

    public String getKmToken();

    public void setKmToken(String kmToken);

    public void logout();

}
