package model;

import java.util.List;

public class AdminNewUser {

    private AdminUserAttributes userAttributes;
    private List<String> roles;
    private Password password;

    public AdminUserAttributes getUserAttributes() {
        return userAttributes;
    }

    public void setUserAttributes(AdminUserAttributes userAttributes) {
        this.userAttributes = userAttributes;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }
}
