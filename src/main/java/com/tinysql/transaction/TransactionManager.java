package com.tinysql.transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TransactionManager {
    private Map<String, Transaction> activeTransactions;

    public TransactionManager() {
        this.activeTransactions = new HashMap<>();
    }

    public String beginTransaction() {
        String txId = UUID.randomUUID().toString();
        Transaction tx = new Transaction(txId);
        activeTransactions.put(txId, tx);
        System.out.println("[TX] Started Transaction: " + txId);
        return txId;
    }

    public void commit(String txId) {
        if (!activeTransactions.containsKey(txId)) {
            throw new RuntimeException("Invalid Transaction ID: " + txId);
        }
        Transaction tx = activeTransactions.get(txId);
        tx.commit();
        activeTransactions.remove(txId);
        System.out.println("[TX] Committed Transaction: " + txId);
    }

    public void rollback(String txId) {
        if (!activeTransactions.containsKey(txId)) {
            throw new RuntimeException("Invalid Transaction ID: " + txId);
        }
        Transaction tx = activeTransactions.get(txId);
        tx.rollback();
        activeTransactions.remove(txId);
        System.out.println("[TX] Rolled back Transaction: " + txId);
    }
}