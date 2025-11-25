package com.tinysql.storage;

import com.tinysql.model.*;
import com.tinysql.util.TinySQLException;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class StorageManagerTest
{

    private StorageManager storage;
    private static final String TABLE_NAME = "test_table";

    @BeforeEach
    void setUp()
    {
        storage = new StorageManager();
        new File("data").mkdirs();
    }

    @AfterEach
    void tearDown()
    {
        File f = new File("data/" + TABLE_NAME + ".csv");
        if (f.exists()) f.delete();
    }

    @Test
    void test_save_structure_content() throws TinySQLException, IOException
    {
        Table t = new Table(TABLE_NAME);
        t.addColumn(new Column("id", DataType.INTEGER, true));
        t.addColumn(new Column("name", DataType.STRING, false));
        t.addColumn(new Column("score", DataType.DOUBLE, false));

        Row r1 = new Row(1);
        r1.set("id", 1);
        r1.set("name", "Alice");
        r1.set("score", 95.5);
        t.insert(r1);

        storage.saveTable(t);

        File file = new File("data/" + TABLE_NAME + ".csv");
        assertTrue(file.exists());

        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals(2, lines.size());
        
        assertEquals("id:INTEGER|name:STRING|score:DOUBLE", lines.get(0));
        
        assertEquals("1,1,Alice,95.5", lines.get(1));
    }

    @Test
    void test_load_edge_cases() throws TinySQLException
    {
        Table t = new Table(TABLE_NAME);
        t.addColumn(new Column("id", DataType.INTEGER, true));
        t.addColumn(new Column("data", DataType.STRING, false));
        
        Row r1 = new Row(1);
        r1.set("id", 1);
        r1.set("data", null);
        t.insert(r1);
        
        storage.saveTable(t);
        
        Table loaded = storage.loadTable(TABLE_NAME);
        assertNotNull(loaded);
        
        Row loadedRow = loaded.selectAll().get(0);
        assertNull(loadedRow.get("data"));
    }
    
    @Test
    void test_parse_all_types() throws TinySQLException
    {
        Table t = new Table(TABLE_NAME);
        t.addColumn(new Column("id", DataType.INTEGER, true));
        t.addColumn(new Column("c_int", DataType.INTEGER, false));
        t.addColumn(new Column("c_str", DataType.STRING, false));
        t.addColumn(new Column("c_bool", DataType.BOOLEAN, false));
        t.addColumn(new Column("c_float", DataType.FLOAT, false));
        t.addColumn(new Column("c_double", DataType.DOUBLE, false));

        Row r = new Row(1);
        r.set("id", 1);
        r.set("c_int", 100);
        r.set("c_str", "Text");
        r.set("c_bool", true);
        r.set("c_float", 10.5f);
        r.set("c_double", 20.99);
        t.insert(r);

        storage.saveTable(t);
        Table loaded = storage.loadTable(TABLE_NAME);
        Row lr = loaded.selectAll().get(0);

        assertEquals(100, lr.get("c_int"));
        assertEquals("Text", lr.get("c_str"));
        assertEquals(true, lr.get("c_bool"));
        assertEquals(10.5f, lr.get("c_float"));
        assertEquals(20.99, lr.get("c_double"));
    }

    @Test
    void test_load_non_existent() throws TinySQLException
    {
        Table t = storage.loadTable("GHOST_TABLE");
        assertNull(t);
    }

    @Test
    void test_partial_load() throws TinySQLException, IOException
    {
        File file = new File("data/" + TABLE_NAME + ".csv");
        String content = "id:INTEGER|name:STRING\n1"; 
        Files.write(file.toPath(), content.getBytes());

        Table loaded = storage.loadTable(TABLE_NAME);
        Row r = loaded.selectAll().get(0);
        
        assertNull(r.get("name"));
    }
}