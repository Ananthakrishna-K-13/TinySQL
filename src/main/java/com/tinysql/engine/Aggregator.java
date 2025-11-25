package com.tinysql.engine;

import com.tinysql.model.Row;
import java.util.List;

public class Aggregator {

    public static double calculate(List<Row> rows, String colName, String function) {
        if (rows.isEmpty()) return 0.0;

        switch (function.toUpperCase()) {
            case "COUNT":
                return rows.size();
            case "SUM":
                return calculateSum(rows, colName);
            case "AVG":
                return calculateSum(rows, colName) / rows.size();
            case "MAX":
                return calculateMax(rows, colName);
            case "MIN":
                return calculateMin(rows, colName);
            default:
                throw new IllegalArgumentException("Unknown aggregation function: " + function);
        }
    }

    private static double calculateSum(List<Row> rows, String colName) {
        double sum = 0;
        for (Row r : rows) {
            Object val = r.get(colName);
            if (val instanceof Number) {
                sum += ((Number) val).doubleValue();
            }
        }
        return sum;
    }

    private static double calculateMax(List<Row> rows, String colName) {
        double max = -Double.MAX_VALUE;
        for (Row r : rows) {
            Object val = r.get(colName);
            if (val instanceof Number) {
                double v = ((Number) val).doubleValue();
                if (v > max) max = v;
            }
        }
        return max;
    }

    private static double calculateMin(List<Row> rows, String colName) {
        double min = Double.MAX_VALUE;
        for (Row r : rows) {
            Object val = r.get(colName);
            if (val instanceof Number) {
                double v = ((Number) val).doubleValue();
                if (v < min) min = v;
            }
        }
        return min;
    }
}