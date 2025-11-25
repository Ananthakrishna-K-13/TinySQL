// package com.tinysql.engine;

// import com.tinysql.engine.ExecutionResult;
// import com.tinysql.model.*;
// import com.tinysql.storage.StorageManager;
// import org.junit.jupiter.api.Test;
// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.List;
// import static org.junit.jupiter.api.Assertions.*;

// class IREMMutantTest {

//     // IREM - Integration Return Expression Modification
//     static class IREMExecutor extends Executor {
//         public IREMExecutor(Database db, StorageManager storage) {
//             super(db, storage);
//         }

//         @Override
//         public ExecutionResult executeSelect(String tableName, String c, String o, String v) 
//         {            
//             return new ExecutionResult(true, "mutant", Collections.emptyList());
//         }
//     }

//     @Test
//     void test_kill_irem_mutant()
//     {
//         Database db = new Database();
//         StorageManager storage = new StorageManager();
//         Executor mutant = new IREMExecutor(db, storage);

//         List<Column> cols = new ArrayList<>();
//         cols.add(new Column("id", DataType.INTEGER, true));
//         mutant.executeCreate("irem_table", cols);
        
//         List<Object> vals = new ArrayList<>(); vals.add(1);
//         mutant.executeInsert("irem_table", vals);

//         ExecutionResult res = mutant.executeSelect("irem_table", null, null, null);

//         // expected 1 row, mutant returns 0.
//         assertEquals(1, res.getData().size());
//     }
// }