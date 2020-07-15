package model;

public class Status {
    private String status;

    public Status() {}

    public Status(String status) {
        this.status = status;
    }

    public Status(UserStatus status) {
        this.status = status.toString();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatus(UserStatus status) {
        this.status = status.toString();
    }
}
