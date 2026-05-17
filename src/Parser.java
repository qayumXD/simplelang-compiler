import java.util.*;

/**
 * Parser for SimpleLang
 * Implements recursive descent parsing to build an Abstract Syntax Tree
 */
public class Parser {
    private List<Token> tokens;
    private int position;
    private Token currentToken;
    private List<String> errors;
    
    /**
     * Constructor
     * @param tokens List of tokens from the lexer
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
        this.currentToken = tokens.size() > 0 ? tokens.get(0) : null;
        this.errors = new ArrayList<>();
    }
    
    /**
     * Advance to the next token
     */
    private void advance() {
        position++;
        if (position < tokens.size()) {
            currentToken = tokens.get(position);
        } else {
            currentToken = null;
        }
    }
    
    /**
     * Peek at the next token without advancing
     * @return The next token or null
     */
    private Token peek() {
        if (position + 1 < tokens.size()) {
            return tokens.get(position + 1);
        }
        return null;
    }
    
    /**
     * Check if current token matches expected type
     * @param type Expected token type
     * @return true if matches
     */
    private boolean check(TokenType type) {
        return currentToken != null && currentToken.is(type);
    }
    
    /**
     * Check if current token matches any of the expected types
     * @param types Expected token types
     * @return true if matches any
     */
    private boolean checkAny(TokenType... types) {
        return currentToken != null && currentToken.isOneOf(types);
    }
    
    /**
     * Consume a token of expected type
     * @param type Expected token type
     * @param message Error message if not matched
     * @return The consumed token
     */
    private Token consume(TokenType type, String message) {
        if (check(type)) {
            Token token = currentToken;
            advance();
            return token;
        }
        
        error(message);
        return null;
    }
    
    /**
     * Skip newlines
     */
    private void skipNewlines() {
        while (check(TokenType.NEWLINE)) {
            advance();
        }
    }
    
    /**
     * Report a parse error
     * @param message Error message
     */
    private void error(String message) {
        if (currentToken != null) {
            errors.add(String.format("Parse error at line %d, column %d: %s", 
                currentToken.getLine(), currentToken.getColumn(), message));
        } else {
            errors.add("Parse error: " + message);
        }
    }
    
    /**
     * Parse the entire program
     * @return Program AST node
     */
    public ProgramNode parse() {
        ProgramNode program = new ProgramNode();
        
        skipNewlines();
        
        while (currentToken != null && !check(TokenType.EOF)) {
            try {
                ASTNode stmt = parseStatement();
                if (stmt != null) {
                    program.addStatement(stmt);
                }
                skipNewlines();
            } catch (Exception e) {
                error("Unexpected error: " + e.getMessage());
                // Skip to next statement
                while (currentToken != null && !check(TokenType.NEWLINE) && !check(TokenType.EOF)) {
                    advance();
                }
                skipNewlines();
            }
        }
        
        return program;
    }
    
    /**
     * Parse a statement
     * @return Statement AST node
     */
    private ASTNode parseStatement() {
        skipNewlines();
        
        if (check(TokenType.LET)) {
            return parseVarDecl();
        } else if (check(TokenType.FUNC)) {
            return parseFuncDecl();
        } else if (check(TokenType.IF)) {
            return parseIfStmt();
        } else if (check(TokenType.WHILE)) {
            return parseWhileStmt();
        } else if (check(TokenType.FOR)) {
            return parseForStmt();
        } else if (check(TokenType.RETURN)) {
            return parseReturnStmt();
        } else if (check(TokenType.IDENTIFIER)) {
            // Could be assignment or expression statement
            Token next = peek();
            if (next != null && next.is(TokenType.EQUAL)) {
                return parseAssignment();
            } else {
                return parseExpressionStmt();
            }
        } else {
            return parseExpressionStmt();
        }
    }
    
