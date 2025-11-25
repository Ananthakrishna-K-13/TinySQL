package com.tinysql.engine;

import com.tinysql.model.Row;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ConditionEvaluatorTest
{

    private Row createRow(Object val)
    {
        Row r = new Row(1);
        r.set("col", val);
        return r;
    }

    @Test
    void test_int_logic()
    {
        Row r10 = createRow(10);

        assertTrue(ConditionEvaluator.evaluate(r10, "col", "=", "10"));
        assertFalse(ConditionEvaluator.evaluate(r10, "col", "=", "11"));

        assertTrue(ConditionEvaluator.evaluate(r10, "col", "!=", "11"));
        assertFalse(ConditionEvaluator.evaluate(r10, "col", "!=", "10"));

        assertFalse(ConditionEvaluator.evaluate(r10, "col", ">", "10")); 
        assertTrue(ConditionEvaluator.evaluate(r10, "col", ">", "9"));

        assertFalse(ConditionEvaluator.evaluate(r10, "col", "<", "10"));
        assertTrue(ConditionEvaluator.evaluate(r10, "col", "<", "11"));

        assertTrue(ConditionEvaluator.evaluate(r10, "col", ">=", "10"));
        assertFalse(ConditionEvaluator.evaluate(r10, "col", ">=", "11"));

        assertTrue(ConditionEvaluator.evaluate(r10, "col", "<=", "10"));
        assertFalse(ConditionEvaluator.evaluate(r10, "col", "<=", "9"));
    }

    @Test
    void test_double_logic()
    {
        Row r = createRow(10.5);

        assertTrue(ConditionEvaluator.evaluate(r, "col", "=", "10.5"));
        assertFalse(ConditionEvaluator.evaluate(r, "col", "=", "10.6"));

        assertFalse(ConditionEvaluator.evaluate(r, "col", ">", "10.5")); 
        assertTrue(ConditionEvaluator.evaluate(r, "col", ">=", "10.5")); 
        assertFalse(ConditionEvaluator.evaluate(r, "col", "<", "10.5")); 
        assertTrue(ConditionEvaluator.evaluate(r, "col", "<=", "10.5"));
    }

    @Test
    void test_float_logic()
    {
        Row r = createRow(10.5f);

        assertTrue(ConditionEvaluator.evaluate(r, "col", "=", "10.5"));
        
        assertFalse(ConditionEvaluator.evaluate(r, "col", ">", "10.5"));
        assertTrue(ConditionEvaluator.evaluate(r, "col", ">=", "10.5"));
        assertFalse(ConditionEvaluator.evaluate(r, "col", "<", "10.5"));
        assertTrue(ConditionEvaluator.evaluate(r, "col", "<=", "10.5"));
    }

    @Test
    void test_bool_logic()
    {
        Row rTrue = createRow(true);
        Row rFalse = createRow(false);
        assertTrue(ConditionEvaluator.evaluate(rTrue, "col", "=", "true"));
        assertFalse(ConditionEvaluator.evaluate(rTrue, "col", "=", "false"));

        assertTrue(ConditionEvaluator.evaluate(rFalse, "col", "=", "false"));
        assertFalse(ConditionEvaluator.evaluate(rFalse, "col", "=", "true"));

        assertTrue(ConditionEvaluator.evaluate(rTrue, "col", "!=", "false"));
        assertFalse(ConditionEvaluator.evaluate(rTrue, "col", "!=", "true"));
    }

    @Test
    void test_str_logic()
    {
        Row r = createRow("Alice");

        assertTrue(ConditionEvaluator.evaluate(r, "col", "=", "Alice"));
        assertFalse(ConditionEvaluator.evaluate(r, "col", "=", "Bob"));
        
        assertTrue(ConditionEvaluator.evaluate(r, "col", "!=", "Bob"));
        assertFalse(ConditionEvaluator.evaluate(r, "col", "!=", "Alice"));
    }

    @Test
    void test_null_missing()
    {
        Row r = new Row(1);
        assertFalse(ConditionEvaluator.evaluate(r, "missing_col", "=", "val"));
        
        r.set("null_col", null);
        assertFalse(ConditionEvaluator.evaluate(r, "null_col", "=", "val"));
    }
    
    @Test
    void test_type_mismatch()
    {
        Row r = createRow("100");
        
        assertTrue(ConditionEvaluator.evaluate(r, "col", "=", "100"));
        assertFalse(ConditionEvaluator.evaluate(r, "col", "=", "101"));
    }
}