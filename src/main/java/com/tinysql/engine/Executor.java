package com.tinysql.engine;

import com.tinysql.model.*;
import com.tinysql.storage.StorageManager;
import com.tinysql.util.TinySQLException;
import java.util.ArrayList;
import java.util.List;

public class Executor {
    private Database db;
    private StorageManager storage;

    public Executor(Database db, StorageManager storage) {
        this.db = db;
        this.storage = storage;
    }

    public ExecutionResult executeCreate(String tableName, List<Column> columns) {
        // 1. Check Memory
        if (db.exists(tableName)) {
            return new ExecutionResult(false, "Table already exists: " + tableName);
        }

        // 2. FIX: Check Disk! 
        // If the file exists on disk, load it into memory and STOP creation.
        try {
            Table existing = storage.loadTable(tableName);
            if (existing != null) {
                db.addTable(existing);
                return new ExecutionResult(false, "Table already exists on disk: " + tableName);
            }
        } catch (TinySQLException e) {
            // If loading fails drastically, return error to be safe
            return new ExecutionResult(false, "Error checking disk for table: " + e.getMessage());
        }

        // 3. If not in memory AND not on disk, create new
        Table t = new Table(tableName);
        for (Column c : columns) t.addColumn(c);
        db.addTable(t);
        
        try { 
            storage.saveTable(t); 
        } catch (TinySQLException e) { 
            return new ExecutionResult(false, e.getMessage()); 
        }
        
        return new ExecutionResult(true, "Created " + tableName);
    }

    public ExecutionResult executeInsert(String tableName, List<Object> values) {
        Table t = getOrLoadTable(tableName);
        if (t == null) return new ExecutionResult(false, "Table not found: " + tableName);

        Row row = new Row(t.getNextId());
        List<Column> cols = t.getColumns();
        
        if (values.size() != cols.size()) return new ExecutionResult(false, "Column count mismatch");

        for (int i = 0; i < cols.size(); i++) {
            row.set(cols.get(i).getName(), values.get(i));
        }
        
        t.insert(row);
        try { storage.saveTable(t); } 
        catch (TinySQLException e) { return new ExecutionResult(true, "Inserted but save failed: " + e.getMessage()); }

        return new ExecutionResult(true, "1 row inserted");
    }

    public ExecutionResult executeSelect(String tableName, String whereCol, String operator, String whereVal) {
        Table t = getOrLoadTable(tableName);
        if (t == null) return new ExecutionResult(false, "Table not found");

        List<Row> results = new ArrayList<>();
        for (Row r : t.selectAll()) {
            if (whereCol == null) {
                results.add(r);
            } else {
                if (ConditionEvaluator.evaluate(r, whereCol, operator, whereVal)) {
                    results.add(r);
                }
            }
        }
        
        return new ExecutionResult(true, results.size() + " rows found.", results);
    }
    
    public ExecutionResult executeAggregate(String tableName, String colName, String function, String whereCol, String op, String val) {
        ExecutionResult filterRes = executeSelect(tableName, whereCol, op, val);
        if (!filterRes.isSuccess()) return filterRes;
        double result = Aggregator.calculate(filterRes.getData(), colName, function);
        return new ExecutionResult(true, function + " result", result);
    }
    
    public ExecutionResult executeJoin(String table1, String table2, String col1, String col2) {
        Table t1 = getOrLoadTable(table1);
        Table t2 = getOrLoadTable(table2);
        return JoinProcessor.executeJoin(t1, t2, col1, col2);
    }
    
    // Helper to lazy load tables from disk if they aren't in memory
    private Table getOrLoadTable(String name) {
        Table t = db.getTable(name);
        if (t == null) {
            try {
                t = storage.loadTable(name);
                if (t != null) db.addTable(t);
            } catch (TinySQLException e) { return null; }
        }
        return t;
    }
}