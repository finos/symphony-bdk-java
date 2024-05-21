package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Password {
    private String hhSalt;
    private String hhPassword;
    private String khSalt;
    private String khPassword;

    public String gethSalt() {
        return hhSalt;
    }

    public void sethSalt(String hhSalt) {
        this.hhSalt = hhSalt;
    }

    public String gethPassword() {
        return hhPassword;
    }

    public void sethPassword(String hhPassword) {
        this.hhPassword = hhPassword;
    }

    public String getKhSalt() {
        return khSalt;
    }

    public void setKhSalt(String khSalt) {
        this.khSalt = khSalt;
    }

    public String getKhPassword() {
        return khPassword;
    }

    public void setKhPassword(String khPassword) {
        this.khPassword = khPassword;
    }
}
