package authentication;

import clients.symphony.api.APIClient;
import configuration.SymConfig;
import model.ClientError;
import model.SessionToken;
import model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MethodNotSupportedException;
import javax.mail.Session;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SymOBOUserAuth extends APIClient implements ISymAuth {
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
                handleError(response, null);
            } catch (Exception e){
                logger.error("Unexpected error, retry authentication in 30 seconds");
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
        try {
            throw new MethodNotSupportedException("this method is not supported");
        } catch (MethodNotSupportedException e) {
            e.printStackTrace();
        }
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
        try {
            throw new MethodNotSupportedException("this method is not supported");
        } catch (MethodNotSupportedException e) {
            e.printStackTrace();
        }
        logger.warn("method is invalid");
    }

    @Override
    public void logout() {

    }


}
