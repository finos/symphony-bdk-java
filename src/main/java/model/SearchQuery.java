package model;

import java.util.Map;

public class SearchQuery {

    private String query;
    private Map<String,String> filters;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, String> filters) {
        this.filters = filters;
    }
}
