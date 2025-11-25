package com.tinysql;

import com.tinysql.engine.Executor;
import com.tinysql.engine.ExecutionResult;
import com.tinysql.model.Column;
import com.tinysql.model.DataType;
import com.tinysql.model.Database;
import com.tinysql.storage.StorageManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   TinySQL Pro Database Engine v3.0       ");
        System.out.println("==========================================");

        Database db = new Database();
        StorageManager storage = new StorageManager();
        Executor executor = new Executor(db, storage);
        
        System.out.println("Ready. Commands: SELECT, INSERT, CREATE, JOIN, EXIT");
        System.out.println("Supports: INT, DOUBLE, FLOAT, TEXT, BOOL");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            while (true) {
                System.out.print("TinySQL> ");
                String query = reader.readLine();
                if (query == null || query.equalsIgnoreCase("EXIT")) break;
                if (query.trim().isEmpty()) continue;

                processQuery(executor, query);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processQuery(Executor executor, String query) {
        String[] parts = query.trim().split("\\s+");
        String cmd = parts[0].toUpperCase();

        try {
            if (cmd.equals("SELECT")) {
                handleSelect(executor, parts);
            } else if (cmd.equals("CREATE")) {
                handleCreate(executor, parts);
            } else if (cmd.equals("INSERT")) {
                handleInsert(executor, parts);
            } else if (cmd.equals("JOIN")) {
                handleJoin(executor, parts);
            } 
            else {
                System.out.println("Unknown command: " + cmd);
            }
        } catch (Exception e) {
            System.err.println("Execution Error: " + e.getMessage());
        }
    }
    
    // Syntax: JOIN users orders ON id user_id
    private static void handleJoin(Executor executor, String[] parts) {
        if (parts.length < 6 || !parts[3].equalsIgnoreCase("ON")) {
            System.out.println("Syntax: JOIN <t1> <t2> ON <col1> <col2>");
            return;
        }
        ExecutionResult res = executor.executeJoin(parts[1], parts[2], parts[4], parts[5]);
        printResult(res);
    }
    
    // Syntax: CREATE TABLE users (id INT, name TEXT, score FLOAT)
    // Updated handleCreate with strict syntax checking
    private static void handleCreate(Executor executor, String[] parts) {
        if (parts.length < 4) { 
            System.out.println("Syntax Error: Expected CREATE TABLE <name> (<cols>)"); 
            return; 
        }
        
        String tableName = parts[2];
        
        // Reconstruct the column definition string
        StringBuilder rawCols = new StringBuilder();
        for(int i=3; i<parts.length; i++) {
            rawCols.append(parts[i]).append(" ");
        }
        
        String colString = rawCols.toString().trim();
        
        // Strict Check: Must start with ( and end with )
        if (!colString.startsWith("(") || !colString.endsWith(")")) {
             System.out.println("Syntax Error: Missing parenthesis '(' or ')'");
             return;
        }

        // Remove outer parenthesis
        String inner = colString.substring(1, colString.length() - 1);
        
        List<Column> cols = new ArrayList<>();
        String[] colDefs = inner.split(",");
        
        for(String def : colDefs) {
            def = def.trim();
            if (def.isEmpty()) continue;
            
            String[] cp = def.split("\\s+");
            
            // FIX: If a column definition doesn't have at least Name + Type, fail immediately
            if (cp.length < 2) {
                System.out.println("Syntax Error: Invalid column definition '" + def + "'. Expected <name> <type>");
                return;
            }
            
            String name = cp[0];
            String typeStr = cp[1].toUpperCase();
            DataType type;
            
            if(typeStr.startsWith("INT")) type = DataType.INTEGER;
            else if(typeStr.startsWith("TEXT")) type = DataType.STRING;
            else if(typeStr.startsWith("FLOAT")) type = DataType.FLOAT;
            else if(typeStr.startsWith("DOUBLE")) type = DataType.DOUBLE;
            else if(typeStr.startsWith("BOOL")) type = DataType.BOOLEAN;
            else {
                 System.out.println("Syntax Error: Unknown type '" + typeStr + "' for column " + name);
                 return;
            }
            
            cols.add(new Column(name, type, false));
        }
        
        if (cols.isEmpty()) {
            System.out.println("Syntax Error: No columns defined");
            return;
        }
        
        ExecutionResult res = executor.executeCreate(tableName, cols);
        printResult(res);
    }

    // Syntax: INSERT INTO users VALUES 1 Alice 50.5
    private static void handleInsert(Executor executor, String[] parts) {
        int valIdx = -1;
        for(int i=0; i<parts.length; i++) if(parts[i].equalsIgnoreCase("VALUES")) valIdx = i;
        if (valIdx == -1) { System.out.println("Syntax Error: Missing VALUES clause"); return; }
        
        String tableName = parts[2];
        List<Object> values = new ArrayList<>();
        for(int i=valIdx+1; i<parts.length; i++) {
            String raw = parts[i];
            if(raw.matches("-?\\d+")) values.add(Integer.parseInt(raw));
            else if(raw.matches("-?\\d*\\.\\d+")) values.add(Double.parseDouble(raw));
            else if(raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("false")) values.add(Boolean.parseBoolean(raw));
            else values.add(raw);
        }
        
        ExecutionResult res = executor.executeInsert(tableName, values);
        printResult(res);
    }
    
    // Syntax: SELECT * FROM users WHERE age > 10
    private static void handleSelect(Executor executor, String[] parts) {
        String selector = parts[1];
        boolean isAgg = selector.contains("(") && selector.contains(")");
        
        int fromIdx = -1;
        for(int i=0; i<parts.length; i++) if(parts[i].equalsIgnoreCase("FROM")) fromIdx = i;
        if (fromIdx == -1) { System.out.println("Syntax Error: Missing FROM"); return; }
        
        String tableName = parts[fromIdx + 1];
        
        String whereCol = null, op = null, val = null;
        int whereIdx = -1;
        for(int i=0; i<parts.length; i++) if(parts[i].equalsIgnoreCase("WHERE")) whereIdx = i;
        
        if (whereIdx != -1 && parts.length > whereIdx + 3) {
            whereCol = parts[whereIdx + 1];
            op = parts[whereIdx + 2];
            val = parts[whereIdx + 3];
        }
        
        if (isAgg) {
            String func = selector.substring(0, selector.indexOf('('));
            String col = selector.substring(selector.indexOf('(')+1, selector.indexOf(')'));
            ExecutionResult res = executor.executeAggregate(tableName, col, func, whereCol, op, val);
            if(res.isSuccess()) System.out.println("AGGREGATE RESULT: " + res.getAggregateResult());
            else System.out.println("ERROR: " + res.getMessage());
        } else {
            ExecutionResult res = executor.executeSelect(tableName, whereCol, op, val);
            printResult(res);
        }
    }

    private static void printResult(ExecutionResult res) {
        if (res.isSuccess()) {
            System.out.println("[OK] " + res.getMessage());
            if (res.getData() != null) {
                for (com.tinysql.model.Row r : res.getData()) {
                    System.out.println("   " + r.toString());
                }
            }
        } else {
            System.out.println("[ERROR] " + res.getMessage());
        }
    }
}