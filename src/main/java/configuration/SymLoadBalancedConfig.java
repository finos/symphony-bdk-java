package configuration;

import static utils.HttpClientBuilderHelper.getHttpClientBuilderWithTruststore;

import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.CommonConstants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
                    log.info("Returning random agent index #{}: {}", currentAgentIndex, agentServers.get(currentAgentIndex));
                }
                return agentServers.get(currentAgentIndex);

            case roundrobin:
                if (currentAgentIndex == -1) {
                    currentAgentIndex++;
                }
                String roundRobinAgentHost = agentServers.get(currentAgentIndex);
                log.info("Returning round-robin agent index #{}: {}", currentAgentIndex, roundRobinAgentHost);
                if (!isSticky) {
                    rotateAgent();
                }
                return roundRobinAgentHost;

            case external:
                if (actualAgentHost == null || !isSticky) {
                    log.info("Retrieving actual agent hostname..");
                    rotateAgent();
                }
                log.info("Actual agent host: {}", actualAgentHost);
                return actualAgentHost;

            default:
        }
        return super.getAgentHost();
    }

    @Override
    public int getAgentPort() {
        return super.getAgentPort();
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
                actualAgentHost = getActualAgentHost();
                newAgent = actualAgentHost;
                break;
            default:
        }
        log.info("Agent rotated to: {}", newAgent);
    }

    protected String getActualAgentHost() {

        String externalAgentHost = (this.agentServers != null && this.agentServers.size() > 0) ?
            this.agentServers.get(0) : this.getAgentHost();

        final String uri = CommonConstants.HTTPS_PREFIX + externalAgentHost + ":" + getAgentPort();

        final Response response = getHttpClientBuilderWithTruststore(this).build().target(uri)
            .path(AgentConstants.INFO)
            .request(MediaType.APPLICATION_JSON)
            .get();

        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            log.error("Unable to get actual hostname, cause : {}", response);
            return null;
        } else {
            return response.readEntity(AgentInfo.class).getServerFqdn();
        }
    }

    public void cloneAttributes(SymConfig config) {
        try {
            BeanUtils.copyProperties(this, config);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Unable to copy properties from " + config + " to this.", e);
        }
    }
}
