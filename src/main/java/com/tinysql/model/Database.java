package com.tinysql.model;

import java.util.HashMap;
import java.util.Map;

public class Database {
    private Map<String, Table> tables;

    public Database() {
        this.tables = new HashMap<>();
    }

    public void addTable(Table table) {
        tables.put(table.getName(), table);
    }

    public Table getTable(String name) {
        return tables.get(name);
    }

    public boolean exists(String tableName) {
        return tables.containsKey(tableName);
    }
}