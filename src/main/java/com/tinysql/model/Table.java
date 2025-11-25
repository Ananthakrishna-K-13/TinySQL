package com.tinysql.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Table {
    private String name;
    private List<Column> columns;
    private Map<Long, Row> rows;
    private long autoIncrementId;

    public Table(String name) {
        this.name = name;
        this.columns = new ArrayList<>();
        this.rows = new ConcurrentHashMap<>();
        this.autoIncrementId = 1;
    }

    public void addColumn(Column col) {
        columns.add(col);
    }

    public void insert(Row row) {
        // Ensure all columns exist in the row data, defaulting to null if missing
        for (Column col : columns) {
            if (!row.getData().containsKey(col.getName())) {
               row.set(col.getName(), null);
            }
        }
        rows.put(row.getRowId(), row);
        
        // Update Auto Increment to prevent ID collisions on reload
        if (row.getRowId() >= autoIncrementId) {
            autoIncrementId = row.getRowId() + 1;
        }
    }

    public List<Row> selectAll() {
        return new ArrayList<>(rows.values());
    }

    public synchronized long getNextId() {
        return autoIncrementId++;
    }

    public String getName() {
        return name;
    }

    public List<Column> getColumns() {
        return columns;
    }
    
    public Column getColumn(String name) {
        for(Column c : columns) {
            if(c.getName().equalsIgnoreCase(name)) return c;
        }
        return null;
    }
    
    public void clear() {
        rows.clear();
        autoIncrementId = 1;
    }
}