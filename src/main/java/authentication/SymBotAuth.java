package authentication;

import configuration.SymConfig;
import exceptions.*;
import model.ClientError;
import model.Token;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpClientBuilderHelper;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

public class SymBotAuth implements ISymAuth{
    private final Logger logger = LoggerFactory.getLogger(SymBotAuth.class);
    private String sessionToken;
    private String kmToken;
    private SymConfig config;
    private Client sessionAuthClient;
    private Client kmAuthClient;

    public SymBotAuth(SymConfig config){
        this.config = config;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientBotBuilder(config);
        Client client = clientBuilder.build();
        if(config.getProxyURL()==null){
            this.sessionAuthClient = client;
            this.kmAuthClient = client;
        }
        else {
            this.kmAuthClient = client;
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.property(ClientProperties.PROXY_URI, config.getProxyURL());
            if(config.getProxyUsername()!=null && config.getProxyPassword()!=null) {
                clientConfig.property(ClientProperties.PROXY_USERNAME,config.getProxyUsername());
                clientConfig.property(ClientProperties.PROXY_PASSWORD,config.getProxyPassword());
            }
            Client proxyClient = clientBuilder.withConfig(clientConfig).build();
            this.sessionAuthClient = proxyClient;
        }

    }

    public SymBotAuth(SymConfig config, ClientConfig sessionAuthClientConfig, ClientConfig kmAuthClientConfig) {
        this.config = config;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientBotBuilder(config);
        if (sessionAuthClientConfig!=null){
            this.sessionAuthClient = clientBuilder.withConfig(sessionAuthClientConfig).build();
        } else {
            this.sessionAuthClient = clientBuilder.build();
        }
        if (kmAuthClient==null){
            this.kmAuthClient = clientBuilder.withConfig(kmAuthClientConfig).build();
        } else {
            this.kmAuthClient = clientBuilder.build();
        }
    }


    public void authenticate(){
        sessionAuthenticate();
        kmAuthenticate();
    }

    public void sessionAuthenticate(){
        if (config!=null) {
            logger.info("Session auth");
            Response response
                    = sessionAuthClient.target(AuthEndpointConstants.HTTPSPREFIX + config.getSessionAuthHost() + ":" + config.getSessionAuthPort())
                    .path(AuthEndpointConstants.SESSIONAUTHPATH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(null);
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
                sessionAuthenticate();
            } else {
                Token sessionTokenResponseContent = response.readEntity(Token.class);
                this.sessionToken = sessionTokenResponseContent.getToken();
            }
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
            Response response
                    = kmAuthClient.target(AuthEndpointConstants.HTTPSPREFIX+config.getKeyAuthHost()+":"+config.getKeyAuthPort())
                    .path(AuthEndpointConstants.KEYAUTHPATH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(null);
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
                kmAuthenticate();
            } else {
                Token kmTokenResponseContent = response.readEntity(Token.class);
                this.kmToken = kmTokenResponseContent.getToken();
            }

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
