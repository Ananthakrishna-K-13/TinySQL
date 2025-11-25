package com.tinysql.tokenizer;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    private final String input;
    private int pos;
    private int length;

    public Tokenizer(String input) {
        // Assertion 1: Critical internal state check
        assert input != null : "Input cannot be null";
        this.input = input;
        this.length = input.length();
        this.pos = 0;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (pos < length) {
            char current = input.charAt(pos);

            if (Character.isWhitespace(current)) {
                pos++;
                continue;
            }

            if (current == '*') { tokens.add(new Token(TokenType.ASTERISK, "*")); pos++; }
            else if (current == ',') { tokens.add(new Token(TokenType.COMMA, ",")); pos++; }
            else if (current == ';') { tokens.add(new Token(TokenType.SEMICOLON, ";")); pos++; }
            else if (current == '(') { tokens.add(new Token(TokenType.LPAREN, "(")); pos++; }
            else if (current == ')') { tokens.add(new Token(TokenType.RPAREN, ")")); pos++; }
            else if (current == '=') { tokens.add(new Token(TokenType.EQUALS, "=")); pos++; }
            else if (current == '>') {
                if (peek() == '=') { tokens.add(new Token(TokenType.GTE, ">=")); pos+=2; }
                else { tokens.add(new Token(TokenType.GT, ">")); pos++; }
            }
            else if (current == '<') {
                if (peek() == '=') { tokens.add(new Token(TokenType.LTE, "<=")); pos+=2; }
                else { tokens.add(new Token(TokenType.LT, "<")); pos++; }
            }
            else if (current == '!') {
                if (peek() == '=') { tokens.add(new Token(TokenType.NOT_EQUALS, "!=")); pos+=2; }
                else { throw new RuntimeException("Unexpected character: !"); }
            }
            else if (current == '\'') {
                tokens.add(readStringLiteral());
            }
            else if (Character.isDigit(current) || current == '-') {
                tokens.add(readNumber());
            }
            else if (Character.isLetter(current)) {
                tokens.add(readIdentifier());
            }
            else {
                throw new RuntimeException("Unexpected character: " + current);
            }
        }
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    private char peek() {
        if (pos + 1 >= length) return '\0';
        return input.charAt(pos + 1);
    }

    private Token readStringLiteral() {
        pos++; // Skip start quote
        StringBuilder sb = new StringBuilder();
        while (pos < length && input.charAt(pos) != '\'') {
            sb.append(input.charAt(pos));
            pos++;
        }
        
        // ASSERTION 2: String must be closed
        // If the fuzzer generates "'hello", this assertion will FAIL (Crash).
        assert pos < length : "Unterminated string literal detected!";
        
        pos++; // Skip end quote
        return new Token(TokenType.STRING_LITERAL, sb.toString());
    }

    private Token readNumber() {
        StringBuilder sb = new StringBuilder();
        if (input.charAt(pos) == '-') {
            sb.append('-');
            pos++;
        }
        
        int dotCount = 0;
        while (pos < length && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
            char c = input.charAt(pos);
            if (c == '.') {
                dotCount++;
                // ASSERTION 3: Numbers cannot have multiple decimals
                // If the fuzzer generates "1.2.3", this assertion will FAIL (Crash).
                assert dotCount <= 1 : "Invalid number format: Multiple decimal points detected";
            }
            sb.append(c);
            pos++;
        }
        
        String val = sb.toString();
        // ASSERTION 4: Malformed Numbers
        // Catches "-", "-.", or "."
        boolean isValid = val.length() > 0 && !val.equals("-") && !val.equals(".") && !val.equals("-.");
        assert isValid : "Invalid number format: Malformed numeric literal";

        return new Token(TokenType.NUMBER_LITERAL, val);
    }

    private Token readIdentifier() {
        StringBuilder sb = new StringBuilder();
        while (pos < length && (Character.isLetterOrDigit(input.charAt(pos)) || input.charAt(pos) == '_' || input.charAt(pos) == '.')) {
            sb.append(input.charAt(pos));
            pos++;
        }
        String word = sb.toString();
        
        if (word.equalsIgnoreCase("SELECT")) return new Token(TokenType.SELECT, word);
        if (word.equalsIgnoreCase("INSERT")) return new Token(TokenType.INSERT, word);
        if (word.equalsIgnoreCase("CREATE")) return new Token(TokenType.CREATE, word);
        if (word.equalsIgnoreCase("TABLE")) return new Token(TokenType.TABLE, word);
        if (word.equalsIgnoreCase("INTO")) return new Token(TokenType.INTO, word);
        if (word.equalsIgnoreCase("VALUES")) return new Token(TokenType.VALUES, word);
        if (word.equalsIgnoreCase("FROM")) return new Token(TokenType.FROM, word);
        if (word.equalsIgnoreCase("WHERE")) return new Token(TokenType.WHERE, word);
        if (word.equalsIgnoreCase("AND")) return new Token(TokenType.AND, word);
        if (word.equalsIgnoreCase("OR")) return new Token(TokenType.OR, word);
        if (word.equalsIgnoreCase("JOIN")) return new Token(TokenType.JOIN, word);
        if (word.equalsIgnoreCase("ON")) return new Token(TokenType.ON, word);
        
        if (word.equalsIgnoreCase("SET")) return new Token(TokenType.SET, word);

        if (word.equalsIgnoreCase("INT")) return new Token(TokenType.INT_TYPE, word);
        if (word.equalsIgnoreCase("TEXT")) return new Token(TokenType.TEXT_TYPE, word);
        if (word.equalsIgnoreCase("FLOAT")) return new Token(TokenType.FLOAT_TYPE, word);
        if (word.equalsIgnoreCase("DOUBLE")) return new Token(TokenType.DOUBLE_TYPE, word);
        if (word.equalsIgnoreCase("BOOL")) return new Token(TokenType.BOOL_TYPE, word);
        if (word.equalsIgnoreCase("COUNT")) return new Token(TokenType.COUNT, word);
        if (word.equalsIgnoreCase("SUM")) return new Token(TokenType.SUM, word);
        if (word.equalsIgnoreCase("AVG")) return new Token(TokenType.AVG, word);
        if (word.equalsIgnoreCase("MIN")) return new Token(TokenType.MIN, word);
        if (word.equalsIgnoreCase("MAX")) return new Token(TokenType.MAX, word);
        
        return new Token(TokenType.IDENTIFIER, word);
    }
}