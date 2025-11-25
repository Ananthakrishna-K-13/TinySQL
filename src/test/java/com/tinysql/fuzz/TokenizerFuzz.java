package com.tinysql.fuzz;

import com.tinysql.tokenizer.Tokenizer;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

@RunWith(JQF.class)
public class TokenizerFuzz
{
    @Fuzz
    public void fuzzTokenizer(String input)
    {
        Tokenizer t = new Tokenizer(input);
        t.tokenize();

    }
}