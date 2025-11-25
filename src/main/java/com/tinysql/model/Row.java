package com.tinysql.model;

import java.util.HashMap;
import java.util.Map;

public class Row {
    private Map<String, Object> data;
    private long rowId;

    public Row(long rowId) {
        this.rowId = rowId;
        this.data = new HashMap<>();
    }

    public void set(String column, Object value) {
        data.put(column, value);
    }

    public Object get(String column) {
        return data.get(column);
    }

    public Map<String, Object> getData() {
        return data;
    }
    
    public long getRowId() {
        return rowId;
    }

    @Override
    public String toString() {
        return "Row#" + rowId + " " + data.toString();
    }
}