    /**
     * Parse variable declaration
     * let x: int = 10
     */
    private VarDeclNode parseVarDecl() {
        Token letToken = consume(TokenType.LET, "Expected 'let'");
        Token nameToken = consume(TokenType.IDENTIFIER, "Expected variable name");
        
        String name = nameToken != null ? nameToken.getValue() : "";
        String type = null;
        
        // Optional type annotation
        if (check(TokenType.COLON)) {
            advance();
            type = parseType();
        }
        
        // Initializer
        consume(TokenType.EQUAL, "Expected '=' in variable declaration");
        ASTNode initializer = parseExpression();
        
        int line = letToken != null ? letToken.getLine() : 0;
        int column = letToken != null ? letToken.getColumn() : 0;
        
        return new VarDeclNode(name, type, initializer, line, column);
    }
    
    /**
     * Parse assignment
     * x = 10
     */
    private AssignmentNode parseAssignment() {
        Token nameToken = consume(TokenType.IDENTIFIER, "Expected variable name");
        consume(TokenType.EQUAL, "Expected '='");
        ASTNode value = parseExpression();
        
        String name = nameToken != null ? nameToken.getValue() : "";
        int line = nameToken != null ? nameToken.getLine() : 0;
        int column = nameToken != null ? nameToken.getColumn() : 0;
        
        return new AssignmentNode(name, value, line, column);
    }
    
    /**
     * Parse function declaration
     * func add(a: int, b: int) -> int:
     */
    private FuncDeclNode parseFuncDecl() {
        Token funcToken = consume(TokenType.FUNC, "Expected 'func'");
        Token nameToken = consume(TokenType.IDENTIFIER, "Expected function name");
        
        String name = nameToken != null ? nameToken.getValue() : "";
        
        // Parameters
        consume(TokenType.LPAREN, "Expected '(' after function name");
        List<FuncDeclNode.Parameter> parameters = new ArrayList<>();
        
        if (!check(TokenType.RPAREN)) {
            do {
                Token paramName = consume(TokenType.IDENTIFIER, "Expected parameter name");
                consume(TokenType.COLON, "Expected ':' after parameter name");
                String paramType = parseType();
                
                if (paramName != null) {
                    parameters.add(new FuncDeclNode.Parameter(paramName.getValue(), paramType));
                }
                
                if (check(TokenType.COMMA)) {
                    advance();
                } else {
                    break;
                }
            } while (true);
        }
        
        consume(TokenType.RPAREN, "Expected ')' after parameters");
        
        // Return type
        String returnType = "void";
        if (check(TokenType.ARROW)) {
            advance();
            returnType = parseType();
        }
        
        // Body
        consume(TokenType.COLON, "Expected ':' after function signature");
        skipNewlines();
        consume(TokenType.INDENT, "Expected indented block after ':'");
        
        List<ASTNode> body = parseBlock();
        
        int line = funcToken != null ? funcToken.getLine() : 0;
        int column = funcToken != null ? funcToken.getColumn() : 0;
        
        return new FuncDeclNode(name, parameters, returnType, body, line, column);
    }
    
    /**
     * Parse if statement
     * if condition:
     *     statements
     */
    private IfNode parseIfStmt() {
        Token ifToken = consume(TokenType.IF, "Expected 'if'");
        ASTNode condition = parseExpression();
        consume(TokenType.COLON, "Expected ':' after if condition");
        skipNewlines();
        consume(TokenType.INDENT, "Expected indented block after ':'");
        
        List<ASTNode> thenBlock = parseBlock();
        
        int line = ifToken != null ? ifToken.getLine() : 0;
        int column = ifToken != null ? ifToken.getColumn() : 0;
        
        IfNode ifNode = new IfNode(condition, thenBlock, line, column);
        
        // Elif branches
        while (check(TokenType.ELIF)) {
            advance();
            ASTNode elifCondition = parseExpression();
            consume(TokenType.COLON, "Expected ':' after elif condition");
            skipNewlines();
            consume(TokenType.INDENT, "Expected indented block after ':'");
            List<ASTNode> elifBlock = parseBlock();
            ifNode.addElifBranch(elifCondition, elifBlock);
        }
        
        // Else branch
        if (check(TokenType.ELSE)) {
            advance();
            consume(TokenType.COLON, "Expected ':' after else");
            skipNewlines();
            consume(TokenType.INDENT, "Expected indented block after ':'");
            List<ASTNode> elseBlock = parseBlock();
            ifNode.setElseBlock(elseBlock);
        }
        
        return ifNode;
    }
    
