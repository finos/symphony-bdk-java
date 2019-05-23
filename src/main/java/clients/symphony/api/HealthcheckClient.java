package clients.symphony.api;

import static clients.symphony.api.constants.AgentConstants.HEALTHCHECK;
import static clients.symphony.api.constants.HttpMethod.GET;
import static clients.symphony.api.constants.QueryParameterNames.SHOW_FIREHOSE_ERRORS;

import clients.ISymClient;
import clients.symphony.api.constants.CommonConstants;
import clients.symphony.api.constants.HttpMethod;
import model.HealthcheckResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class HealthcheckClient extends APIClient {
    private final Logger logger = LoggerFactory.getLogger(HealthcheckClient.class);
    private ISymClient botClient;
    private final String target;

    public HealthcheckClient(ISymClient client) {
        botClient = client;
        target = CommonConstants.HTTPS_PREFIX + botClient.getConfig().getAgentHost()
            + ":" + botClient.getConfig().getAgentPort();
    }

    public HealthcheckResponse performHealthCheck() {
        Boolean showFirehoseErros = botClient.getConfig().getShowFirehoseErrors();
        if (showFirehoseErros != null && showFirehoseErros) {
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(SHOW_FIREHOSE_ERRORS.getName(), Boolean.TRUE);
            return (HealthcheckResponse) doRequest(HEALTHCHECK, GET, HealthcheckResponse.class,
                parameters);
        }
        return (HealthcheckResponse) doRequest(HEALTHCHECK, GET, HealthcheckResponse.class);
    }

    private Object doRequest(String path, HttpMethod method, Class clazz) {
        return doRequest(path, method, clazz, null);
    }

    private Object doRequest(String path, HttpMethod method, Class clazz,
        Map<String, Object> queryParams) {
        try {
            WebTarget webTarget = botClient.getAgentClient().target(target).path(path);
            if (queryParams != null) {
                for(Map.Entry<String, Object> entry:queryParams.entrySet()){
                    webTarget = webTarget.queryParam(entry.getKey(), entry.getValue());
                }
            }
            Response response = webTarget.request(MediaType.APPLICATION_JSON_TYPE)
                .header("sessionToken", botClient.getSymAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymAuth().getKmToken())
                .method(method.name());
            if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                return response.readEntity(clazz);
            }
            handleError(response, botClient);
        } catch (ResponseProcessingException e) {
            logger.error(e.getMessage(), e);
        } catch (ProcessingException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}
