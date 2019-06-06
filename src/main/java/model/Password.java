package model;

public class Password {

    private String hSalt;
    private String hPassword;
    private String khSalt;
    private String khPassword;

    public String gethSalt() {
        return hSalt;
    }

    public void sethSalt(String hSalt) {
        this.hSalt = hSalt;
    }

    public String gethPassword() {
        return hPassword;
    }

    public void sethPassword(String hPassword) {
        this.hPassword = hPassword;
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
