package com.tinysql.model;

import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TableTest
{

    private Table table;

    @BeforeEach
    void setUp()
    {
        table = new Table("test_table");
        table.addColumn(new Column("id", DataType.INTEGER, true));
        table.addColumn(new Column("data", DataType.STRING, false));
    }

    @Test
    void test_insert_updates_auto_increment()
    {
        assertEquals(1, table.getNextId());

        Row r = new Row(50);
        r.set("id", 50);
        r.set("data", "Jump");
        table.insert(r);

        assertEquals(51, table.getNextId());
    }

    @Test
    void test_insert_fills_missing_columns()
    {
        Row r = new Row(1);
        r.set("id", 1);
        
        table.insert(r);
        
        Row saved = table.selectAll().get(0);
        
        assertTrue(saved.getData().containsKey("data"));
        assertNull(saved.get("data"));
    }

    @Test
    void test_clear()
    {
        Row r = new Row(1);
        r.set("id", 1);
        table.insert(r);
        assertFalse(table.selectAll().isEmpty());

        table.clear();

        assertTrue(table.selectAll().isEmpty());
        
        assertEquals(1, table.getNextId());
    }
}