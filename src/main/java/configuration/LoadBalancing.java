package configuration;

import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter @Setter
public class LoadBalancing {

    /**
     * Load balancing method
     */
    private LoadBalancingMethod method;

    /**
     * Enable sticky sessions or not
     */
    private boolean stickySessions;
}
