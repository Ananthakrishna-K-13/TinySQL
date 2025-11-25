package com.tinysql.engine;

import com.tinysql.model.*;
import com.tinysql.storage.StorageManager;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExecutorTest
{

    private Database db;
    private StorageManager storage;
    private Executor executor;

    @BeforeEach
    void setUp()
    {
        File dir = new File("data");
        if (dir.exists())
        {
            for (File f : dir.listFiles()) f.delete();
        }
        else
        {
            dir.mkdirs();
        }

        db = new Database();
        storage = new StorageManager();
        executor = new Executor(db, storage);
    }

    private List<Column> createCols()
    {
        List<Column> cols = new ArrayList<>();
        cols.add(new Column("id", DataType.INTEGER, true));
        cols.add(new Column("val", DataType.STRING, false));
        return cols;
    }

    @Test
    void test_create_exists_on_disk() throws IOException
    {
        String tableName = "disk_table";
        File f = new File("data/" + tableName + ".csv");
        String content = "id:INTEGER|val:STRING\n";
        Files.write(f.toPath(), content.getBytes());

        ExecutionResult res = executor.executeCreate(tableName, createCols());

        assertFalse(res.isSuccess());
        assertTrue(res.getMessage().contains("exists on disk"));
        
        assertNotNull(db.getTable(tableName));
    }

    @Test
    void test_insert_persistence()
    {
        String tableName = "test_insert";
        executor.executeCreate(tableName, createCols());

        List<Object> vals = new ArrayList<>();
        vals.add(1);
        vals.add("DataValue");

        ExecutionResult res = executor.executeInsert(tableName, vals);
        assertTrue(res.isSuccess());

        Table t = db.getTable(tableName);
        assertEquals(1, t.selectAll().size());
        Row r = t.selectAll().get(0);
        assertEquals("DataValue", r.get("val"));

        File f = new File("data/" + tableName + ".csv");
        try
        {
            List<String> lines = Files.readAllLines(f.toPath());
            assertTrue(lines.size() >= 2);
            assertTrue(lines.get(1).contains("DataValue"));
        }
        catch (IOException e)
        {
            fail("Could not read table file");
        }
    }

    @Test
    void test_select_branches()
    {
        String tableName = "filter_test";
        executor.executeCreate(tableName, createCols());
        
        List<Object> v1 = new ArrayList<>(); v1.add(1); v1.add("A");
        List<Object> v2 = new ArrayList<>(); v2.add(2); v2.add("B");
        executor.executeInsert(tableName, v1);
        executor.executeInsert(tableName, v2);

        ExecutionResult resAll = executor.executeSelect(tableName, null, null, null);
        assertEquals(2, resAll.getData().size());

        ExecutionResult resFilter = executor.executeSelect(tableName, "val", "=", "A");
        assertEquals(1, resFilter.getData().size());
        assertEquals("A", resFilter.getData().get(0).get("val"));
    }

    @Test
    void test_agg_failure()
    {
        ExecutionResult res = executor.executeAggregate("ghost_table", "id", "COUNT", null, null, null);
        
        assertFalse(res.isSuccess());
        assertTrue(res.getMessage().contains("not found"));
    }

    @Test
    void test_lazy_load() throws IOException
    {
        String tableName = "lazy_table";
        File f = new File("data/" + tableName + ".csv");
        String content = "id:INTEGER|val:STRING\n1,1,LazyData";
        Files.write(f.toPath(), content.getBytes());

        assertFalse(db.exists(tableName));

        ExecutionResult res = executor.executeSelect(tableName, null, null, null);
        
        assertTrue(res.isSuccess());
        assertEquals(1, res.getData().size());
        assertTrue(db.exists(tableName));
    }
    
    @Test
    void test_join_return()
    {
        executor.executeCreate("t1", createCols());
        executor.executeCreate("t2", createCols());
        
        ExecutionResult res = executor.executeJoin("t1", "t2", "id", "id");
        assertNotNull(res);
    }
}