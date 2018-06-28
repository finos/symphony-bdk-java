package model;

public class SuppressionResult {

    private String messageId;
    private boolean suppressed;
    private long suppressionDate;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public boolean isSuppressed() {
        return suppressed;
    }

    public void setSuppressed(boolean suppressed) {
        this.suppressed = suppressed;
    }

    public long getSuppressionDate() {
        return suppressionDate;
    }

    public void setSuppressionDate(long suppressionDate) {
        this.suppressionDate = suppressionDate;
    }
}
