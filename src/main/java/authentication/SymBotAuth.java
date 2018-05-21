package authentication;

import configuration.SymConfig;
import exceptions.NoConfigException;
import model.Token;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
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
    private Client sessionAuthClient;
    private Client kmAuthClient;

    public SymBotAuth(SymConfig config){
        this.config = config;

        if(config.getProxyURL()==null){
            Client client = ClientBuilder.newClient();
            this.sessionAuthClient = client;
            this.kmAuthClient = client;
        }
        else {
            Client client = ClientBuilder.newClient();
            this.kmAuthClient = client;
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.property(ClientProperties.PROXY_URI, config.getProxyURL());
            if(config.getProxyUsername()!=null && config.getProxyPassword()!=null) {
                clientConfig.property(ClientProperties.PROXY_USERNAME,config.getProxyUsername());
                clientConfig.property(ClientProperties.PROXY_PASSWORD,config.getProxyPassword());
            }
            Client proxyClient =  ClientBuilder.newClient(clientConfig);
            this.sessionAuthClient = proxyClient;
        }
        //TODO: not use system properties
        if(config.getTruststorePath()!=null) {
            System.setProperty("javax.net.ssl.trustStore", config.getTruststorePath());
        }
        if (config.getTruststorePassword() != null) {
            System.setProperty("javax.net.ssl.trustStorePassword", config.getTruststorePassword());
        }

        System.setProperty("javax.net.ssl.keyStore", config.getBotCertPath()+config.getBotCertName()+".p12");
        System.setProperty("javax.net.ssl.keyStorePassword", config.getBotCertPassword());
        System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
    }

    public SymBotAuth(SymConfig config, Client sessionAuthClient, Client kmAuthClient) {
        this.config = config;
        this.sessionAuthClient = sessionAuthClient;
        this.kmAuthClient = kmAuthClient;
        if(config.getTruststorePath()!=null) {
            System.setProperty("javax.net.ssl.trustStore", config.getTruststorePath());
        }
        if (config.getTruststorePassword() != null) {
            System.setProperty("javax.net.ssl.trustStorePassword", config.getTruststorePassword());
        }

        System.setProperty("javax.net.ssl.keyStore", config.getBotCertPath()+config.getBotCertName()+".p12");
        System.setProperty("javax.net.ssl.keyStorePassword", config.getBotCertPassword());
        System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
    }

    public void authenticate(){
        sessionAuthenticate();
        kmAuthenticate();
    }

    public void sessionAuthenticate(){
        if (config!=null) {
            logger.info("Session auth");
            Response sessionTokenResponse
                    = sessionAuthClient.target(AuthEndpointConstants.HTTPSPREFIX + config.getSessionAuthHost() + ":" + config.getSessionAuthPort())
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
        logger.info("KM auth");
        if (config!=null) {
            Response kmTokenResponse
                    = kmAuthClient.target(AuthEndpointConstants.HTTPSPREFIX+config.getKeyAuthHost()+":"+config.getKeyAuthPort())
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
        Response response = sessionAuthClient.target(AuthEndpointConstants.HTTPSPREFIX+config.getSessionAuthHost()+":"+config.getSessionAuthPort())
                .path(AuthEndpointConstants.LOGOUTPATH)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",getSessionToken())
                .post(null);
    }
}
