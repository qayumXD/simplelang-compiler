/**
 * TokenType enumeration for SimpleLang
 * Defines all possible token types in the language
 */
public enum TokenType {
    // Literals
    NUMBER,         // 123, 45.67
    STRING,         // "hello", 'world'
    TRUE,           // true
    FALSE,          // false
    
    // Identifiers
    IDENTIFIER,     // variable names, function names
    
    // Keywords
    LET,            // let
    FUNC,           // func
    IF,             // if
    ELIF,           // elif
    ELSE,           // else
    WHILE,          // while
    FOR,            // for
    IN,             // in
    RETURN,         // return
    AND,            // and
    OR,             // or
    NOT,            // not
    
    // Type keywords
    INT,            // int
    FLOAT,          // float
    STRING_TYPE,    // string
    BOOL,           // bool
    VOID,           // void
    ARRAY,          // array
    OBJECT,         // object
    
    // Built-in functions
    PRINT,          // print
    RANGE,          // range
    
    // Operators
    PLUS,           // +
    MINUS,          // -
    STAR,           // *
    SLASH,          // /
    PERCENT,        // %
    
    EQUAL,          // =
    EQUAL_EQUAL,    // ==
    NOT_EQUAL,      // !=
    LESS,           // <
    GREATER,        // >
    LESS_EQUAL,     // <=
    GREATER_EQUAL,  // >=
    
    // Delimiters
    LPAREN,         // (
    RPAREN,         // )
    LBRACKET,       // [
    RBRACKET,       // ]
    LBRACE,         // {
    RBRACE,         // }
    COLON,          // :
    COMMA,          // ,
    DOT,            // .
    ARROW,          // ->
    
    // Special
    NEWLINE,        // \n (significant for indentation)
    INDENT,         // Indentation increase
    DEDENT,         // Indentation decrease
    EOF,            // End of file
    
    // Error
    ERROR           // Lexical error
}
