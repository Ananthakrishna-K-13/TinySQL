package com.tinysql.fuzz;

import com.tinysql.Main;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@RunWith(JQF.class)
public class MainFuzz {

    @Fuzz
    public void fuzzCLI(String input) 
    {
        InputStream originalIn = System.in;
        
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    
        Main.main(new String[]{});
            
    }
}