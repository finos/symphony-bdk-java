package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)

public class SignalSubscriptionResult {

    private int requestedSubscription;
    private int successfulSubscription;
    private int failedSubscription;
    private List<Long> subscriptionErrors;

    public int getRequestedSubscription() {
        return requestedSubscription;
    }

    public void setRequestedSubscription(int requestedSubscription) {
        this.requestedSubscription = requestedSubscription;
    }

    public int getSuccessfulSubscription() {
        return successfulSubscription;
    }

    public void setSuccessfulSubscription(int successfulSubscription) {
        this.successfulSubscription = successfulSubscription;
    }

    public int getFailedSubscription() {
        return failedSubscription;
    }

    public void setFailedSubscription(int failedSubscription) {
        this.failedSubscription = failedSubscription;
    }

    public List<Long> getSubscriptionErrors() {
        return subscriptionErrors;
    }

    public void setSubscriptionErrors(List<Long> subscriptionErrors) {
        this.subscriptionErrors = subscriptionErrors;
    }
}
