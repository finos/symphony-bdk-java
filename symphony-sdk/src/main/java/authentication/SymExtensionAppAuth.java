package authentication;

import authentication.extensionapp.TokensRepository;
import configuration.SymConfig;
import lombok.extern.slf4j.Slf4j;
import model.AppAuthResponse;
import model.UserInfo;
import org.glassfish.jersey.client.ClientConfig;
import utils.HttpClientBuilderHelper;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static utils.JwtHelper.validateJwt;

@Slf4j
public final class SymExtensionAppAuth extends AbstractSymExtensionAppAuth {

    /**
     * Create an instance initialized with provided Symphony configuration.
     *
     * @param config the Symphony configuration
     */
    public SymExtensionAppAuth(SymConfig config) {
        super(config);
        ClientConfig clientConfig = HttpClientBuilderHelper.getPodClientConfig(config);
        this.initSessionAuthClient(config, clientConfig);
    }

    /**
     * Create an instance initialized with provided Symphony and session authentication client configuration.
     *
     * @param config                  the Symphony configuration
     * @param sessionAuthClientConfig the session authentication client configuration
     */
    public SymExtensionAppAuth(SymConfig config, ClientConfig sessionAuthClientConfig) {
        super(config);
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientAppBuilder(config);
        this.initSessionAuthClient(config, sessionAuthClientConfig);
    }

    public SymExtensionAppAuth(SymConfig config, ClientConfig sessionAuthClientConfig, TokensRepository tokensRepository) {
        super(config, tokensRepository);
        this.initSessionAuthClient(config, sessionAuthClientConfig);
    }

    private void initSessionAuthClient(SymConfig config, ClientConfig clientConfig) {
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientAppBuilder(config);
        if (clientConfig != null) {
            this.sessionAuthClient = clientBuilder.withConfig(clientConfig).build();
        } else {
            this.sessionAuthClient = clientBuilder.build();
        }
    }

    @Override
    public AppAuthResponse appAuthenticate() {
        String appToken = this.generateToken();
        return sessionAppAuthenticate(appToken);
    }

    @Override
    public AppAuthResponse sessionAppAuthenticate(String appToken, String... podSessionAuthUrl) {
        String target = this.formattedPodSessionAuthUrl(podSessionAuthUrl);
        logger.info("Session extension app auth");
        Map<String, String> input = new HashMap<>();
        input.put("appToken", appToken);

        Invocation.Builder builder = sessionAuthClient.target(target)
                .path(AuthEndpointConstants.SESSION_EXT_APP_AUTH_PATH)
                .request(MediaType.APPLICATION_JSON);

        try (Response response = builder.post(Entity.entity(input, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                AppAuthResponse appAuthResponse = response.readEntity(AppAuthResponse.class);
                return tokensRepository.save(appAuthResponse);
            } else {
                return handleSessionAppAuthFailure(response, appToken, podSessionAuthUrl);
            }
        }
    }

    @Override
    public UserInfo verifyJWT(String jwt, String... podSessionAuthUrl) {
        String podCertificate = this.getPodCertificate(podSessionAuthUrl);
        return validateJwt(jwt, podCertificate);
    }

    @Override
    public Boolean validateTokens(String appToken, String symphonyToken) {
        return tokensRepository.get(appToken)
                .filter(token -> token.getSymphonyToken().equals(symphonyToken))
                .isPresent();
    }

    private String getPodCertificate(String... podSessionAuthUrl) {
        return this.getPodCertificateFromCertPath(AuthEndpointConstants.POD_CERT_PATH, podSessionAuthUrl);
    }

}
