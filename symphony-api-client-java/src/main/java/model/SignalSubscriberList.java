package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SignalSubscriberList {

    private int offset;
    private int total;
    private boolean hasMore;
    private List<SignalSubscriber> data;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<SignalSubscriber> getData() {
        return data;
    }

    public void setData(List<SignalSubscriber> data) {
        this.data = data;
    }
}
