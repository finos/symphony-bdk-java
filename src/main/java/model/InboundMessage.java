package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;
import utils.SymMessageParser;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InboundMessage {
    private String messageId;
    private Long timestamp;
    private String message;
    private String data;
    private List<Attachment> attachments;
    private User user;
    private Stream stream;
    private Boolean externalRecipients;
    private String diagnostic;
    private String userAgent;
    private String originalFormat;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public Boolean getExternalRecipients() {
        return externalRecipients;
    }

    public void setExternalRecipients(Boolean externalRecipients) {
        this.externalRecipients = externalRecipients;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getOriginalFormat() {
        return originalFormat;
    }

    public void setOriginalFormat(String originalFormat) {
        this.originalFormat = originalFormat;
    }

    public String getMessageText() {
        if (SymMessageParser.getInstance() == null) {
            return null;
        }
        return SymMessageParser.getInstance().messageToText(message, data);
    }

    public List<String> getHashtags() {
        return SymMessageParser.getHashtags(this);
    }

    public List<String> getCashtags() {
        return SymMessageParser.getCashtags(this);
    }

    public List<Long> getMentions() {
        return SymMessageParser.getMentions(this);
    }

    public Map<String, String> getEmojis() {
        return SymMessageParser.getEmojis(this);
    }
}