    /**
     * Parse while statement
     * while condition:
     *     statements
     */
    private WhileNode parseWhileStmt() {
        Token whileToken = consume(TokenType.WHILE, "Expected 'while'");
        ASTNode condition = parseExpression();
        consume(TokenType.COLON, "Expected ':' after while condition");
        skipNewlines();
        consume(TokenType.INDENT, "Expected indented block after ':'");
        
        List<ASTNode> body = parseBlock();
        
        int line = whileToken != null ? whileToken.getLine() : 0;
        int column = whileToken != null ? whileToken.getColumn() : 0;
        
        return new WhileNode(condition, body, line, column);
    }
    
    /**
     * Parse for statement
     * for item in items:
     *     statements
     */
    private ForNode parseForStmt() {
        Token forToken = consume(TokenType.FOR, "Expected 'for'");
        Token varToken = consume(TokenType.IDENTIFIER, "Expected variable name");
        consume(TokenType.IN, "Expected 'in' after for variable");
        ASTNode iterable = parseExpression();
        consume(TokenType.COLON, "Expected ':' after for clause");
        skipNewlines();
        consume(TokenType.INDENT, "Expected indented block after ':'");
        
        List<ASTNode> body = parseBlock();
        
        String variable = varToken != null ? varToken.getValue() : "";
        int line = forToken != null ? forToken.getLine() : 0;
        int column = forToken != null ? forToken.getColumn() : 0;
        
        return new ForNode(variable, iterable, body, line, column);
    }
    
    /**
     * Parse return statement
     * return expression
     */
    private ReturnNode parseReturnStmt() {
        Token returnToken = consume(TokenType.RETURN, "Expected 'return'");
        
        ASTNode value = null;
        if (!check(TokenType.NEWLINE) && !check(TokenType.EOF) && !check(TokenType.DEDENT)) {
            value = parseExpression();
        }
        
        int line = returnToken != null ? returnToken.getLine() : 0;
        int column = returnToken != null ? returnToken.getColumn() : 0;
        
        return new ReturnNode(value, line, column);
    }
    
    /**
     * Parse expression statement
     */
    private ASTNode parseExpressionStmt() {
        return parseExpression();
    }
    
    /**
     * Parse a block of statements (indented)
     * @return List of statements
     */
    private List<ASTNode> parseBlock() {
        List<ASTNode> statements = new ArrayList<>();
        
        skipNewlines();
        
        while (!check(TokenType.DEDENT) && !check(TokenType.EOF) && currentToken != null) {
            ASTNode stmt = parseStatement();
            if (stmt != null) {
                statements.add(stmt);
            }
            skipNewlines();
        }
        
        if (check(TokenType.DEDENT)) {
            advance();
        }
        
        return statements;
    }
    
    /**
     * Parse type annotation
     * @return Type string
     */
    private String parseType() {
        if (checkAny(TokenType.INT, TokenType.FLOAT, TokenType.STRING_TYPE, 
                     TokenType.BOOL, TokenType.VOID)) {
            Token typeToken = currentToken;
            advance();
            return typeToken.getValue().isEmpty() ? typeToken.getType().toString().toLowerCase() : typeToken.getValue();
        } else if (check(TokenType.ARRAY)) {
            advance();
            consume(TokenType.LESS, "Expected '<' after 'array'");
            String elementType = parseType();
            consume(TokenType.GREATER, "Expected '>' after array element type");
            return "array<" + elementType + ">";
        } else if (check(TokenType.OBJECT)) {
            advance();
            return "object";
        } else {
            error("Expected type");
            return "error";
        }
    }
    
    // Expression parsing with operator precedence
    
    /**
     * Parse expression
     * @return Expression AST node
     */
    private ASTNode parseExpression() {
        return parseLogicalOr();
    }
    
