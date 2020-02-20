package clients.symphony.api;

import clients.ISymClient;
import exceptions.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.ClientError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static javax.ws.rs.core.Response.Status.*;

public abstract class APIClient {
    private final Logger logger = LoggerFactory.getLogger(APIClient.class);
    
    public Invocation.Builder createInvocationBuilder(Client client, String target, String path, String sessionToken) {
        Invocation.Builder builder = client
            .target(target)
            .path(path)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", sessionToken)
            .header("Cache-Control", "no-cache");
        
        return builder;
    }
    
    public Invocation.Builder createInvocationBuilderFromWebTarget(WebTarget webTarget, String path, String sessionToken) {
        Invocation.Builder builder = webTarget
            .path(path)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", sessionToken)
            .header("Cache-Control", "no-cache");
        
        return builder;
    }

    protected void handleError(Response response, ISymClient botClient) throws SymClientException {
        if (response.getStatusInfo().getFamily() == Family.SERVER_ERROR) {
            logger.error("REST error: error code {} reason {}",
                response.getStatusInfo().getStatusCode(),
                response.getStatusInfo().getReasonPhrase()
            );
            throw new ServerErrorException(response.getStatusInfo().getReasonPhrase());
        } else {
            ClientError error = response.readEntity(ClientError.class);
            if (response.getStatus() == BAD_REQUEST.getStatusCode()) {
                logger.error("Client error occurred: {}", error);
                throw new APIClientErrorException(error.getMessage());
            } else if (response.getStatus() == UNAUTHORIZED.getStatusCode()) {
                logger.error("User unauthorized, refreshing tokens");
                if (botClient != null) {
                    try {
                        botClient.getSymAuth().authenticate();
                    } catch (AuthenticationException e) {
                        throw new SymClientException("Authentication Exception");
                    }
                }
                throw new UnauthorizedException(error.getMessage());
            } else if (response.getStatus() == FORBIDDEN.getStatusCode()) {
                logger.error("Forbidden: Caller lacks necessary entitlement.");
                throw new ForbiddenException(error.getMessage());
            }
        }
    }
}
