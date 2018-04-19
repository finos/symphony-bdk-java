package authentication;

import configuration.SymConfig;
import exceptions.NoConfigException;
import model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SymBotAuth implements ISymBotAuth{
    private final Logger logger = LoggerFactory.getLogger(SymBotAuth.class);
    private String sessionToken;
    private String kmToken;
    private SymConfig config;

    public SymBotAuth(SymConfig config){
        this.config = config;
        //TODO: not use system properties
        System.setProperty("javax.net.ssl.trustStore", config.getTruststorePath());
        if (config.getTruststorePassword() != null) {
            System.setProperty("javax.net.ssl.trustStorePassword", config.getTruststorePassword());
        }

        System.setProperty("javax.net.ssl.keyStore", config.getBotCertPath()+config.getBotCertName()+".p12");
        System.setProperty("javax.net.ssl.keyStorePassword", config.getBotCertPassword());
        System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
//        TODO: Add proxy support
    }

    public void authenticate(){
        sessionAuthenticate();
        kmAuthenticate();
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        authenticate();
                    }
                },
                config.getAuthTokenRefreshPeriod()*1000*60
        );
    }

    public void sessionAuthenticate(){
        if (config!=null) {
            Client client = ClientBuilder.newClient();
            Response sessionTokenResponse
                    = client.target(AuthEndpointConstants.HTTPSPREFIX + config.getSessionAuthHost() + ":" + config.getSessionAuthPort())
                    .path(AuthEndpointConstants.SESSIONAUTHPATH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(null);
            Token sessionTokenResponseContent = sessionTokenResponse.readEntity(Token.class);
            this.sessionToken = sessionTokenResponseContent.getToken();
        } else {
            try {
                throw new NoConfigException("Must provide a SymConfig object to authenticate");
            } catch (NoConfigException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void kmAuthenticate(){
        if (config!=null) {
            Client client = ClientBuilder.newClient();
            Response kmTokenResponse
                    = client.target(AuthEndpointConstants.HTTPSPREFIX+config.getKeyAuthHost()+":"+config.getKeyAuthPort())
                    .path(AuthEndpointConstants.KEYAUTHPATH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(null);
            Token kmTokenResponseContent = kmTokenResponse.readEntity(Token.class);
            this.kmToken = kmTokenResponseContent.getToken();
        } else {
            try {
                throw new NoConfigException("Must provide a SymConfig object to authenticate");
            } catch (NoConfigException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getKmToken() {
        return kmToken;
    }

    public void setKmToken(String kmToken) {
        this.kmToken = kmToken;
    }

    public void logout(){
        Client client = ClientBuilder.newClient();
        Response response = client.target(AuthEndpointConstants.HTTPSPREFIX+config.getSessionAuthHost()+":"+config.getSessionAuthPort())
                .path(AuthEndpointConstants.LOGOUTPATH)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",getSessionToken())
                .post(null);
    }
}
