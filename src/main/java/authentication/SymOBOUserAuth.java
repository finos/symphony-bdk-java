package authentication;

import configuration.SymConfig;
import model.ClientError;
import model.SessionToken;
import model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Session;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SymOBOUserAuth implements ISymAuth {
    private final Logger logger = LoggerFactory.getLogger(SymOBOUserAuth.class);
    private String sessionToken;
    private SymConfig config;
    private Client sessionAuthClient;
    private Long uid;
    private String username;
    SymOBOAuth appAuth;

    public SymOBOUserAuth(SymConfig config, Client sessionAuthClient, Long uid, SymOBOAuth appAuth) {
        this.config = config;
        this.sessionAuthClient = sessionAuthClient;
        this.uid = uid;
        this.appAuth = appAuth;
    }

    public SymOBOUserAuth(SymConfig config, Client sessionAuthClient, String username, SymOBOAuth appAuth) {
        this.config = config;
        this.sessionAuthClient = sessionAuthClient;
        this.username = username;
        this.appAuth = appAuth;
    }

    @Override
    public void authenticate() {
        sessionAuthenticate();
    }

    @Override
    public void sessionAuthenticate() {
        Response response = null;
        if (uid != null) {
            response= sessionAuthClient.target(AuthEndpointConstants.HTTPSPREFIX + config.getSessionAuthHost() + ":" + config.getSessionAuthPort())
                .path(AuthEndpointConstants.OBOUSERAUTH.replace("{uid}", Long.toString(uid)))
                .request(MediaType.APPLICATION_JSON)
                    .header("sessionToken", appAuth.getSessionToken())
                .post(null);
        } else {
            response = sessionAuthClient.target(AuthEndpointConstants.HTTPSPREFIX + config.getSessionAuthHost() + ":" + config.getSessionAuthPort())
                .path(AuthEndpointConstants.OBOUSERAUTHUSERNAME.replace("{username}", username))
                .request(MediaType.APPLICATION_JSON)
                    .header("sessionToken", appAuth.getSessionToken())
                    .post(null);
        }
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                ClientError error = response.readEntity((ClientError.class));
                if (response.getStatus() == 400){
                    logger.error("Client error occurred", error);
                } else if (response.getStatus() == 401){
                    logger.error("User unauthorized, refreshing tokens");
                } else if (response.getStatus() == 403){
                    logger.error("Forbidden: Caller lacks necessary entitlement.");
                } else if (response.getStatus() == 500) {
                    logger.error(error.getMessage());
                }
            } catch (Exception e){
                logger.error("Unexpected error");
                e.printStackTrace();
            }
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            appAuth.sessionAppAuthenticate();
            sessionAuthenticate();
        } else {
            SessionToken session = response.readEntity(SessionToken.class);
            this.sessionToken = session.getSessionToken();
        }
    }

    @Override
    public void kmAuthenticate() {
        logger.warn("method is invalid");
    }

    @Override
    public String getSessionToken() {
        return sessionToken;
    }

    @Override
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    @Override
    public String getKmToken() {
        logger.warn("method is invalid");
        return null;
    }

    @Override
    public void setKmToken(String kmToken) {
        logger.warn("method is invalid");
    }

    @Override
    public void logout() {

    }


}
