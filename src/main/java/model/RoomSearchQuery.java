package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RoomSearchQuery {

   private String query;
   private List<String> labels;
   private Boolean active;

    @JsonProperty("private")
   private Boolean isPrivate;

   private NumericId creator;
   private NumericId owner;
   private NumericId member;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public NumericId getCreator() {
        return creator;
    }

    public void setCreator(NumericId creator) {
        this.creator = creator;
    }

    public NumericId getOwner() {
        return owner;
    }

    public void setOwner(NumericId owner) {
        this.owner = owner;
    }

    public NumericId getMember() {
        return member;
    }

    public void setMember(NumericId member) {
        this.member = member;
    }
}
