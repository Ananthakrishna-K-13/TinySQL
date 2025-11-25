package com.tinysql.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest
{

    private Database db;

    @BeforeEach
    void setUp()
    {
        db = new Database();
    }

    @Test
    void test_get_table()
    {
        Table t = new Table("users");
        db.addTable(t);

        Table retrieved = db.getTable("users");
        
        assertNotNull(retrieved);
        assertEquals("users", retrieved.getName());
    }

    @Test
    void test_exists_true()
    {
        Table t = new Table("orders");
        db.addTable(t);

        assertTrue(db.exists("orders"));
    }

    @Test
    void test_exists_false()
    {
        assertFalse(db.exists("ghost_table"));
    }
}