package model;

import java.util.List;

public class AdminStreamFilter {

    private List<String> streamTypes;
    private String scope;
    private String origin;
    private String status;
    private String privacy;
    private long startDate;
    private long endDate;

    public List<String> getStreamTypes() {
        return streamTypes;
    }

    public void setStreamTypes(List<String> streamTypes) {
        this.streamTypes = streamTypes;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
}
