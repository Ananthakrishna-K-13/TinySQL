package com.tinysql;

import com.tinysql.engine.Executor;
import com.tinysql.engine.ExecutionResult;
import com.tinysql.model.*;
import com.tinysql.storage.StorageManager;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTests
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

    private List<Object> row(Object... values)
    {
        List<Object> list = new ArrayList<>();
        Collections.addAll(list, values);
        return list;
    }

    private void createUsersTable()
    {
        List<Column> cols = new ArrayList<>();
        cols.add(new Column("id", DataType.INTEGER, true));
        cols.add(new Column("name", DataType.STRING, false));
        cols.add(new Column("age", DataType.INTEGER, false));
        cols.add(new Column("salary", DataType.DOUBLE, false));
        cols.add(new Column("active", DataType.BOOLEAN, false));
        executor.executeCreate("users", cols);
    }

    @Test
    void test_workflow()
    {
        createUsersTable();
        executor.executeInsert("users", row(1, "Alice", 30, 50000.0, true));
        
        ExecutionResult res = executor.executeSelect("users", null, null, null);
        assertEquals(1, res.getData().size());
        assertEquals("Alice", res.getData().get(0).get("name"));
    }

    @Test
    void test_persistence()
    {
        createUsersTable();
        executor.executeInsert("users", row(1, "Bob", 40, 100.50, true));

        db = new Database();
        storage = new StorageManager();
        executor = new Executor(db, storage);

        ExecutionResult res = executor.executeSelect("users", null, null, null);
        assertTrue(res.isSuccess());
        assertEquals(1, res.getData().size());
        assertEquals("Bob", res.getData().get(0).get("name"));
    }

    @Test
    void test_insert_multiple()
    {
        createUsersTable();
        executor.executeInsert("users", row(1, "A", 10, 1.0, true));
        executor.executeInsert("users", row(2, "B", 20, 2.0, false));
        executor.executeInsert("users", row(3, "C", 30, 3.0, true));

        ExecutionResult res = executor.executeSelect("users", null, null, null);
        assertEquals(3, res.getData().size());
    }

    @Test
    void test_duplicate_table()
    {
        createUsersTable();
        ExecutionResult res = executor.executeCreate("users", new ArrayList<>());
        assertFalse(res.isSuccess());
        assertTrue(res.getMessage().contains("exists"));
    }

    @Test
    void test_where_equal()
    {
        createUsersTable();
        executor.executeInsert("users", row(1, "Alice", 25, 100.0, true));
        executor.executeInsert("users", row(2, "Bob", 30, 200.0, false));

        ExecutionResult res = executor.executeSelect("users", "name", "=", "Alice");
        assertEquals(1, res.getData().size());
        assertEquals(25, res.getData().get(0).get("age"));
    }

    @Test
    void test_where_gt()
    {
        createUsersTable();
        executor.executeInsert("users", row(1, "A", 10, 10.0, true));
        executor.executeInsert("users", row(2, "B", 20, 20.0, true));

        ExecutionResult res = executor.executeSelect("users", "age", ">", "15");
        assertEquals(1, res.getData().size());
        assertEquals("B", res.getData().get(0).get("name"));
    }

    @Test
    void test_where_lt()
    {
        createUsersTable();
        executor.executeInsert("users", row(1, "A", 10, 10.0, true));
        executor.executeInsert("users", row(2, "B", 20, 20.0, true));

        ExecutionResult res = executor.executeSelect("users", "salary", "<", "15.0");
        assertEquals(1, res.getData().size());
        assertEquals("A", res.getData().get(0).get("name"));
    }

    @Test
    void test_where_gte()
    {
        createUsersTable();
        executor.executeInsert("users", row(1, "A", 10, 10.0, true));
        executor.executeInsert("users", row(2, "B", 20, 20.0, true));

        ExecutionResult res = executor.executeSelect("users", "age", ">=", "20");
        assertEquals(1, res.getData().size());
        assertEquals(20, res.getData().get(0).get("age"));
    }

    @Test
    void test_where_lte()
    {
        createUsersTable();
        executor.executeInsert("users", row(1, "A", 10, 10.0, true));
        executor.executeInsert("users", row(2, "B", 20, 20.0, true));

        ExecutionResult res = executor.executeSelect("users", "age", "<=", "10");
        assertEquals(1, res.getData().size());
        assertEquals(10, res.getData().get(0).get("age"));
    }

    @Test
    void test_where_neq()
    {
        createUsersTable();
        executor.executeInsert("users", row(1, "A", 10, 10.0, true));
        executor.executeInsert("users", row(2, "B", 20, 20.0, true));

        ExecutionResult res = executor.executeSelect("users", "name", "!=", "A");
        assertEquals(1, res.getData().size());
        assertEquals("B", res.getData().get(0).get("name"));
    }

    @Test
    void test_bool_filter()
    {
        createUsersTable();
        executor.executeInsert("users", row(1, "Active", 10, 10.0, true));
        executor.executeInsert("users", row(2, "Inactive", 20, 20.0, false));

        ExecutionResult res = executor.executeSelect("users", "active", "=", "true");
        assertEquals(1, res.getData().size());
        assertEquals("Active", res.getData().get(0).get("name"));
    }

    @Test
    void test_str_filter()
    {
        createUsersTable();
        executor.executeInsert("users", row(1, "Zack", 10, 10.0, true));
        executor.executeInsert("users", row(2, "Adam", 20, 20.0, true));

        ExecutionResult res = executor.executeSelect("users", "name", "=", "Adam");
        assertEquals(1, res.getData().size());
    }

    @Test
    void test_insert_mismatch()
    {
        createUsersTable();
        ExecutionResult res = executor.executeInsert("users", row(1, "Alice"));
        assertFalse(res.isSuccess());
        assertTrue(res.getMessage().contains("mismatch"));
    }

    @Test
    void test_select_missing()
    {
        ExecutionResult res = executor.executeSelect("ghost_table", null, null, null);
        assertFalse(res.isSuccess());
        assertTrue(res.getMessage().contains("not found"));
    }

    @Test
    void test_agg_count()
    {
        createUsersTable();
        executor.executeInsert("users", row(1, "A", 10, 10.0, true));
        executor.executeInsert("users", row(2, "B", 20, 20.0, true));

        ExecutionResult res = executor.executeAggregate("users", "id", "COUNT", null, null, null);
        assertEquals(2.0, res.getAggregateResult(), 0.001);
    }

    @Test
    void test_agg_sum()
    {
        createUsersTable();
        executor.executeInsert("users", row(1, "A", 10, 10.5, true));
        executor.executeInsert("users", row(2, "B", 20, 20.5, true));

        ExecutionResult res = executor.executeAggregate("users", "salary", "SUM", null, null, null);
        assertEquals(31.0, res.getAggregateResult(), 0.001);
    }

    @Test
    void test_agg_avg()
    {
        createUsersTable();
        executor.executeInsert("users", row(1, "A", 10, 10.0, true));
        executor.executeInsert("users", row(2, "B", 20, 20.0, true));

        ExecutionResult res = executor.executeAggregate("users", "salary", "AVG", null, null, null);
        assertEquals(15.0, res.getAggregateResult(), 0.001);
    }

    @Test
    void test_agg_max()
    {
        createUsersTable();
        executor.executeInsert("users", row(1, "A", 10, 10.0, true));
        executor.executeInsert("users", row(2, "B", 50, 50.0, true));
        executor.executeInsert("users", row(3, "C", 20, 20.0, true));

        ExecutionResult res = executor.executeAggregate("users", "age", "MAX", null, null, null);
        assertEquals(50.0, res.getAggregateResult(), 0.001);
    }

    @Test
    void test_agg_min()
    {
        createUsersTable();
        executor.executeInsert("users", row(1, "A", 10, 10.0, true));
        executor.executeInsert("users", row(2, "B", 5, 5.0, true));
        executor.executeInsert("users", row(3, "C", 20, 20.0, true));

        ExecutionResult res = executor.executeAggregate("users", "salary", "MIN", null, null, null);
        assertEquals(5.0, res.getAggregateResult(), 0.001);
    }

    @Test
    void test_join()
    {
        createUsersTable();
        executor.executeInsert("users", row(1, "Alice", 30, 5000.0, true));
        executor.executeInsert("users", row(2, "Bob", 40, 6000.0, true));

        List<Column> orderCols = new ArrayList<>();
        orderCols.add(new Column("id", DataType.INTEGER, true));
        orderCols.add(new Column("user_id", DataType.INTEGER, false));
        orderCols.add(new Column("total", DataType.DOUBLE, false));
        executor.executeCreate("orders", orderCols);

        executor.executeInsert("orders", row(100, 1, 50.0));
        executor.executeInsert("orders", row(101, 99, 20.0));

        ExecutionResult res = executor.executeJoin("users", "orders", "id", "user_id");
        
        assertTrue(res.isSuccess());
        assertEquals(1, res.getData().size());
        
        Row resultRow = res.getData().get(0);
        assertEquals("Alice", resultRow.get("users.name"));
        assertEquals(50.0, resultRow.get("orders.total"));
    }
}