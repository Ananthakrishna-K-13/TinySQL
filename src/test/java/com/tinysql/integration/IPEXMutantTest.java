// package com.tinysql.engine;

// import com.tinysql.model.*;
// import com.tinysql.storage.StorageManager;
// import org.junit.jupiter.api.*;
// import java.util.ArrayList;
// import java.util.List;
// import static org.junit.jupiter.api.Assertions.*;

// class IPEXMutantTest
// {

//     static class IPEXExecutor extends Executor
//     {
//         public IPEXExecutor(Database db, StorageManager storage)
//         {
//             super(db, storage);
//         }

//         @Override
//         public ExecutionResult executeJoin(String t1, String t2, String c1, String c2)
//         {
//             // MUTANT: Literal Parameter Exchange
//             // We swap c1 and c2 when calling the super method.
//             return super.executeJoin(t1, t2, c2, c1);
//         }
//     }

//     @Test
//     void test_kill_ipex()
//     {
//         Database db = new Database();
//         StorageManager storage = new StorageManager();
//         Executor mutant = new IPEXExecutor(db, storage);

//         List<Column> colsA = new ArrayList<>();
//         colsA.add(new Column("col_a", DataType.INTEGER, true));
//         mutant.executeCreate("A", colsA);
        
//         List<Object> valA = new ArrayList<>(); valA.add(1);
//         mutant.executeInsert("A", valA);

//         List<Column> colsB = new ArrayList<>();
//         colsB.add(new Column("col_b", DataType.INTEGER, true));
//         mutant.executeCreate("B", colsB);
        
//         List<Object> valB = new ArrayList<>(); valB.add(1);
//         mutant.executeInsert("B", valB);

//         ExecutionResult res = mutant.executeJoin("A", "B", "col_a", "col_b");
        
//         assertTrue(res.isSuccess());
//         assertFalse(res.getMessage().contains("missing"));
//     }
// }