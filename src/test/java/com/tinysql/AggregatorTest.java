package com.tinysql.engine;

import com.tinysql.model.Row;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AggregatorTest
{

    private Row createRow(long id, Object val)
    {
        Row r = new Row(id);
        r.set("val", val);
        return r;
    }

    @Test
    void test_empty_returns_zero()
    {
        List<Row> rows = Collections.emptyList();
        assertEquals(0.0, Aggregator.calculate(rows, "val", "SUM"), 0.0001);
        assertEquals(0.0, Aggregator.calculate(rows, "val", "COUNT"), 0.0001);
    }

    @Test
    void test_count()
    {
        List<Row> rows = new ArrayList<>();
        rows.add(createRow(1, 10));
        rows.add(createRow(2, 20));
        rows.add(createRow(3, 30));

        double result = Aggregator.calculate(rows, "val", "COUNT");
        
        assertEquals(3.0, result, 0.0001);
    }

    @Test
    void test_sum()
    {
        List<Row> rows = new ArrayList<>();
        rows.add(createRow(1, 10.0));
        rows.add(createRow(2, 20.0));
        rows.add(createRow(3, 5.5));

        double result = Aggregator.calculate(rows, "val", "SUM");
        
        assertEquals(35.5, result, 0.0001);
    }

    @Test
    void test_avg()
    {
        List<Row> rows = new ArrayList<>();
        rows.add(createRow(1, 10));
        rows.add(createRow(2, 20));

        double result = Aggregator.calculate(rows, "val", "AVG");
        assertEquals(15.0, result, 0.0001);
    }

    @Test
    void test_max()
    {
        List<Row> rows = new ArrayList<>();
        rows.add(createRow(1, -50.0));
        rows.add(createRow(2, -10.0));
        rows.add(createRow(3, -100.0));

        double result = Aggregator.calculate(rows, "val", "MAX");
        assertEquals(-10.0, result, 0.0001);
    }

    @Test
    void test_min()
    {
        List<Row> rows = new ArrayList<>();
        rows.add(createRow(1, 50.0));
        rows.add(createRow(2, 10.0));
        rows.add(createRow(3, 100.0));

        double result = Aggregator.calculate(rows, "val", "MIN");
        assertEquals(10.0, result, 0.0001);
    }

    @Test
    void test_ignore_non_numbers()
    {
        List<Row> rows = new ArrayList<>();
        rows.add(createRow(1, 10));
        rows.add(createRow(2, "NotANumber"));
        rows.add(createRow(3, 20));

        assertEquals(30.0, Aggregator.calculate(rows, "val", "SUM"), 0.0001);
        assertEquals(20.0, Aggregator.calculate(rows, "val", "MAX"), 0.0001);
        assertEquals(10.0, Aggregator.calculate(rows, "val", "MIN"), 0.0001);
    }
    
    @Test
    void test_invalid_function()
    {
        List<Row> rows = new ArrayList<>();
        rows.add(createRow(1, 1));
        assertThrows(IllegalArgumentException.class, () ->
        {
            Aggregator.calculate(rows, "val", "MEDIAN");
        });
    }
}