    /**
     * Parse logical OR expression
     */
    private ASTNode parseLogicalOr() {
        ASTNode left = parseLogicalAnd();
        
        while (check(TokenType.OR)) {
            Token op = currentToken;
            advance();
            ASTNode right = parseLogicalAnd();
            left = new BinaryOpNode("or", left, right, op.getLine(), op.getColumn());
        }
        
        return left;
    }
    
    /**
     * Parse logical AND expression
     */
    private ASTNode parseLogicalAnd() {
        ASTNode left = parseEquality();
        
        while (check(TokenType.AND)) {
            Token op = currentToken;
            advance();
            ASTNode right = parseEquality();
            left = new BinaryOpNode("and", left, right, op.getLine(), op.getColumn());
        }
        
        return left;
    }
    
    /**
     * Parse equality expression (== !=)
     */
    private ASTNode parseEquality() {
        ASTNode left = parseComparison();
        
        while (checkAny(TokenType.EQUAL_EQUAL, TokenType.NOT_EQUAL)) {
            Token op = currentToken;
            String operator = op.is(TokenType.EQUAL_EQUAL) ? "==" : "!=";
            advance();
            ASTNode right = parseComparison();
            left = new BinaryOpNode(operator, left, right, op.getLine(), op.getColumn());
        }
        
        return left;
    }
    
    /**
     * Parse comparison expression (< > <= >=)
     */
    private ASTNode parseComparison() {
        ASTNode left = parseTerm();
        
        while (checkAny(TokenType.LESS, TokenType.GREATER, TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL)) {
            Token op = currentToken;
            String operator;
            if (op.is(TokenType.LESS)) operator = "<";
            else if (op.is(TokenType.GREATER)) operator = ">";
            else if (op.is(TokenType.LESS_EQUAL)) operator = "<=";
            else operator = ">=";
            
            advance();
            ASTNode right = parseTerm();
            left = new BinaryOpNode(operator, left, right, op.getLine(), op.getColumn());
        }
        
        return left;
    }
    
    /**
     * Parse term expression (+ -)
     */
    private ASTNode parseTerm() {
        ASTNode left = parseFactor();
        
        while (checkAny(TokenType.PLUS, TokenType.MINUS)) {
            Token op = currentToken;
            String operator = op.is(TokenType.PLUS) ? "+" : "-";
            advance();
            ASTNode right = parseFactor();
            left = new BinaryOpNode(operator, left, right, op.getLine(), op.getColumn());
        }
        
        return left;
    }
    
    /**
     * Parse factor expression (* / %)
     */
    private ASTNode parseFactor() {
        ASTNode left = parseUnary();
        
        while (checkAny(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT)) {
            Token op = currentToken;
            String operator;
            if (op.is(TokenType.STAR)) operator = "*";
            else if (op.is(TokenType.SLASH)) operator = "/";
            else operator = "%";
            
            advance();
            ASTNode right = parseUnary();
            left = new BinaryOpNode(operator, left, right, op.getLine(), op.getColumn());
        }
        
        return left;
    }
    
    /**
     * Parse unary expression (not, -)
     */
    private ASTNode parseUnary() {
        if (checkAny(TokenType.NOT, TokenType.MINUS)) {
            Token op = currentToken;
            String operator = op.is(TokenType.NOT) ? "not" : "-";
            advance();
            ASTNode operand = parseUnary();
            return new UnaryOpNode(operator, operand, op.getLine(), op.getColumn());
        }
        
        return parseCall();
    }
    
