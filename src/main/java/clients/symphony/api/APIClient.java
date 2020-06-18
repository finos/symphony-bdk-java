package clients.symphony.api;

import clients.ISymClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.*;
import javax.ws.rs.core.Response;
import model.ClientError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static javax.ws.rs.core.Response.Status.*;

public abstract class APIClient {
    private final Logger logger = LoggerFactory.getLogger(APIClient.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private ClientError readClientError(Response response) {
        String errorString = response.readEntity(String.class);
        try {
            return mapper.readValue(errorString, ClientError.class);
        } catch (JsonProcessingException e) {
            ClientError error = new ClientError();
            error.setMessage(errorString);
            return error;
        }
    }

    protected void handleError(Response response, ISymClient botClient) throws SymClientException {
        if (response.getStatusInfo().getFamily() == Family.SERVER_ERROR) {
            logger.error("REST error: error code {} reason {}",
                response.getStatusInfo().getStatusCode(),
                response.getStatusInfo().getReasonPhrase()
            );
            throw new ServerErrorException(response.getStatusInfo().getReasonPhrase());
        } else {
            ClientError error = this.readClientError(response);
            if (response.getStatus() == BAD_REQUEST.getStatusCode()) {
                logger.error("Client error occurred: {}", error.getMessage());
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
