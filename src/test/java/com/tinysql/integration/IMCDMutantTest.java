// package com.tinysql.engine;

// import com.tinysql.model.*;
// import com.tinysql.storage.StorageManager;
// import com.tinysql.util.TinySQLException;
// import org.junit.jupiter.api.*;
// import java.io.File;
// import java.nio.file.Files;
// import java.util.ArrayList;
// import java.util.List;
// import static org.junit.jupiter.api.Assertions.*;

// class IMCDMutantTest
// {

//     static class IMCDExecutor extends Executor
//     {
//         private final Database db;
//         private final StorageManager storage;
        
//         public IMCDExecutor(Database db, StorageManager storage)
//         {
//             super(db, storage);
//             this.db = db;
//             this.storage = storage;
//         }

//         @Override
//         public ExecutionResult executeInsert(String tableName, List<Object> values)
//         {
//             Table t = getTable(tableName);
//             if (t == null) return new ExecutionResult(false, "Table not found");

//             Row row = new Row(t.getNextId());
//             List<Column> cols = t.getColumns();
            
//             if (values.size() != cols.size()) return new ExecutionResult(false, "Mismatch");

//             for (int i = 0; i < cols.size(); i++)
//             {
//                 row.set(cols.get(i).getName(), values.get(i));
//             }
            
//             t.insert(row);
            
//             // storage.saveTable(t);
            
//             return new ExecutionResult(true, "1 row inserted");
//         }

//         private Table getTable(String name)
//         {
//             Table t = db.getTable(name);
//             if (t == null)
//             {
//                 try
//                 {
//                     t = storage.loadTable(name);
//                     if (t != null) db.addTable(t);
//                 }
//                 catch (TinySQLException e) { return null; }
//             }
//             return t;
//         }
//     }

//     @Test
//     void test_kill_imcd() throws Exception
//     {
//         Database db = new Database();
//         StorageManager storage = new StorageManager();
//         Executor mutant = new IMCDExecutor(db, storage);

//         List<Column> cols = new ArrayList<>();
//         cols.add(new Column("id", DataType.INTEGER, true));
//         mutant.executeCreate("imcd_t", cols);

//         List<Object> vals = new ArrayList<>(); vals.add(1);
//         mutant.executeInsert("imcd_t", vals);

//         File f = new File("data/imcd_t.csv");
//         long lines = Files.lines(f.toPath()).count();
        
//         assertEquals(2, lines);
//     }
// }