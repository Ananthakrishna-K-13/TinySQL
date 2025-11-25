package com.tinysql.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ColumnTest
{

    @Test
    void test_primary_key_status()
    {
        Column pkCol = new Column("id", DataType.INTEGER, true);
        assertTrue(pkCol.isPrimaryKey());
        
        Column nonPkCol = new Column("name", DataType.STRING, false);
        assertFalse(nonPkCol.isPrimaryKey());
    }

    @Test
    void test_to_string()
    {
        Column col = new Column("score", DataType.DOUBLE, false);
        String output = col.toString();
    
        assertFalse(output.isEmpty());
        assertEquals("score(DOUBLE)", output);
    }

    @Test
    void test_getters()
    {
        Column col = new Column("age", DataType.INTEGER, false);
        assertEquals("age", col.getName());
        assertEquals(DataType.INTEGER, col.getType());
    }
}