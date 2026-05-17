import java.util.*;

/**
 * Lexer for SimpleLang
 * Performs lexical analysis and tokenization with Python-style indentation
 */
public class Lexer {
    private String source;
    private int position;
    private int line;
    private int column;
    private char currentChar;
    private List<Token> tokens;
    private Stack<Integer> indentStack;
    private int currentIndent;
    private boolean atLineStart;
    private List<String> errors;
    
    // Keywords map
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    static {
        KEYWORDS.put("let", TokenType.LET);
        KEYWORDS.put("func", TokenType.FUNC);
        KEYWORDS.put("if", TokenType.IF);
        KEYWORDS.put("elif", TokenType.ELIF);
        KEYWORDS.put("else", TokenType.ELSE);
        KEYWORDS.put("while", TokenType.WHILE);
        KEYWORDS.put("for", TokenType.FOR);
        KEYWORDS.put("in", TokenType.IN);
        KEYWORDS.put("return", TokenType.RETURN);
        KEYWORDS.put("and", TokenType.AND);
        KEYWORDS.put("or", TokenType.OR);
        KEYWORDS.put("not", TokenType.NOT);
        KEYWORDS.put("true", TokenType.TRUE);
        KEYWORDS.put("false", TokenType.FALSE);
        KEYWORDS.put("int", TokenType.INT);
        KEYWORDS.put("float", TokenType.FLOAT);
        KEYWORDS.put("string", TokenType.STRING_TYPE);
        KEYWORDS.put("bool", TokenType.BOOL);
        KEYWORDS.put("void", TokenType.VOID);
        KEYWORDS.put("array", TokenType.ARRAY);
        KEYWORDS.put("object", TokenType.OBJECT);
        KEYWORDS.put("print", TokenType.PRINT);
        KEYWORDS.put("range", TokenType.RANGE);
    }
    
    /**
     * Constructor
     * @param source The source code to tokenize
     */
    public Lexer(String source) {
        this.source = source;
        this.position = 0;
        this.line = 1;
        this.column = 1;
        this.currentChar = source.length() > 0 ? source.charAt(0) : '\0';
        this.tokens = new ArrayList<>();
        this.indentStack = new Stack<>();
        this.indentStack.push(0); // Base indentation level
        this.currentIndent = 0;
        this.atLineStart = true;
        this.errors = new ArrayList<>();
    }
    
    /**
     * Advance to the next character
     */
    private void advance() {
        if (currentChar == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        
        position++;
        if (position < source.length()) {
            currentChar = source.charAt(position);
        } else {
            currentChar = '\0';
        }
    }
    
    /**
     * Peek at the next character without advancing
     * @return The next character or '\0' if at end
     */
    private char peek() {
        if (position + 1 < source.length()) {
            return source.charAt(position + 1);
        }
        return '\0';
    }
    
    /**
     * Skip whitespace (spaces and tabs, but not newlines)
     */
    private void skipWhitespace() {
        while (currentChar == ' ' || currentChar == '\t') {
            advance();
        }
    }
    
    /**
     * Skip comments (lines starting with #)
     */
    private void skipComment() {
        if (currentChar == '#') {
            while (currentChar != '\n' && currentChar != '\0') {
                advance();
            }
        }
    }
    
    /**
     * Handle indentation at the start of a line
     */
    private void handleIndentation() {
        int spaces = 0;
        while (currentChar == ' ' || currentChar == '\t') {
            if (currentChar == ' ') {
                spaces++;
            } else { // tab
                spaces += 4; // Treat tab as 4 spaces
            }
            advance();
        }
        
        // Skip blank lines and comments
        if (currentChar == '\n' || currentChar == '#') {
            return;
        }
        
        currentIndent = spaces;
        
        // Generate INDENT tokens
        if (currentIndent > indentStack.peek()) {
            indentStack.push(currentIndent);
            tokens.add(new Token(TokenType.INDENT, line, column));
        }
        // Generate DEDENT tokens
        else if (currentIndent < indentStack.peek()) {
            while (!indentStack.isEmpty() && currentIndent < indentStack.peek()) {
                indentStack.pop();
                tokens.add(new Token(TokenType.DEDENT, line, column));
            }
            
            // Check for indentation error
            if (indentStack.isEmpty() || currentIndent != indentStack.peek()) {
                errors.add(String.format("Indentation error at line %d, column %d", line, column));
            }
        }
        
        atLineStart = false;
    }
    
    /**
     * Read a number (integer or float)
     * @return Token representing the number
     */
    private Token readNumber() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        boolean isFloat = false;
        
        while (Character.isDigit(currentChar) || currentChar == '.') {
            if (currentChar == '.') {
                if (isFloat) {
                    errors.add(String.format("Invalid number format at line %d, column %d", line, column));
                    break;
                }
                isFloat = true;
            }
            sb.append(currentChar);
            advance();
        }
        
        return new Token(TokenType.NUMBER, sb.toString(), startLine, startColumn);
    }
    
    /**
     * Read a string literal (double or single quoted)
     * @return Token representing the string
     */
    private Token readString() {
        int startLine = line;
        int startColumn = column;
        char quote = currentChar;
        StringBuilder sb = new StringBuilder();
        
        advance(); // Skip opening quote
        
        while (currentChar != quote && currentChar != '\0' && currentChar != '\n') {
            if (currentChar == '\\') {
                advance();
                // Handle escape sequences
                switch (currentChar) {
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    case 'r': sb.append('\r'); break;
                    case '\\': sb.append('\\'); break;
                    case '"': sb.append('"'); break;
                    case '\'': sb.append('\''); break;
                    default: sb.append(currentChar);
                }
            } else {
                sb.append(currentChar);
            }
            advance();
        }
        
        if (currentChar != quote) {
            errors.add(String.format("Unterminated string at line %d, column %d", startLine, startColumn));
            return new Token(TokenType.ERROR, sb.toString(), startLine, startColumn);
        }
        
        advance(); // Skip closing quote
        return new Token(TokenType.STRING, sb.toString(), startLine, startColumn);
    }
    
