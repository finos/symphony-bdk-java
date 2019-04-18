package configuration;

import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.CommonConstants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.FqdnHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpClientBuilderHelper;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SymLoadBalancedConfig extends SymConfig {
    private int currentAgentIndex = -1;
    private String actualAgentHost;
    private LoadBalancing loadBalancing;
    private List<String> agentServers;
    private static final Logger logger = LoggerFactory.getLogger(SymLoadBalancedConfig.class);

    public LoadBalancing getLoadBalancing() {
        return loadBalancing;
    }

    public void setLoadBalancing(LoadBalancing loadBalancing) {
        this.loadBalancing = loadBalancing;
    }

    public List<String> getAgentServers() {
        return agentServers;
    }

    public void setAgentServers(List<String> agentServers) {
        this.agentServers = agentServers;
    }

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
        }
        return super.getAgentHost();
    }

    public void rotateAgent() {
        String newAgent = null;
        switch (loadBalancing.getMethod()) {
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
        }
        logger.info("Agent rotated to: {}", newAgent);
    }

    @Override
    public int getAgentPort() {
        return super.getAgentPort();
    }

    private String getActualAgentHost() {
        String externalAgentHost = (agentServers != null && agentServers.size() > 0) ?
            agentServers.get(0) : getAgentHost();

        Client client = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(this).build();

        Response response
            = client.target(CommonConstants.HTTPS_PREFIX
            + externalAgentHost
            + ":" + getAgentPort())
            .path(AgentConstants.GETHOST)
            .request(MediaType.APPLICATION_JSON)
            .get();

        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            logger.error("Unable to get actual hostname");
            return null;
        } else {
            return response.readEntity(FqdnHost.class).getServerFqdn();
        }
    }

    public void cloneAttributes(SymConfig config) {
        this.setSessionAuthHost(config.getSessionAuthHost());
        this.setSessionAuthPort(config.getSessionAuthPort());
        this.setKeyAuthHost(config.getKeyAuthHost());
        this.setKeyAuthPort(config.getKeyAuthPort());
        this.setPodHost(config.getPodHost());
        this.setPodPort(config.getPodPort());
        this.setAgentHost(config.getAgentHost());
        this.setAgentPort(config.getAgentPort());
        this.setBotCertPath(config.getBotCertPath());
        this.setBotCertName(config.getBotCertName());
        this.setBotCertPassword(config.getBotCertPassword());
        this.setBotEmailAddress(config.getBotEmailAddress());
        this.setAppCertPath(config.getAppCertPath());
        this.setAppCertName(config.getAppCertName());
        this.setAppCertPassword(config.getAppCertPassword());
        this.setProxyURL(config.getProxyURL());
        this.setProxyUsername(config.getProxyUsername());
        this.setProxyPassword(config.getProxyPassword());
        this.setPodProxyURL(config.getPodProxyURL());
        this.setPodProxyUsername(config.getPodProxyUsername());
        this.setPodProxyPassword(config.getPodProxyPassword());
        this.setKeyManagerProxyURL(config.getKeyManagerProxyURL());
        this.setKeyManagerProxyUsername(config.getKeyManagerProxyUsername());
        this.setKeyManagerProxyPassword(config.getKeyManagerProxyPassword());
        this.setAuthTokenRefreshPeriod(config.getAuthTokenRefreshPeriod());
        this.setTruststorePath(config.getTruststorePath());
        this.setTruststorePassword(config.getTruststorePassword());
        this.setBotUsername(config.getBotUsername());
        this.setBotPrivateKeyName(config.getBotPrivateKeyName());
        this.setBotPrivateKeyPath(config.getBotPrivateKeyPath());
        this.setAppPrivateKeyName(config.getAppPrivateKeyName());
        this.setAppPrivateKeyPath(config.getAppPrivateKeyPath());
        this.setAppId(config.getAppId());
        this.setDatafeedEventsThreadpoolSize(config.getDatafeedEventsThreadpoolSize());
        this.setDatafeedEventsErrorTimeout(config.getDatafeedEventsErrorTimeout());
        this.setAuthenticationFilterUrlPattern(config.getAuthenticationFilterUrlPattern());
    }
}
