package com.tinysql.engine;

import com.tinysql.model.*;
import java.util.ArrayList;
import java.util.List;

public class JoinProcessor {

    public static ExecutionResult executeJoin(Table t1, Table t2, String col1, String col2) {
        if (t1 == null || t2 == null) {
            return new ExecutionResult(false, "One or more tables not found for JOIN.");
        }

        // Verify Columns Exist
        if (t1.getColumn(col1) == null) return new ExecutionResult(false, "Column " + col1 + " missing in " + t1.getName());
        if (t2.getColumn(col2) == null) return new ExecutionResult(false, "Column " + col2 + " missing in " + t2.getName());

        List<Row> resultRows = new ArrayList<>();
        long resultIdCounter = 1;

        // Nested Loop Join
        List<Row> rows1 = t1.selectAll();
        List<Row> rows2 = t2.selectAll();

        for (Row r1 : rows1) {
            Object val1 = r1.get(col1);
            if (val1 == null) continue;

            for (Row r2 : rows2) {
                Object val2 = r2.get(col2);
                if (val2 == null) continue;

                if (val1.toString().equals(val2.toString())) {
                    // Match found - Merge Rows
                    Row newRow = new Row(resultIdCounter++);
                    
                    // Add data from T1 (prefixed)
                    for (Column c : t1.getColumns()) {
                        newRow.set(t1.getName() + "." + c.getName(), r1.get(c.getName()));
                    }
                    
                    // Add data from T2 (prefixed)
                    for (Column c : t2.getColumns()) {
                        newRow.set(t2.getName() + "." + c.getName(), r2.get(c.getName()));
                    }
                    
                    resultRows.add(newRow);
                }
            }
        }

        return new ExecutionResult(true, "Joined " + resultRows.size() + " rows.", resultRows);
    }
}