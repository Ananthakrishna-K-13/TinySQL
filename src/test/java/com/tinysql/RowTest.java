package com.tinysql.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class RowTest
{

    @Test
    void test_to_string()
    {
        Row row = new Row(123);
        row.set("name", "Alice");
        
        String output = row.toString();
        
        assertFalse(output.isEmpty());
        assertTrue(output.contains("Row#123"));
        assertTrue(output.contains("name=Alice"));
    }
    
    @Test
    void test_getters()
    {
        Row row = new Row(55);
        row.set("col", "val");
        
        assertEquals(55, row.getRowId());
        assertEquals("val", row.get("col"));
        assertNotNull(row.getData());
        assertEquals(1, row.getData().size());
    }
}