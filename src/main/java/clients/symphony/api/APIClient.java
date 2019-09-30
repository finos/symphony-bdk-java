package clients.symphony.api;

import clients.ISymClient;
import clients.symphony.api.constants.CommonConstants;
import exceptions.*;
import javax.ws.rs.core.Response;
import model.ClientError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class APIClient {
    private final Logger logger = LoggerFactory.getLogger(APIClient.class);

    protected void handleError(Response response, ISymClient botClient) throws SymClientException {
        if (response.getStatusInfo().getFamily()
                == Response.Status.Family.SERVER_ERROR) {
            logger.error("REST error: error code {} reason{}", response.getStatusInfo().getStatusCode(),
                response.getStatusInfo().getReasonPhrase());
            throw new ServerErrorException(response.getStatusInfo()
                    .getReasonPhrase());
        } else {
            ClientError error = response.readEntity((ClientError.class));
            if (response.getStatus() == CommonConstants.CLIENT_ERROR) {
                logger.error("Client error occurred", error);
                throw new APIClientErrorException(error.getMessage());
            } else if (response.getStatus() == CommonConstants.UNAUTHORIZED) {
                logger.error("User unauthorized, refreshing tokens");
                if (botClient != null) {
                    try {
                        botClient.getSymAuth().authenticate();
                    } catch (AuthenticationException e) {
                        throw new SymClientException("Authentication Exception");
                    }
                }
                throw new UnauthorizedException(error.getMessage());
            } else if (response.getStatus() == CommonConstants.FORBIDDEN) {
                logger.error("Forbidden: Caller lacks necessary entitlement.");
                throw new ForbiddenException(error.getMessage());
            }
        }
    }
}
