package authentication;

import clients.symphony.api.APIClient;
import configuration.SymConfig;
import exceptions.NoConfigException;
import model.AppAuthResponse;
import model.PodCert;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpClientBuilderHelper;
import utils.JwtHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public final class SymExtensionAppRSAAuth extends APIClient {
    private final Logger logger = LoggerFactory.getLogger(SymExtensionAppRSAAuth.class);
    private SymConfig config;
    private Client sessionAuthClient;
    private String jwt;
    private long expiration =  300000;

    public SymExtensionAppRSAAuth(final SymConfig configuration) {
        this.config = configuration;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientAppBuilder(config);
        Client client = clientBuilder.build();
        if (config.getProxyURL() == null || config.getProxyURL().equals("")) {
            this.sessionAuthClient = client;
        } else {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.property(ClientProperties.PROXY_URI, config.getProxyURL());
            if (config.getProxyUsername() != null && config.getProxyPassword() != null) {
                clientConfig.property(ClientProperties.PROXY_USERNAME, config.getProxyUsername());
                clientConfig.property(ClientProperties.PROXY_PASSWORD, config.getProxyPassword());
            }
            Client proxyClient = clientBuilder.withConfig(clientConfig).build();
            this.sessionAuthClient = proxyClient;
        }
    }

    public SymExtensionAppRSAAuth(final SymConfig configuration,
                               final ClientConfig sessionAuthClientConfig) {
        this.config = configuration;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientAppBuilder(config);
        if (sessionAuthClientConfig != null) {
            this.sessionAuthClient = clientBuilder.withConfig(sessionAuthClientConfig).build();
        } else {
            this.sessionAuthClient = clientBuilder.build();
        }
    }

    public AppAuthResponse RSAAppAuthenticate() {
      PrivateKey privateKey = getPrivateKey();
        if (config != null) {
            logger.info("RSA extension app auth");
            jwt = JwtHelper.createSignedJwt(config.getAppId(), expiration, privateKey);
            Map<String, String> token = new HashMap<>();
            token.put("token", jwt);
            Response response
                    = sessionAuthClient.target(AuthEndpointConstants.HTTPSPREFIX +
                config.getSessionAuthHost() + ":" + config.getSessionAuthPort())
                    .path(AuthEndpointConstants.RSASESSIONEXTAPPAUTH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(token, MediaType.APPLICATION_JSON));
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, null);
                } catch (Exception e) {
                    logger.error("Unexpected error, retry authentication in 30 seconds");
                }
                try {
                    TimeUnit.SECONDS.sleep(AuthEndpointConstants.TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                RSAAppAuthenticate();
            } else {
                AppAuthResponse appAuthResponse = response.readEntity(AppAuthResponse.class);
                return appAuthResponse;
            }
        } else {
            throw new NoConfigException("Must provide a SymConfig object to authenticate");

        }
        return null;
    }

    private PrivateKey getPrivateKey() {
        PrivateKey privateKey = null;
        try {
            privateKey = JwtHelper.parseRSAPrivateKey(
                new File(config.getAppPrivateKeyPath() + config.getAppPrivateKeyName()));
        } catch (IOException | GeneralSecurityException e) {
            logger.error(e.getMessage());
        }
        return privateKey;
    }

}
