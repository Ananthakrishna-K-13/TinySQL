package com.tinysql;

import org.junit.jupiter.api.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class MainCLITest
{

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeAll
    static void init()
    {
        clearDataDirectory();
    }

    private static void clearDataDirectory()
    {
        File dir = new File("data");
        if (dir.exists() && dir.isDirectory())
        {
            File[] files = dir.listFiles();
            if (files != null)
            {
                for (File file : files)
                {
                    file.delete();
                }
            }
        }
    }

    @BeforeEach
    void setUp()
    {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restore()
    {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private void run(String input)
    {
        String fullInput = input + "\nEXIT"; 
        System.setIn(new ByteArrayInputStream(fullInput.getBytes()));
        Main.main(new String[]{});
    }

    @Test
    void test_startup()
    {
        run(""); 
        String output = outContent.toString();
        assertTrue(output.contains("TinySQL Pro Database Engine"));
        assertTrue(output.contains("Ready. Commands:")); 
    }

    @Test
    void test_unknown()
    {
        run("GARBAGE");
        assertTrue(outContent.toString().contains("Unknown command"));
    }

    @Test
    void test_create()
    {
        String cmd = "CREATE TABLE users (id INT, name TEXT, age INT)";
        String cmdInvalid = "CREATE TABLE"; 

        run(cmd + "\n" + cmdInvalid);
        
        String output = outContent.toString();
        assertTrue(output.contains("Created users")); 
        assertTrue(output.contains("Syntax Error")); 
    }

    @Test
    void test_insert()
    {
        String create = "CREATE TABLE users2 (id INT, name TEXT)";
        String insert = "INSERT INTO users2 VALUES 1 Alice";
        String insertInvalid = "INSERT INTO users2 1 Alice";

        run(create + '\n' + insert + "\n" + insertInvalid);
        
        String output = outContent.toString();
        assertTrue(output.contains("1 row inserted"));
        assertTrue(output.contains("Syntax Error"));
    }

    @Test
    void test_select()
    {
        String create = "CREATE TABLE users3 (id INT, age INT)";
        String insert = "INSERT INTO users3 VALUES 1 25";
        
        String select = "SELECT * FROM users3";
        String selectWhere = "SELECT * FROM users3 WHERE age > 20";
        String selectInvalid = "SELECT * users3";

        run(create + "\n" +  insert + "\n" + select + "\n" + selectWhere + "\n" + selectInvalid);
        
        String output = outContent.toString();
        assertTrue(output.contains("rows found"));
        assertTrue(output.contains("Missing FROM"));
    }

    @Test
    void test_agg()
    {
        String create = "CREATE TABLE items (price DOUBLE)";
        String insert1 = "INSERT INTO items VALUES 10.5";
        String insert2 = "INSERT INTO items VALUES 20.5";
        
        String agg = "SELECT AVG(price) FROM items";

        run(create + "\n" + insert1 + "\n" + insert2 + "\n" + agg);

        String output = outContent.toString();
        assertTrue(output.contains("RESULT:"));
    }

    @Test
    void test_join()
    {
        String join = "JOIN users orders ON id user_id";
        String joinInvalid = "JOIN users orders id user_id";
        String joinShort = "JOIN users";

        run(join + "\n" + joinInvalid + "\n" + joinShort);
        
        String output = outContent.toString();
        assertTrue(output.contains("Syntax: JOIN")); 
    }
    
    @Test
    void test_empty()
    {
        run("\n   \n");
    }
}