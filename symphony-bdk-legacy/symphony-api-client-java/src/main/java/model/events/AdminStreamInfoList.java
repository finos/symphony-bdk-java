package model.events;

import java.util.List;
import model.AdminStreamFilter;
import model.AdminStreamInfo;

public class AdminStreamInfoList {

    private int count;
    private int skip;
    private int limit;
    private AdminStreamFilter filter;
    private List<AdminStreamInfo> streams;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public AdminStreamFilter getFilter() {
        return filter;
    }

    public void setFilter(AdminStreamFilter filter) {
        this.filter = filter;
    }

    public List<AdminStreamInfo> getStreams() {
        return streams;
    }

    public void setStreams(List<AdminStreamInfo> streams) {
        this.streams = streams;
    }
}
