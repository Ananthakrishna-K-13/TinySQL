package com.tinysql.transaction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransactionManagerTest
{

    private TransactionManager tm;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp()
    {
        tm = new TransactionManager();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams()
    {
        System.setOut(originalOut);
    }

    @Test
    void test_begin()
    {
        String txId = tm.beginTransaction();
        
        assertNotNull(txId);
        assertFalse(txId.isEmpty());
        assertTrue(outContent.toString().contains("Started"));
    }

    @Test
    void test_commit_valid() throws Exception
    {
        String txId = tm.beginTransaction();
        
        Transaction txObj = getTransactionFromManager(tm, txId);
        assertTrue(txObj.isActive());

        tm.commit(txId);

        assertFalse(txObj.isActive());
        assertTrue(outContent.toString().contains("Committed"));
    }

    @Test
    void test_rollback_valid() throws Exception
    {
        String txId = tm.beginTransaction();
        
        Transaction txObj = getTransactionFromManager(tm, txId);
        assertTrue(txObj.isActive());

        tm.rollback(txId);

        assertFalse(txObj.isActive());
        assertTrue(outContent.toString().contains("Rolled back"));
    }

    @Test
    void test_commit_invalid()
    {
        Exception e = assertThrows(RuntimeException.class, () ->
        {
            tm.commit("INVALID");
        });
    }

    @Test
    void test_rollback_invalid()
    {
        Exception e = assertThrows(RuntimeException.class, () ->
        {
            tm.rollback("INVALID");
        });
    }

    private Transaction getTransactionFromManager(TransactionManager tm, String txId) throws Exception
    {
        Field field = TransactionManager.class.getDeclaredField("activeTransactions");
        field.setAccessible(true);
        Map<String, Transaction> map = (Map<String, Transaction>) field.get(tm);
        return map.get(txId);
    }
}