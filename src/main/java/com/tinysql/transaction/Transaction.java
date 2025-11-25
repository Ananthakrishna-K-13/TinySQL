package com.tinysql.transaction;

public class Transaction {
    private String id;
    private long startTime;
    private boolean isActive;

    public Transaction(String id) {
        this.id = id;
        this.startTime = System.currentTimeMillis();
        this.isActive = true;
    }

    public String getId() { return id; }
    public boolean isActive() { return isActive; }
    
    public void commit() { this.isActive = false; }
    public void rollback() { this.isActive = false; }
}