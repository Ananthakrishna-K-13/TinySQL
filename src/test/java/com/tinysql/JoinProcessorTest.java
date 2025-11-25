package com.tinysql.engine;

import com.tinysql.model.*;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JoinProcessorTest
{

    private Table t1;
    private Table t2;

    @BeforeEach
    void setUp()
    {
        t1 = new Table("users");
        t1.addColumn(new Column("id", DataType.INTEGER, true));
        t1.addColumn(new Column("name", DataType.STRING, false));

        t2 = new Table("orders");
        t2.addColumn(new Column("id", DataType.INTEGER, true));
        t2.addColumn(new Column("user_id", DataType.INTEGER, false));
        t2.addColumn(new Column("total", DataType.DOUBLE, false));
    }

    private Row createRow(long id, Object... vals)
    {
        return new Row(id);
    }

    @Test
    void test_join_input_validation()
    {
        ExecutionResult res1 = JoinProcessor.executeJoin(null, t2, "id", "user_id");
        assertNotNull(res1);
        assertFalse(res1.isSuccess());
        assertTrue(res1.getMessage().contains("tables not found"));

        ExecutionResult res2 = JoinProcessor.executeJoin(t1, null, "id", "user_id");
        assertNotNull(res2);
        assertFalse(res2.isSuccess());
    }

    @Test
    void test_missing_columns()
    {
        ExecutionResult res1 = JoinProcessor.executeJoin(t1, t2, "missing_col", "user_id");
        assertNotNull(res1);
        assertFalse(res1.isSuccess());
        assertTrue(res1.getMessage().contains("missing_col missing"));

        ExecutionResult res2 = JoinProcessor.executeJoin(t1, t2, "id", "missing_col");
        assertNotNull(res2);
        assertFalse(res2.isSuccess());
        assertTrue(res2.getMessage().contains("missing_col missing"));
    }

    @Test
    void test_inner_join_logic()
    {
        Row u1 = new Row(1); u1.set("id", 1); u1.set("name", "Alice");
        Row u2 = new Row(2); u2.set("id", 2); u2.set("name", "Bob");
        Row u3 = new Row(3); u3.set("id", 3); u3.set("name", "Charlie"); 
        t1.insert(u1);
        t1.insert(u2);
        t1.insert(u3);

        Row o1 = new Row(100); o1.set("id", 100); o1.set("user_id", 1); o1.set("total", 50.0);
        Row o2 = new Row(101); o2.set("id", 101); o2.set("user_id", 1); o2.set("total", 25.0);
        Row o3 = new Row(102); o3.set("id", 102); o3.set("user_id", 2); o3.set("total", 10.0);
        Row o4 = new Row(103); o4.set("id", 103); o4.set("user_id", 99); o4.set("total", 0.0);
        Row oNull = new Row(104); oNull.set("id", 104); oNull.set("user_id", null);
        
        t2.insert(o1);
        t2.insert(o2);
        t2.insert(o3);
        t2.insert(o4);
        t2.insert(oNull);

        ExecutionResult res = JoinProcessor.executeJoin(t1, t2, "id", "user_id");

        assertTrue(res.isSuccess());
        assertNotNull(res.getData());
        
        List<Row> rows = res.getData();
        
        assertEquals(3, rows.size());

        boolean foundAliceBigOrder = false;
        for(Row r : rows)
        {
            Object name = r.get("users.name");
            Object total = r.get("orders.total");
            if ("Alice".equals(name) && Double.valueOf(50.0).equals(total))
            {
                foundAliceBigOrder = true;
                break;
            }
        }
        assertTrue(foundAliceBigOrder);

        long id1 = rows.get(0).getRowId();
        long id2 = rows.get(1).getRowId();
        assertNotEquals(id1, id2);
    }

    @Test
    void test_join_with_no_matches()
    {
        Row u1 = new Row(1); u1.set("id", 1);
        t1.insert(u1);
        
        Row o1 = new Row(100); o1.set("user_id", 99);
        t2.insert(o1);

        ExecutionResult res = JoinProcessor.executeJoin(t1, t2, "id", "user_id");
        assertTrue(res.isSuccess());
        assertEquals(0, res.getData().size());
    }
}