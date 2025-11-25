package com.tinysql.tokenizer;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class TokenTest
{

    @Test
    void test_toString()
    {
        Token t = new Token(TokenType.SELECT, "SELECT");
        
        String output = t.toString();
        
        assertFalse(output.isEmpty());
        assertEquals("Token{SELECT, 'SELECT'}", output);
    }
}