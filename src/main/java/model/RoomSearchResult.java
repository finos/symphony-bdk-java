package model;

import java.util.List;

public class RoomSearchResult {

    private int count;
    private int skip;
    private int limit;
    private RoomSearchQuery query;
    private List<RoomInfo> rooms;

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

    public RoomSearchQuery getQuery() {
        return query;
    }

    public void setQuery(RoomSearchQuery query) {
        this.query = query;
    }

    public List<RoomInfo> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomInfo> rooms) {
        this.rooms = rooms;
    }
}
