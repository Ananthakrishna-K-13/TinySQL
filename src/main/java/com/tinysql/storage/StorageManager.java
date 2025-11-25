package com.tinysql.storage;

import com.tinysql.model.*;
import com.tinysql.util.TinySQLException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StorageManager {
    private static final String DATA_DIR = "data/";

    public StorageManager() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Saves a Table to a CSV file.
     * Format: Header on line 1, Data on subsequent lines.
     */
    public void saveTable(Table table) throws TinySQLException {
        String filename = DATA_DIR + table.getName() + ".csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // 1. Write Header: name:TYPE|name:TYPE
            StringBuilder header = new StringBuilder();
            for (int i = 0; i < table.getColumns().size(); i++) {
                Column col = table.getColumns().get(i);
                header.append(col.getName()).append(":").append(col.getType());
                if (i < table.getColumns().size() - 1) {
                    header.append("|");
                }
            }
            writer.write(header.toString());
            writer.newLine();

            // 2. Write Rows: id,val1,val2,val3...
            for (Row row : table.selectAll()) {
                StringBuilder line = new StringBuilder();
                line.append(row.getRowId()).append(","); 
                
                for (int i = 0; i < table.getColumns().size(); i++) {
                    Column col = table.getColumns().get(i);
                    Object val = row.get(col.getName());
                    if (val == null) {
                        line.append("NULL");
                    } else {
                        line.append(val.toString());
                    }
                    if (i < table.getColumns().size() - 1) {
                        line.append(",");
                    }
                }
                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new TinySQLException("Failed to save table " + table.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Loads a Table from a CSV file.
     */
    public Table loadTable(String tableName) throws TinySQLException {
        String filename = DATA_DIR + tableName + ".csv";
        File f = new File(filename);
        if (!f.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String headerLine = reader.readLine();
            if (headerLine == null) return null;

            // 1. Parse Schema
            Table table = new Table(tableName);
            String[] cols = headerLine.split("\\|");
            for (String colDef : cols) {
                if (colDef.trim().isEmpty()) continue;
                String[] parts = colDef.split(":");
                String name = parts[0];
                DataType type = DataType.valueOf(parts[1]);
                table.addColumn(new Column(name, type, name.equalsIgnoreCase("id")));
            }

            // 2. Parse Rows
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split(",");
                
                if (values.length < 1) continue;

                // First value is always ID
                long id = Long.parseLong(values[0]);
                Row row = new Row(id);
                
                List<Column> columns = table.getColumns();
                // Data values start at index 1 in CSV
                for (int i = 0; i < columns.size(); i++) {
                    if (i + 1 >= values.length) break;
                    String valStr = values[i + 1];
                    Column col = columns.get(i);
                    Object val = parseValue(valStr, col.getType());
                    row.set(col.getName(), val);
                }
                table.insert(row);
            }
            return table;
        } catch (IOException | IllegalArgumentException e) {
            throw new TinySQLException("Failed to load table " + tableName + ": " + e.getMessage());
        }
    }

    private Object parseValue(String val, DataType type) {
        if (val.equals("NULL")) return null;
        try {
            switch (type) {
                case INTEGER: return Integer.parseInt(val);
                case STRING: return val;
                case BOOLEAN: return Boolean.parseBoolean(val);
                case FLOAT: return Float.parseFloat(val);
                case DOUBLE: return Double.parseDouble(val);
                default: return val;
            }
        } catch (Exception e) {
            return null; 
        }
    }
}