package configuration;

public class LoadBalancing {
    private LoadBalancingMethod method;
    private boolean stickySessions;

    public LoadBalancingMethod getMethod() {
        return method;
    }

    public void setMethod(LoadBalancingMethod method) {
        this.method = method;
    }

    public boolean isStickySessions() {
        return stickySessions;
    }

    public void setStickySessions(boolean stickySessions) {
        this.stickySessions = stickySessions;
    }
}
