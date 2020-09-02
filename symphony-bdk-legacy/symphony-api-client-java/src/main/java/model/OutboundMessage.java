package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OutboundMessage {
    private String message;
    private String data;
    private File[] attachment;
    private List<ContentAttachment> contentAttachment;

    public OutboundMessage() {}

    public OutboundMessage(String message) {
        this.message = message;
    }

    public OutboundMessage(String message, String data) {
        this.message = message;
        this.data = data;
    }

    public OutboundMessage(String message, File... attachment) {
        this.message = message;
        this.attachment = attachment;
    }

    public OutboundMessage(String message, String data, File... attachment) {
        this.message = message;
        this.data = data;
        this.attachment = attachment;
    }

    public OutboundMessage(String message, List<ContentAttachment> contentAttachment) {
        this.message = message;
        this.contentAttachment = contentAttachment;
    }

    public OutboundMessage(String message, ContentAttachment... contentAttachment) {
        this.message = message;
        this.contentAttachment = Arrays.asList(contentAttachment);
    }

    public OutboundMessage(String message, String data, List<ContentAttachment> contentAttachment) {
        this.message = message;
        this.data = data;
        this.contentAttachment = contentAttachment;
    }

    public OutboundMessage(String message, String data, ContentAttachment... contentAttachment) {
        this.message = message;
        this.data = data;
        this.contentAttachment = Arrays.asList(contentAttachment);
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

    public File[] getAttachment() {
        return attachment;
    }

    public void setAttachment(File... attachment) {
        this.attachment = attachment;
    }

    public void addAttachment(File attachment) {
        if (this.attachment == null) {
            this.attachment = new File[] { attachment };
            return;
        }
        File[] newArray = new File[this.attachment.length + 1];
        System.arraycopy(this.attachment, 0, newArray, 0, this.attachment.length);
        newArray[newArray.length - 1] = attachment;
        this.attachment = newArray;
    }

    public List<ContentAttachment> getContentAttachment() {
        return contentAttachment;
    }

    public void setContentAttachment(List<ContentAttachment> contentAttachment) {
        this.contentAttachment = contentAttachment;
    }

    public void setContentAttachment(ContentAttachment... contentAttachment) {
        this.contentAttachment = Arrays.asList(contentAttachment);
    }

    public void addContentAttachment(ContentAttachment contentAttachment) {
        if (this.contentAttachment == null) {
            this.contentAttachment = new ArrayList<>();
        }
        this.contentAttachment.add(contentAttachment);
    }

    public boolean hasAttachment() {
        return (this.attachment != null && this.attachment.length > 0 && this.attachment[0] != null)
            || (this.contentAttachment != null && !this.contentAttachment.isEmpty()
                && this.contentAttachment.get(0) != null);
    }
}
