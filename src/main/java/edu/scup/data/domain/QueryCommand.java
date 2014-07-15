package edu.scup.data.domain;

import java.util.HashMap;
import java.util.Map;

public class QueryCommand {

    private final Map<String, Object> query = new HashMap<>();

    public Map<String, Object> getQuery() {
        return query;
    }
}
