package com.tinysql.tokenizer;

import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest
{

    private List<Token> tokenize(String input)
    {
        return new Tokenizer(input).tokenize();
    }

    @Test
    void test_symbol()
    {
        String input = "* , ; ( ) = > < >= <= !=";
        List<Token> tokens = tokenize(input);

        assertEquals(TokenType.ASTERISK, tokens.get(0).type);
        assertEquals(TokenType.COMMA, tokens.get(1).type);
        assertEquals(TokenType.SEMICOLON, tokens.get(2).type);
        assertEquals(TokenType.LPAREN, tokens.get(3).type);
        assertEquals(TokenType.RPAREN, tokens.get(4).type);
        assertEquals(TokenType.EQUALS, tokens.get(5).type);
        assertEquals(TokenType.GT, tokens.get(6).type);
        assertEquals(TokenType.LT, tokens.get(7).type);
        assertEquals(TokenType.GTE, tokens.get(8).type);
        assertEquals(TokenType.LTE, tokens.get(9).type);
        assertEquals(TokenType.NOT_EQUALS, tokens.get(10).type);
        assertEquals(TokenType.EOF, tokens.get(11).type);
    }

    @Test
    void test_keyword()
    {
        String input = "SELECT INSERT UPDATE DELETE CREATE DROP TABLE INTO VALUES FROM WHERE AND OR SET JOIN ON " +
                       "INT TEXT BOOL FLOAT DOUBLE " +
                       "COUNT SUM AVG MIN MAX";
        
        List<Token> tokens = tokenize(input);
        
        int i = 0;
        assertEquals(TokenType.SELECT, tokens.get(i++).type);
        assertEquals(TokenType.INSERT, tokens.get(i++).type);
        assertEquals(TokenType.IDENTIFIER, tokens.get(i++).type);
        assertEquals(TokenType.IDENTIFIER, tokens.get(i++).type);
        
        assertEquals(TokenType.CREATE, tokenize("CREATE").get(0).type);
        assertEquals(TokenType.TABLE, tokenize("TABLE").get(0).type);
        assertEquals(TokenType.INTO, tokenize("INTO").get(0).type);
        assertEquals(TokenType.VALUES, tokenize("VALUES").get(0).type);
        assertEquals(TokenType.FROM, tokenize("FROM").get(0).type);
        assertEquals(TokenType.WHERE, tokenize("WHERE").get(0).type);
        assertEquals(TokenType.AND, tokenize("AND").get(0).type);
        assertEquals(TokenType.OR, tokenize("OR").get(0).type);
        assertEquals(TokenType.SET, tokenize("SET").get(0).type);
        assertEquals(TokenType.JOIN, tokenize("JOIN").get(0).type);
        assertEquals(TokenType.ON, tokenize("ON").get(0).type);

        assertEquals(TokenType.INT_TYPE, tokenize("INT").get(0).type);
        assertEquals(TokenType.TEXT_TYPE, tokenize("TEXT").get(0).type);
        assertEquals(TokenType.BOOL_TYPE, tokenize("BOOL").get(0).type);
        assertEquals(TokenType.FLOAT_TYPE, tokenize("FLOAT").get(0).type);
        assertEquals(TokenType.DOUBLE_TYPE, tokenize("DOUBLE").get(0).type);

        assertEquals(TokenType.COUNT, tokenize("COUNT").get(0).type);
        assertEquals(TokenType.SUM, tokenize("SUM").get(0).type);
        assertEquals(TokenType.AVG, tokenize("AVG").get(0).type);
        assertEquals(TokenType.MIN, tokenize("MIN").get(0).type);
        assertEquals(TokenType.MAX, tokenize("MAX").get(0).type);
    }

    @Test
    void test_string()
    {
        String input = "'Hello World'";
        List<Token> tokens = tokenize(input);
        
        assertEquals(TokenType.STRING_LITERAL, tokens.get(0).type);
        assertEquals("Hello World", tokens.get(0).value);
        
        assertEquals("", tokenize("''").get(0).value);
    }

    @Test
    void test_number()
    {
        List<Token> t1 = tokenize("123");
        assertEquals(TokenType.NUMBER_LITERAL, t1.get(0).type);
        assertEquals("123", t1.get(0).value);

        List<Token> t2 = tokenize("123.456");
        assertEquals("123.456", t2.get(0).value);

        List<Token> t3 = tokenize("-99");
        assertEquals("-99", t3.get(0).value);

        List<Token> t4 = tokenize("-99.99");
        assertEquals("-99.99", t4.get(0).value);
    }

    @Test
    void test_identifier()
    {
        String input = "myTable_123";
        List<Token> tokens = tokenize(input);
        
        assertEquals(TokenType.IDENTIFIER, tokens.get(0).type);
        assertEquals("myTable_123", tokens.get(0).value);
    }

    @Test
    void test_whitespace()
    {
        String input = "   SELECT   \t  * \n  FROM  ";
        List<Token> tokens = tokenize(input);
        
        assertEquals(TokenType.SELECT, tokens.get(0).type);
        assertEquals(TokenType.ASTERISK, tokens.get(1).type);
        assertEquals(TokenType.FROM, tokens.get(2).type);
    }

    @Test
    void test_unexpected()
    {
        assertThrows(RuntimeException.class, () ->
        {
            tokenize("#");
        });
        
        assertThrows(RuntimeException.class, () ->
        {
            tokenize("!"); 
        });
    }
    
    @Test
    void test_peek()
    {
        List<Token> t1 = tokenize(">");
        assertEquals(TokenType.GT, t1.get(0).type);
        
        List<Token> t2 = tokenize(">=");
        assertEquals(TokenType.GTE, t2.get(0).type);
        
        List<Token> t3 = tokenize("<");
        assertEquals(TokenType.LT, t3.get(0).type);
        
        List<Token> t4 = tokenize("<=");
        assertEquals(TokenType.LTE, t4.get(0).type);
    }
}