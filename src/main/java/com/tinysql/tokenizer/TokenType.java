package com.tinysql.tokenizer;

public enum TokenType {
    // Keywords
    SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, TABLE, INTO, VALUES, FROM, WHERE, AND, OR, SET, JOIN, ON,
    
    // Aggregate Functions
    COUNT, SUM, AVG, MIN, MAX,
    
    // Types
    INT_TYPE, TEXT_TYPE, BOOL_TYPE, FLOAT_TYPE, DOUBLE_TYPE,
    
    // Symbols
    ASTERISK, COMMA, SEMICOLON, LPAREN, RPAREN, EQUALS, GT, LT, GTE, LTE, NOT_EQUALS,
    
    // Literals
    IDENTIFIER, STRING_LITERAL, NUMBER_LITERAL,
    
    EOF
}