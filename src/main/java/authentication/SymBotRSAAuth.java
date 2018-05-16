package authentication;

import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.CommonConstants;
import clients.symphony.api.constants.PodConstants;
import configuration.SymConfig;
import exceptions.NoConfigException;
import exceptions.UnauthorizedException;
import model.InboundMessage;
import model.Token;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class SymBotRSAAuth implements ISymBotAuth {
    private final Logger logger = LoggerFactory.getLogger(SymBotRSAAuth.class);
    private String sessionToken;
    private String kmToken;
    private SymConfig config;
    private String jwt;

    public SymBotRSAAuth(SymConfig config) {
        this.config = config;
    }

    @Override
    public void authenticate() {
        long expiration =  300000;

        PrivateKey privateKey = null;
        try {
            privateKey = JwtHelper.parseRSAPrivateKey(new File(config.getBotPrivateKeyPath()+config.getBotPrivateKeyName()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        jwt = JwtHelper.createSignedJwt(config.getBotUsername(), expiration, privateKey);
        sessionAuthenticate();
        kmAuthenticate();

    }

    @Override
    public void sessionAuthenticate() {

        Map<String, String> token = new HashMap<>();
        token.put("token", jwt);
        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + config.getPodHost() + ":" + config.getPodPort())
                .path(AuthEndpointConstants.RSASESSIONAUTH)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(token,MediaType.APPLICATION_JSON));


        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                throw new UnauthorizedException("could not authenticate");
            } catch (UnauthorizedException e) {
                e.printStackTrace();
            }
        }
        else {
            sessionToken =  response.readEntity(Token.class).getToken();
        }
    }

    @Override
    public void kmAuthenticate() {
        Map<String, String> token = new HashMap<>();
        token.put("token", jwt);
        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + config.getKeyAuthHost() + ":" + config.getKeyAuthPort())
                .path(AuthEndpointConstants.RSAKMAUTH)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(token,MediaType.APPLICATION_JSON));




        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                throw new UnauthorizedException("could not authenticate");
            } catch (UnauthorizedException e) {
                e.printStackTrace();
            }
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
        Client client = ClientBuilder.newClient();
        Response response = client.target(AuthEndpointConstants.HTTPSPREFIX + config.getSessionAuthHost() + ":" + config.getSessionAuthPort())
                .path(AuthEndpointConstants.LOGOUTPATH)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken", getSessionToken())
                .post(null);
    }
}