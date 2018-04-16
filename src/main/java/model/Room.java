package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Room {

    private String name;
    private String description;
    private Boolean membersCanInvite;
    private Boolean discoverable;
    private Boolean isPublic;
    private Boolean readOnly;
    private Boolean copyProtected;
    private Boolean crossPod;
    private Boolean viewHistory;
    private Boolean multiLateralRoom;
    private List<Keyword> keywords;



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

    public Boolean getMembersCanInvite() {
        return membersCanInvite;
    }

    public void setMembersCanInvite(Boolean membersCanInvite) {
        this.membersCanInvite = membersCanInvite;
    }

    public Boolean getDiscoverable() {
        return discoverable;
    }

    public void setDiscoverable(Boolean discoverable) {
        this.discoverable = discoverable;
    }

    @JsonProperty("public")
    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Boolean getCopyProtected() {
        return copyProtected;
    }

    public void setCopyProtected(Boolean copyProtected) {
        this.copyProtected = copyProtected;
    }

    public Boolean getCrossPod() {
        return crossPod;
    }

    public void setCrossPod(Boolean crossPod) {
        this.crossPod = crossPod;
    }

    public Boolean getViewHistory() {
        return viewHistory;
    }

    public void setViewHistory(Boolean viewHistory) {
        this.viewHistory = viewHistory;
    }

    public Boolean getMultiLateralRoom() {
        return multiLateralRoom;
    }

    public void setMultiLateralRoom(Boolean multiLateralRoom) {
        this.multiLateralRoom = multiLateralRoom;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }
}
