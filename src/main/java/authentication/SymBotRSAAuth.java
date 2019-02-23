package authentication;

import clients.symphony.api.APIClient;
import clients.symphony.api.constants.CommonConstants;
import configuration.SymConfig;
import exceptions.UnauthorizedException;
import model.Token;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpClientBuilderHelper;
import utils.JwtHelper;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class SymBotRSAAuth extends APIClient implements ISymAuth {
    private final Logger logger = LoggerFactory.getLogger(SymBotRSAAuth.class);
    private String sessionToken;
    private String kmToken;
    private SymConfig config;
    private Client sessionAuthClient;
    private Client kmAuthClient;
    private String jwt;
    private long lastAuthTime=0;

    public SymBotRSAAuth(SymConfig config) {
        this.config = config;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config);
        Client client = clientBuilder.build();
        if(isEmpty(config.getProxyURL())){
            this.sessionAuthClient = client;
        }
        else {
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
        if(config.getKeyManagerProxyURL()==null || config.getKeyManagerProxyURL().equals("")){
            this.kmAuthClient = client;
        }
        else {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.connectorProvider(new ApacheConnectorProvider());
            clientConfig.property(ClientProperties.PROXY_URI, config.getKeyManagerProxyURL());
            if(config.getKeyManagerProxyUsername()!=null && config.getKeyManagerProxyUsername()!=null) {
                clientConfig.property(ClientProperties.PROXY_USERNAME,config.getKeyManagerProxyUsername());
                clientConfig.property(ClientProperties.PROXY_PASSWORD,config.getKeyManagerProxyPassword());
            }
            Client kmProxyClient = clientBuilder.withConfig(clientConfig).build();
            this.kmAuthClient = kmProxyClient;
        }
    }

    public SymBotRSAAuth(SymConfig config, ClientConfig sessionAuthClientConfig, ClientConfig kmAuthClientConfig) {
        logger.info("SymOBOAuth with ClientConfig variables");
        this.config = config;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config);
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

    @Override
    public void authenticate() {
        long expiration =  300000;

        PrivateKey privateKey = null;
        try {
            privateKey = JwtHelper.parseRSAPrivateKey(new File(config.getBotPrivateKeyPath()+config.getBotPrivateKeyName()));
        } catch (IOException | GeneralSecurityException e) {
            logger.error("Error trying to parse RSA private key", e);
        }
        if(lastAuthTime==0 | System.currentTimeMillis()-lastAuthTime>3000) {
            logger.info("Last auth time was {}", lastAuthTime);
            logger.info("Now is {}",System.currentTimeMillis());
            jwt = JwtHelper.createSignedJwt(config.getBotUsername(), expiration, privateKey);
            sessionAuthenticate();
            kmAuthenticate();
            lastAuthTime=System.currentTimeMillis();
        } else{
            try {
                logger.info("Re-authenticated too fast. Wait 30 seconds to try again.");
                TimeUnit.SECONDS.sleep(AuthEndpointConstants.TIMEOUT);
                authenticate();
            } catch (InterruptedException e) {
                logger.error("Error with authentication", e);
            }
        }

    }

    @Override
    public void sessionAuthenticate() {

        Map<String, String> token = new HashMap<>();
        token.put("token", jwt);
        Response response
                = this.sessionAuthClient.target(CommonConstants.HTTPSPREFIX + config.getPodHost() + ":" + config.getPodPort())
                .path(AuthEndpointConstants.RSASESSIONAUTH)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(token,MediaType.APPLICATION_JSON));

        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, null);
            } catch (Exception e){
                logger.error("Unexpected error, retry authentication in 30 seconds");
            }
            try {
                TimeUnit.SECONDS.sleep(AuthEndpointConstants.TIMEOUT);
            } catch (InterruptedException e) {
                logger.error("Error with authentication", e);
            }
            sessionAuthenticate();
        }
        else {
            sessionToken =  response.readEntity(Token.class).getToken();
        }
    }

    @Override
    public void kmAuthenticate() {
        Map<String, String> token = new HashMap<>();
        token.put("token", jwt);
        Response response
                = this.kmAuthClient.target(CommonConstants.HTTPSPREFIX + config.getKeyAuthHost() + ":" + config.getKeyAuthPort())
                .path(AuthEndpointConstants.RSAKMAUTH)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(token,MediaType.APPLICATION_JSON));

        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, null);
            } catch (Exception e){
                logger.error("Unexpected error, retry authentication in 30 seconds");
            }
            try {
                TimeUnit.SECONDS.sleep(AuthEndpointConstants.TIMEOUT);
            } catch (InterruptedException e) {
                logger.error("Error with authentication", e);
            }
            kmAuthenticate();
        }
        else {
            kmToken =  response.readEntity(Token.class).getToken();
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

    public void logout() {
        logger.info("Logging out");
        Client client = ClientBuilder.newClient();
        Response response = client.target(AuthEndpointConstants.HTTPSPREFIX + config.getSessionAuthHost() + ":" + config.getSessionAuthPort())
                .path(AuthEndpointConstants.LOGOUTPATH)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken", getSessionToken())
                .post(null);

        if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, null);
            } catch (Exception e) {
                logger.error("Unexpected error, retry logout in 30 seconds", e);
            }
        }
    }
}