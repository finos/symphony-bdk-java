package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)

public class AdminUserInfo {
    private AdminUserAttributes userAttributes;
    private AdminUserSystemInfo userSystemInfo;
    private List<String> roles;
    private List<Long> features;
    private List<Long> apps;
    private List<Long> groups;
    private List<Long> disclaimers;
    private Avatar avatar;

    public AdminUserAttributes getUserAttributes() {
        return userAttributes;
    }

    public void setUserAttributes(AdminUserAttributes userAttributes) {
        this.userAttributes = userAttributes;
    }

    public AdminUserSystemInfo getUserSystemInfo() {
        return userSystemInfo;
    }

    public void setUserSystemInfo(AdminUserSystemInfo userSystemInfo) {
        this.userSystemInfo = userSystemInfo;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<Long> getFeatures() {
        return features;
    }

    public void setFeatures(List<Long> features) {
        this.features = features;
    }

    public List<Long> getApps() {
        return apps;
    }

    public void setApps(List<Long> apps) {
        this.apps = apps;
    }

    public List<Long> getGroups() {
        return groups;
    }

    public void setGroups(List<Long> groups) {
        this.groups = groups;
    }

    public List<Long> getDisclaimers() {
        return disclaimers;
    }

    public void setDisclaimers(List<Long> disclaimers) {
        this.disclaimers = disclaimers;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }
}
