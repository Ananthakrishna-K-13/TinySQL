package com.tinysql.engine;

import java.util.List;
import com.tinysql.model.Row;

public class ExecutionResult {
    private boolean success;
    private String message;
    private List<Row> data;
    private Double aggregateResult;

    public ExecutionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
        this.aggregateResult = null;
    }

    public ExecutionResult(boolean success, String message, List<Row> data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.aggregateResult = null;
    }
    
    public ExecutionResult(boolean success, String message, Double aggregateResult) {
        this.success = success;
        this.message = message;
        this.data = null;
        this.aggregateResult = aggregateResult;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<Row> getData() { return data; }
    public Double getAggregateResult() { return aggregateResult; }
}