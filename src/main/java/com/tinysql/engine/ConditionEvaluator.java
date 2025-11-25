package com.tinysql.engine;

import com.tinysql.model.Row;

public class ConditionEvaluator {

    public static boolean evaluate(Row row, String colName, String operator, String value) {
        // Handle Table.Column syntax for joins
        Object cellValue = row.get(colName);
        
        if (cellValue == null) return false;

        if (cellValue instanceof Integer) {
            return evaluateInteger((Integer) cellValue, Integer.parseInt(value), operator);
        } else if (cellValue instanceof Double) {
            return evaluateDouble((Double) cellValue, Double.parseDouble(value), operator);
        } else if (cellValue instanceof Float) {
            return evaluateFloat((Float) cellValue, Float.parseFloat(value), operator);
        } else if (cellValue instanceof Boolean) {
            return evaluateBoolean((Boolean) cellValue, Boolean.parseBoolean(value), operator);
        } else {
            return evaluateString(cellValue.toString(), value, operator);
        }
    }

    private static boolean evaluateInteger(int cell, int target, String op) {
        switch (op) {
            case "=": return cell == target;
            case ">": return cell > target;
            case "<": return cell < target;
            case ">=": return cell >= target;
            case "<=": return cell <= target;
            case "!=": return cell != target;
            default: return false;
        }
    }

    private static boolean evaluateDouble(double cell, double target, String op) {
        double epsilon = 0.000001;
        switch (op) {
            case "=": return Math.abs(cell - target) < epsilon;
            case ">": return cell > target;
            case "<": return cell < target;
            case ">=": return cell >= target;
            case "<=": return cell <= target;
            case "!=": return Math.abs(cell - target) > epsilon;
            default: return false;
        }
    }

    private static boolean evaluateFloat(float cell, float target, String op) {
        float epsilon = 0.000001f;
        switch (op) {
            case "=": return Math.abs(cell - target) < epsilon;
            case ">": return cell > target;
            case "<": return cell < target;
            case ">=": return cell >= target;
            case "<=": return cell <= target;
            case "!=": return Math.abs(cell - target) > epsilon;
            default: return false;
        }
    }

    private static boolean evaluateBoolean(boolean cell, boolean target, String op) {
        if (op.equals("=")) return cell == target;
        if (op.equals("!=")) return cell != target;
        return false;
    }

    private static boolean evaluateString(String cell, String target, String op) {
        switch (op) {
            case "=": return cell.equals(target);
            case "!=": return !cell.equals(target);
            default: return false;
        }
    }
}