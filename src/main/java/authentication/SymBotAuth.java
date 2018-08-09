package authentication;

import clients.symphony.api.APIClient;
import configuration.SymConfig;
import exceptions.*;
import model.ClientError;
import model.Token;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
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

public class SymBotAuth extends APIClient implements ISymAuth{
    private final Logger logger = LoggerFactory.getLogger(SymBotAuth.class);
    private String sessionToken;
    private String kmToken;
    private SymConfig config;
    private Client sessionAuthClient;
    private Client kmAuthClient;
    private long lastAuthTime=0;

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
            clientConfig.connectorProvider(new ApacheConnectorProvider());
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
        if(lastAuthTime==0 | System.currentTimeMillis()-lastAuthTime>3000) {
            sessionAuthenticate();
            kmAuthenticate();
        } else{
            try {
                logger.info("Re-authenticated too fast. Wait 30 seconds to try again.");
                TimeUnit.SECONDS.sleep(30);
                authenticate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
                    handleError(response, null);
                } catch (Exception e){
                    logger.error("Unexpected error, retry authentication in 30 seconds");
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
            throw new NoConfigException("Must provide a SymConfig object to authenticate");
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
                    handleError(response, null);
                } catch (Exception e){
                    logger.error("Unexpected error, retry authentication in 30 seconds");
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
