/**
 * Token class for SimpleLang
 * Represents a single token with its type, value, and position
 */
public class Token {
    private TokenType type;
    private String value;
    private int line;
    private int column;
    
    /**
     * Constructor for Token
     * @param type The type of the token
     * @param value The string value of the token
     * @param line The line number where the token appears
     * @param column The column number where the token starts
     */
    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }
    
    /**
     * Constructor for tokens without a value (keywords, operators)
     * @param type The type of the token
     * @param line The line number where the token appears
     * @param column The column number where the token starts
     */
    public Token(TokenType type, int line, int column) {
        this(type, "", line, column);
    }
    
    // Getters
    public TokenType getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    /**
     * Check if this token is of a specific type
     * @param type The type to check against
     * @return true if the token matches the type
     */
    public boolean is(TokenType type) {
        return this.type == type;
    }
    
    /**
     * Check if this token is one of several types
     * @param types Variable number of types to check against
     * @return true if the token matches any of the types
     */
    public boolean isOneOf(TokenType... types) {
        for (TokenType t : types) {
            if (this.type == t) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * String representation of the token
     * @return Formatted string showing token details
     */
    @Override
    public String toString() {
        if (value.isEmpty()) {
            return String.format("Token(%s, line=%d, col=%d)", 
                type, line, column);
        } else {
            return String.format("Token(%s, '%s', line=%d, col=%d)", 
                type, value, line, column);
        }
    }
    
    /**
     * Compact string representation for display
     * @return Formatted string for GUI display
     */
    public String toDisplayString() {
        if (value.isEmpty()) {
            return String.format("%-15s [%d:%d]", type, line, column);
        } else {
            // Truncate long values for display
            String displayValue = value.length() > 30 
                ? value.substring(0, 27) + "..." 
                : value;
            return String.format("%-15s %-30s [%d:%d]", 
                type, "'" + displayValue + "'", line, column);
        }
    }
}
