package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;



@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoList {

    private List<UserInfo> users;

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }
}
