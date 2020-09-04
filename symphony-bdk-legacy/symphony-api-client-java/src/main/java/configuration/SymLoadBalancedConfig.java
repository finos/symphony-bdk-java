package configuration;

import static utils.HttpClientBuilderHelper.getHttpClientBuilderWithTruststore;

import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.CommonConstants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import exceptions.SymClientException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import model.AgentInfo;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Slf4j
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SymLoadBalancedConfig extends SymConfig {

    private LoadBalancing loadBalancing;
    private List<String> agentServers = Collections.emptyList();

    private int currentAgentIndex = -1;

    private String actualAgentHost = null;
    private int actualAgentPort = -1;

    @Override
    public String getAgentHost() {
        boolean isSticky = loadBalancing.isStickySessions();
        switch (loadBalancing.getMethod()) {
            case random:
                if (currentAgentIndex == -1 || !isSticky) {
                    rotateAgent();
                    logger.info("Returning random agent index #{}: {}", currentAgentIndex, agentServers.get(currentAgentIndex));
                }
                return agentServers.get(currentAgentIndex);

            case roundrobin:
                if (currentAgentIndex == -1) {
                    currentAgentIndex++;
                }
                String roundRobinAgentHost = agentServers.get(currentAgentIndex);
                logger.info("Returning round-robin agent index #{}: {}", currentAgentIndex, roundRobinAgentHost);
                if (!isSticky) {
                    rotateAgent();
                }
                return roundRobinAgentHost;

            case external:
                if (actualAgentHost == null || !isSticky) {
                    logger.info("Retrieving actual agent hostname..");
                    rotateAgent();
                }
                logger.info("Actual agent host: {}", actualAgentHost);
                return actualAgentHost;

            default:
        }

        return super.getAgentHost();
    }

    public void rotateAgent() {
        String newAgent = null;
        switch (this.loadBalancing.getMethod()) {
            case random:
                currentAgentIndex = ThreadLocalRandom.current().nextInt(0, agentServers.size());
                newAgent = agentServers.get(currentAgentIndex);
                break;
            case roundrobin:
                if (++currentAgentIndex == agentServers.size()) {
                    currentAgentIndex = 0;
                }
                newAgent = agentServers.get(currentAgentIndex);
                break;
            case external:
                if (agentServers.size() != 1) {
                  throw new SymClientException("Load balancing configuration with method \"external\" must contain only one agent server");
                }
                actualAgentHost = getActualAgentHost(agentServers.get(0));
                newAgent = actualAgentHost;
                break;
            default:
        }
        logger.info("Agent rotated to: {}", newAgent);
    }

    /**
     * Retrieves actual Agent FQDN (Fully Qualified Domain Name) by calling /agent/v1/info endpoint.
     * @return actual Agent host
     */
    protected String getActualAgentHost(String externalLoadBalancerUrl) {
        // we assume host + port + context is put in the agentServers in the lb-config
        final String uri = CommonConstants.HTTPS_PREFIX + externalLoadBalancerUrl;
        final Response response = getHttpClientBuilderWithTruststore(this).build().target(uri)
            .path(AgentConstants.INFO)
            .request(MediaType.APPLICATION_JSON)
            .get();

        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            logger.error("Unable to get actual Agent hostname, cause : {}", response);
            return null;
        } else {
            final String agentServerFqdn = response.readEntity(AgentInfo.class).getServerFqdn();
            logger.debug("Agent FQDN={}", agentServerFqdn);
            return agentServerFqdn;
        }
    }

    public void cloneAttributes(SymConfig config) {
        try {
            BeanUtils.copyProperties(this, config);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("Unable to copy properties from " + config + " to this.", e);
        }
    }
}