    /**
     * Read an identifier or keyword
     * @return Token representing the identifier or keyword
     */
    private Token readIdentifier() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        
        while (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
            sb.append(currentChar);
            advance();
        }
        
        String value = sb.toString();
        TokenType type = KEYWORDS.getOrDefault(value, TokenType.IDENTIFIER);
        
        return new Token(type, value, startLine, startColumn);
    }
    
    /**
     * Get the next token
     * @return The next token or null if at end
     */
    private Token getNextToken() {
        // Handle indentation at line start
        if (atLineStart) {
            handleIndentation();
        }
        
        // Skip whitespace and comments
        while (currentChar == ' ' || currentChar == '\t' || currentChar == '#') {
            if (currentChar == '#') {
                skipComment();
            } else {
                skipWhitespace();
            }
        }
        
        // End of file
        if (currentChar == '\0') {
            return null;
        }
        
        int startLine = line;
        int startColumn = column;
        
        // Newline
        if (currentChar == '\n') {
            advance();
            atLineStart = true;
            return new Token(TokenType.NEWLINE, startLine, startColumn);
        }
        
        // Numbers
        if (Character.isDigit(currentChar)) {
            return readNumber();
        }
        
        // Strings
        if (currentChar == '"' || currentChar == '\'') {
            return readString();
        }
        
        // Identifiers and keywords
        if (Character.isLetter(currentChar) || currentChar == '_') {
            return readIdentifier();
        }
        
        // Two-character operators
        if (currentChar == '=' && peek() == '=') {
            advance();
            advance();
            return new Token(TokenType.EQUAL_EQUAL, startLine, startColumn);
        }
        if (currentChar == '!' && peek() == '=') {
            advance();
            advance();
            return new Token(TokenType.NOT_EQUAL, startLine, startColumn);
        }
        if (currentChar == '<' && peek() == '=') {
            advance();
            advance();
            return new Token(TokenType.LESS_EQUAL, startLine, startColumn);
        }
        if (currentChar == '>' && peek() == '=') {
            advance();
            advance();
            return new Token(TokenType.GREATER_EQUAL, startLine, startColumn);
        }
        if (currentChar == '-' && peek() == '>') {
            advance();
            advance();
            return new Token(TokenType.ARROW, startLine, startColumn);
        }
        
        // Single-character operators and delimiters
        Token token = null;
        switch (currentChar) {
            case '+': token = new Token(TokenType.PLUS, startLine, startColumn); break;
            case '-': token = new Token(TokenType.MINUS, startLine, startColumn); break;
            case '*': token = new Token(TokenType.STAR, startLine, startColumn); break;
            case '/': token = new Token(TokenType.SLASH, startLine, startColumn); break;
            case '%': token = new Token(TokenType.PERCENT, startLine, startColumn); break;
            case '=': token = new Token(TokenType.EQUAL, startLine, startColumn); break;
            case '<': token = new Token(TokenType.LESS, startLine, startColumn); break;
            case '>': token = new Token(TokenType.GREATER, startLine, startColumn); break;
            case '(': token = new Token(TokenType.LPAREN, startLine, startColumn); break;
            case ')': token = new Token(TokenType.RPAREN, startLine, startColumn); break;
            case '[': token = new Token(TokenType.LBRACKET, startLine, startColumn); break;
            case ']': token = new Token(TokenType.RBRACKET, startLine, startColumn); break;
            case '{': token = new Token(TokenType.LBRACE, startLine, startColumn); break;
            case '}': token = new Token(TokenType.RBRACE, startLine, startColumn); break;
            case ':': token = new Token(TokenType.COLON, startLine, startColumn); break;
            case ',': token = new Token(TokenType.COMMA, startLine, startColumn); break;
            case '.': token = new Token(TokenType.DOT, startLine, startColumn); break;
            default:
                errors.add(String.format("Unexpected character '%c' at line %d, column %d", 
                    currentChar, line, column));
                token = new Token(TokenType.ERROR, String.valueOf(currentChar), startLine, startColumn);
        }
        
        advance();
        return token;
    }
    
    /**
     * Tokenize the entire source code
     * @return List of tokens
     */
    public List<Token> tokenize() {
        Token token;
        while ((token = getNextToken()) != null) {
            // Skip newlines that are not significant
            if (token.getType() == TokenType.NEWLINE) {
                // Only add newline if it's significant (not multiple in a row)
                if (tokens.isEmpty() || tokens.get(tokens.size() - 1).getType() != TokenType.NEWLINE) {
                    tokens.add(token);
                }
            } else {
                tokens.add(token);
            }
        }
        
        // Generate DEDENT tokens for remaining indentation levels
        while (indentStack.size() > 1) {
            indentStack.pop();
            tokens.add(new Token(TokenType.DEDENT, line, column));
        }
        
        // Add EOF token
        tokens.add(new Token(TokenType.EOF, line, column));
        
        return tokens;
    }
    
    /**
     * Get lexical errors
     * @return List of error messages
     */
    public List<String> getErrors() {
        return errors;
    }
    
    /**
     * Check if there are any errors
     * @return true if there are errors
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
