package authentication.jwt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtPayload {
    @JsonProperty("sub")
    private String userId;
    private JwtUser user;
    @JsonProperty("aud")
    private String applicationId;
    @JsonProperty("iss")
    private String companyName;
    @JsonProperty("exp")
    private Long expirationDateInSeconds;

    public JwtPayload() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public JwtUser getUser() {
        return user;
    }

    public void setUser(JwtUser user) {
        this.user = user;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getExpirationDateInSeconds() {
        return expirationDateInSeconds;
    }

    public void setExpirationDateInSeconds(Long expirationDateInSeconds) {
        this.expirationDateInSeconds = expirationDateInSeconds;
    }
}
