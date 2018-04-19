package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties
public class RoomProperties {

    private String name;
    private String description;
    private User creatorUser;
    private Long createdDate;
    private Boolean external;
    private Boolean crossPod;
    private Boolean isPublic;
    private Boolean copyProtected;
    private Boolean readOnly;
    private Boolean discoverable;
    private Boolean membersCanInvite;
    private List<Keyword> keywords;
    private Boolean canViewHistory;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreatorUser() {
        return creatorUser;
    }

    public void setCreatorUser(User creatorUser) {
        this.creatorUser = creatorUser;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getExternal() {
        return external;
    }

    public void setExternal(Boolean external) {
        this.external = external;
    }

    public Boolean getCrossPod() {
        return crossPod;
    }

    public void setCrossPod(Boolean crossPod) {
        this.crossPod = crossPod;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Boolean getCopyProtected() {
        return copyProtected;
    }

    public void setCopyProtected(Boolean copyProtected) {
        this.copyProtected = copyProtected;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Boolean getDiscoverable() {
        return discoverable;
    }

    public void setDiscoverable(Boolean discoverable) {
        this.discoverable = discoverable;
    }

    public Boolean getMembersCanInvite() {
        return membersCanInvite;
    }

    public void setMembersCanInvite(Boolean membersCanInvite) {
        this.membersCanInvite = membersCanInvite;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public Boolean getCanViewHistory() {
        return canViewHistory;
    }

    public void setCanViewHistory(Boolean canViewHistory) {
        this.canViewHistory = canViewHistory;
    }
}