    /**
     * Parse call, array access, and member access
     */
    private ASTNode parseCall() {
        ASTNode expr = parsePrimary();
        
        while (true) {
            if (check(TokenType.LPAREN)) {
                // Function call
                Token lparen = currentToken;
                advance();
                List<ASTNode> arguments = new ArrayList<>();
                
                if (!check(TokenType.RPAREN)) {
                    do {
                        arguments.add(parseExpression());
                        if (check(TokenType.COMMA)) {
                            advance();
                        } else {
                            break;
                        }
                    } while (true);
                }
                
                consume(TokenType.RPAREN, "Expected ')' after arguments");
                
                // Extract function name from identifier node
                String funcName = "";
                if (expr instanceof IdentifierNode) {
                    funcName = ((IdentifierNode) expr).getName();
                }
                
                expr = new CallNode(funcName, arguments, lparen.getLine(), lparen.getColumn());
                
            } else if (check(TokenType.LBRACKET)) {
                // Array access
                Token lbracket = currentToken;
                advance();
                ASTNode index = parseExpression();
                consume(TokenType.RBRACKET, "Expected ']' after array index");
                expr = new ArrayAccessNode(expr, index, lbracket.getLine(), lbracket.getColumn());
                
            } else if (check(TokenType.DOT)) {
                // Member access
                Token dot = currentToken;
                advance();
                Token member = consume(TokenType.IDENTIFIER, "Expected member name after '.'");
                String memberName = member != null ? member.getValue() : "";
                expr = new MemberAccessNode(expr, memberName, dot.getLine(), dot.getColumn());
                
            } else {
                break;
            }
        }
        
        return expr;
    }
    
    /**
     * Parse primary expression (literals, identifiers, parentheses)
     */
    private ASTNode parsePrimary() {
        // Number literal
        if (check(TokenType.NUMBER)) {
            Token token = currentToken;
            advance();
            return new NumberLiteralNode(token.getValue(), token.getLine(), token.getColumn());
        }
        
        // String literal
        if (check(TokenType.STRING)) {
            Token token = currentToken;
            advance();
            return new StringLiteralNode(token.getValue(), token.getLine(), token.getColumn());
        }
        
        // Boolean literals
        if (check(TokenType.TRUE)) {
            Token token = currentToken;
            advance();
            return new BooleanLiteralNode(true, token.getLine(), token.getColumn());
        }
        if (check(TokenType.FALSE)) {
            Token token = currentToken;
            advance();
            return new BooleanLiteralNode(false, token.getLine(), token.getColumn());
        }
        
        // Identifier
        if (check(TokenType.IDENTIFIER) || check(TokenType.PRINT) || check(TokenType.RANGE)) {
            Token token = currentToken;
            advance();
            String name = token.getValue().isEmpty() ? token.getType().toString().toLowerCase() : token.getValue();
            return new IdentifierNode(name, token.getLine(), token.getColumn());
        }
        
        // Array literal
        if (check(TokenType.LBRACKET)) {
            Token lbracket = currentToken;
            advance();
            List<ASTNode> elements = new ArrayList<>();
            
            if (!check(TokenType.RBRACKET)) {
                do {
                    elements.add(parseExpression());
                    if (check(TokenType.COMMA)) {
                        advance();
                    } else {
                        break;
                    }
                } while (true);
            }
            
            consume(TokenType.RBRACKET, "Expected ']' after array elements");
            return new ArrayLiteralNode(elements, lbracket.getLine(), lbracket.getColumn());
        }
        
        // Object literal
        if (check(TokenType.LBRACE)) {
            Token lbrace = currentToken;
            advance();
            Map<String, ASTNode> fields = new LinkedHashMap<>();
            
            if (!check(TokenType.RBRACE)) {
                do {
                    Token key = consume(TokenType.IDENTIFIER, "Expected field name");
                    consume(TokenType.COLON, "Expected ':' after field name");
                    ASTNode value = parseExpression();
                    
                    if (key != null) {
                        fields.put(key.getValue(), value);
                    }
                    
                    if (check(TokenType.COMMA)) {
                        advance();
                    } else {
                        break;
                    }
                } while (true);
            }
            
            consume(TokenType.RBRACE, "Expected '}' after object fields");
            return new ObjectLiteralNode(fields, lbrace.getLine(), lbrace.getColumn());
        }
        
        // Parenthesized expression
        if (check(TokenType.LPAREN)) {
            advance();
            ASTNode expr = parseExpression();
            consume(TokenType.RPAREN, "Expected ')' after expression");
            return expr;
        }
        
        error("Expected expression");
        return new NumberLiteralNode("0", 0, 0); // Error recovery
    }
    
    /**
     * Get parse errors
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
