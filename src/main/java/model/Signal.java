package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Signal {

    private String id;
    private String name;
    private String query;
    private long timestamp;
    private boolean companyWide;
    private boolean visibleOnProfile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isCompanyWide() {
        return companyWide;
    }

    public void setCompanyWide(boolean companyWide) {
        this.companyWide = companyWide;
    }

    public boolean isVisibleOnProfile() {
        return visibleOnProfile;
    }

    public void setVisibleOnProfile(boolean visibleOnProfile) {
        this.visibleOnProfile = visibleOnProfile;
    }
}
