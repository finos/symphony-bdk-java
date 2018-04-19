package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties

public class InboundConnectionRequest {

    private Long userId;
    private String status;
    private Long firstRequestedAt;
    private Long updatedAt;
    private Integer requestCounter;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getFirstRequestedAt() {
        return firstRequestedAt;
    }

    public void setFirstRequestedAt(Long firstRequestedAt) {
        this.firstRequestedAt = firstRequestedAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getRequestCounter() {
        return requestCounter;
    }

    public void setRequestCounter(Integer requestCounter) {
        this.requestCounter = requestCounter;
    }
}
