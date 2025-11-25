package com.tinysql.transaction;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTest
{

    @Test
    void test_getter() 
    {
        String id = "TX-12345";
        Transaction tx = new Transaction(id);
        assertEquals(id, tx.getId());
    }

    @Test
    void test_init() 
    {
        Transaction tx = new Transaction("TX-INIT");
        assertTrue(tx.isActive());
    }

    @Test
    void test_commit()
    {
        Transaction tx = new Transaction("TX-COMMIT");
        tx.commit();
        assertFalse(tx.isActive());
    }
    
    @Test
    void test_rollback() 
    {
        Transaction tx = new Transaction("TX-ROLLBACK");
        tx.rollback();
        assertFalse(tx.isActive());
    }
}