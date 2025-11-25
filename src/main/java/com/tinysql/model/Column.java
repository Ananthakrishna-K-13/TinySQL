package com.tinysql.model;

public class Column {
    private String name;
    private DataType type;
    private boolean isPrimaryKey;

    public Column(String name, DataType type, boolean isPrimaryKey) {
        this.name = name;
        this.type = type;
        this.isPrimaryKey = isPrimaryKey;
    }

    public String getName() {
        return name;
    }

    public DataType getType() {
        return type;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }
    
    @Override
    public String toString() {
        return name + "(" + type + ")";
    }
}