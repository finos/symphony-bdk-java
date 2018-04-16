package model;

public class NumericId {

    private long id;

    public NumericId(Long userId) {
        id = userId;
    }

    public NumericId() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
