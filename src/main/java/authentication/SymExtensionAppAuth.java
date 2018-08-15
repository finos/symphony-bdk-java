package authentication;

import clients.symphony.api.APIClient;
import configuration.SymConfig;
import exceptions.NoConfigException;
import model.AppAuthResponse;
import model.PodCert;
import model.Token;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpClientBuilderHelper;

import javax.security.auth.login.LoginException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SymExtensionAppAuth extends APIClient {
    private final Logger logger = LoggerFactory.getLogger(SymExtensionAppAuth.class);
    private SymConfig config;
    private Client sessionAuthClient;

    public SymExtensionAppAuth(SymConfig config){
        this.config = config;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientAppBuilder(config);
        Client client = clientBuilder.build();
        if(config.getProxyURL()==null || config.getProxyURL().equals("")){
            this.sessionAuthClient = client;
        }
        else {
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

    public SymExtensionAppAuth(SymConfig config, ClientConfig sessionAuthClientConfig) {
        this.config = config;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientAppBuilder(config);
        if (sessionAuthClientConfig!=null){
            this.sessionAuthClient = clientBuilder.withConfig(sessionAuthClientConfig).build();
        } else {
            this.sessionAuthClient = clientBuilder.build();
        }
    }

    public AppAuthResponse sessionAppAuthenticate(String appToken) {
        if (config!=null) {
            logger.info("Session extension app auth");
            Map<String, String> input = new HashMap<>();
            input.put("appToken", appToken);
            Response response
                    = sessionAuthClient.target(AuthEndpointConstants.HTTPSPREFIX + config.getSessionAuthHost() + ":" + config.getSessionAuthPort())
                    .path(AuthEndpointConstants.SESSIONEXTAPPAUTH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(input, MediaType.APPLICATION_JSON));
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
                sessionAppAuthenticate(appToken);
            } else {
                AppAuthResponse appAuthResponse = response.readEntity(AppAuthResponse.class);
                return appAuthResponse;
            }
        } else {
            throw new NoConfigException("Must provide a SymConfig object to authenticate");

        }
        return null;
    }

    public AppAuthResponse sessionAppAuthenticate(String appToken, String podSessionAuthUrl) {
        if (config!=null) {
            logger.info("Session extension app auth");
            Map<String, String> input = new HashMap<>();
            input.put("appToken", appToken);
            Response response
                    = sessionAuthClient.target(AuthEndpointConstants.HTTPSPREFIX + podSessionAuthUrl)
                    .path(AuthEndpointConstants.SESSIONEXTAPPAUTH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(input, MediaType.APPLICATION_JSON));
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
                sessionAppAuthenticate(appToken,podSessionAuthUrl);
            } else {
                AppAuthResponse appAuthResponse = response.readEntity(AppAuthResponse.class);
                return appAuthResponse;
            }
        } else {
            throw new NoConfigException("Must provide a SymConfig object to authenticate");

        }
        return null;
    }

    public Object verifyJWT(String jwt){
        Response response
                = sessionAuthClient.target(AuthEndpointConstants.HTTPSPREFIX  + config.getSessionAuthHost() + ":" + config.getSessionAuthPort())
                .path(AuthEndpointConstants.PODCERT)
                .request(MediaType.APPLICATION_JSON)
                .get();
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
            verifyJWT(jwt);
        } else {
            PodCert cert = response.readEntity(PodCert.class);
            // Get the public key from the cert
            PublicKey publicKey;
            try {

                X509Certificate x509Certificate = parseX509Certificate(cert.getCertificate());
                publicKey = x509Certificate.getPublicKey();
                JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                        .setVerificationKey(publicKey)
                        .setSkipAllValidators()
                        .build();

                // validate and decode the jwt
                JwtClaims jwtDecoded = jwtConsumer.processToClaims(jwt);
                return jwtDecoded.getClaimValue("user");
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (InvalidJwtException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Object verifyJWT(String jwt, String podSessionAuthUrl){
        Response response
                = sessionAuthClient.target(AuthEndpointConstants.HTTPSPREFIX  + podSessionAuthUrl)
                .path(AuthEndpointConstants.PODCERT)
                .request(MediaType.APPLICATION_JSON)
                .get();
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
            verifyJWT(jwt, podSessionAuthUrl);
        } else {
            PodCert cert = response.readEntity(PodCert.class);
            // Get the public key from the cert
            PublicKey publicKey;
            try {

                X509Certificate x509Certificate = parseX509Certificate(cert.getCertificate());
                publicKey = x509Certificate.getPublicKey();
                JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                        .setVerificationKey(publicKey)
                        .setSkipAllValidators()
                        .build();

                // validate and decode the jwt
                JwtClaims jwtDecoded = jwtConsumer.processToClaims(jwt);
                return jwtDecoded.getClaimValue("user");
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (InvalidJwtException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private X509Certificate parseX509Certificate(final String certificateString)
            throws GeneralSecurityException {
        try {
            CertificateFactory f = CertificateFactory.getInstance("X.509");
            byte [] bytes = certificateString.getBytes("UTF-8");
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            X509Certificate certificate = (X509Certificate)f.generateCertificate(stream);
            return certificate;

        } catch (UnsupportedEncodingException e) {
            throw new GeneralSecurityException(e);
        }
    }



}
