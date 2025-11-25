package com.tinysql.engine;

import com.tinysql.model.Row;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionResultTest
{

    @Test
    void test_get_agg_result_non_zero()
    {
        double expectedValue = 55.5;
        ExecutionResult res = new ExecutionResult(true, "Agg Success", expectedValue);
        
        assertEquals(expectedValue, res.getAggregateResult(), 0.0001);
    }

    @Test
    void test_constructor_and_getters()
    {
        List<Row> rows = new ArrayList<>();
        rows.add(new Row(1));
        
        ExecutionResult res = new ExecutionResult(true, "Operation OK", rows);
        
        assertTrue(res.isSuccess());
        
        assertEquals("Operation OK", res.getMessage());
        
        assertEquals(1, res.getData().size());
        assertNull(res.getAggregateResult());
    }

    @Test
    void test_failure_constructor()
    {
        ExecutionResult res = new ExecutionResult(false, "Error occurred");
        
        assertFalse(res.isSuccess());
        assertEquals("Error occurred", res.getMessage());
        assertNull(res.getData());
    }
}