package clients.symphony.api;

import clients.ISymClient;
import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.HttpMethod;
import clients.symphony.api.constants.QueryParameterNames;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import model.HealthcheckResponse;

public class HealthcheckClient extends APIClient {
    private ISymClient botClient;

    public HealthcheckClient(ISymClient client) {
        botClient = client;
    }

    public HealthcheckResponse performHealthCheck() {
        boolean showFirehoseErrors = botClient.getConfig().getShowFirehoseErrors();
        if (showFirehoseErrors) {
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(QueryParameterNames.SHOW_FIREHOSE_ERRORS.getName(), Boolean.TRUE);
            return doRequest(AgentConstants.HEALTHCHECK, HttpMethod.GET, HealthcheckResponse.class, parameters);
        }
        return doRequest(AgentConstants.HEALTHCHECK, HttpMethod.GET, HealthcheckResponse.class);
    }

    private <T> T doRequest(String path, HttpMethod method, Class<T> clazz) {
        return doRequest(path, method, clazz, null);
    }

    private <T> T doRequest(String path, HttpMethod method, Class<T> clazz, Map<String, Object> queryParams) {

        WebTarget webTarget = botClient.getAgentClient()
            .target(botClient.getConfig().getAgentUrl());

        if (queryParams != null) {
            for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                webTarget = webTarget.queryParam(entry.getKey(), entry.getValue());
            }
        }

        Invocation.Builder builder = createInvocationBuilderFromWebTarget(webTarget, path, 
            botClient.getSymAuth().getSessionToken());
            
         builder = builder.header("keyManagerToken", botClient.getSymAuth().getKmToken());

        try (Response response = builder.method(method.name())) {
            if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                return response.readEntity(clazz);
            }
            handleError(response, botClient);
            return null;
        }
    }
}
