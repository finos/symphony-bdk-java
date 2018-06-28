package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class AdminStreamInfo {

    private String id;
    private boolean isExternal;
    private boolean isActive;
    private boolean isPublic;
    private String type;
    private boolean crossPod;
    private String origin;
    private AdminStreamAttributes attributes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public void setExternal(boolean external) {
        isExternal = external;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isCrossPod() {
        return crossPod;
    }

    public void setCrossPod(boolean crossPod) {
        this.crossPod = crossPod;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public AdminStreamAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(AdminStreamAttributes attributes) {
        this.attributes = attributes;
    }
}
