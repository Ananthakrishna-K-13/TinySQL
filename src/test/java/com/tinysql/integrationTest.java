package com.tinysql.engine;

import com.tinysql.model.*;
import com.tinysql.engine.*;
import com.tinysql.storage.StorageManager;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest
{
    @Test
    void test_kill_imcd() throws Exception
    {
        Database db = new Database();
        StorageManager storage = new StorageManager();
        Executor mutant = new Executor(db, storage);

        List<Column> cols = new ArrayList<>();
        cols.add(new Column("id", DataType.INTEGER, true));
        mutant.executeCreate("imcd_tt", cols);

        List<Object> vals = new ArrayList<>(); vals.add(1);
        mutant.executeInsert("imcd_tt", vals);

        File f = new File("data/imcd_tt.csv");
        long lines = Files.lines(f.toPath()).count();
        
        assertEquals(2, lines);
    }

    @Test
    void test_kill_ipex()
    {
        Database db = new Database();
        StorageManager storage = new StorageManager();
        Executor mutant = new Executor(db, storage);

        List<Column> colsA = new ArrayList<>();
        colsA.add(new Column("col_a", DataType.INTEGER, true));
        mutant.executeCreate("A", colsA);
        
        List<Object> valA = new ArrayList<>(); valA.add(1);
        mutant.executeInsert("A", valA);

        List<Column> colsB = new ArrayList<>();
        colsB.add(new Column("col_b", DataType.INTEGER, true));
        mutant.executeCreate("B", colsB);
        
        List<Object> valB = new ArrayList<>(); valB.add(1);
        mutant.executeInsert("B", valB);

        ExecutionResult res = mutant.executeJoin("A", "B", "col_a", "col_b");
        
        assertTrue(res.isSuccess());
        assertFalse(res.getMessage().contains("missing"));
    }

    @Test
    void test_kill_irem_mutant()
    {
        Database db = new Database();
        StorageManager storage = new StorageManager();
        Executor mutant = new Executor(db, storage);

        List<Column> cols = new ArrayList<>();
        cols.add(new Column("id", DataType.INTEGER, true));
        mutant.executeCreate("irem_table", cols);
        
        List<Object> vals = new ArrayList<>(); vals.add(1);
        mutant.executeInsert("irem_table", vals);

        ExecutionResult res = mutant.executeSelect("irem_table", null, null, null);

        // expected 1 row, mutant returns 0.
        assertEquals(1, res.getData().size());
    }
}