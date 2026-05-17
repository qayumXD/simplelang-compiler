# LAB_Assignment02_SimpleLang Compiler
## SimpleLang Compiler - Implementation/Interpretation Guide

## Name: Muhammad Abdul Qayyoum
# Reg # SP23-BCS-033


A complete compiler implementation for **SimpleLang** - a modern, statically-typed programming language with Python-like syntax.
##### Drive link For Video Demonstration:https://drive.google.com/file/d/1ysQUBm6XQFESAlau2BQwi9HV-X0uyWXS/view?usp=sharing
##### Github link to SourceCode:https://github.com/qayumXD/simplelang-compiler

Detailed guide to understanding and extending the SimpleLang compiler implementation.

---

## 📋 Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Phase 1: Lexical Analysis](#phase-1-lexical-analysis)
3. [Phase 2: Syntax Analysis](#phase-2-syntax-analysis)
4. [Phase 3: Semantic Analysis](#phase-3-semantic-analysis)
5. [GUI Implementation](#gui-implementation)
6. [Testing Strategy](#testing-strategy)
7. [Extension Points](#extension-points)

---













## 🏗️ Architecture Overview

### Component Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    Main.java                            │
│              (Entry Point)                              │
└────────────────┬────────────────────────────────────────┘
                 │
        ┌────────┴────────┐
        │                 │
        ▼                 ▼
    ┌────────┐        ┌──────────┐
    │ Lexer  │        │CompilerGUI│
    └────┬───┘        └──────────┘
         │
         ▼
    ┌────────┐
    │ Parser │
    └────┬───┘
         │
         ▼
    ┌──────────────┐
    │SemanticAnalyzer│
    └────┬─────────┘
         │
         ▼
    ┌──────────────┐
    │ SymbolTable  │
    └──────────────┘
```

### Class Hierarchy

```
ASTNode (abstract)
├── ProgramNode
├── VarDeclNode
├── AssignmentNode
├── FuncDeclNode
├── IfNode
├── WhileNode
├── ForNode
├── ReturnNode
├── BinaryOpNode
├── UnaryOpNode
├── CallNode
├── ArrayAccessNode
├── MemberAccessNode
├── NumberLiteralNode
├── StringLiteralNode
├── BooleanLiteralNode
├── IdentifierNode
├── ArrayLiteralNode
└── ObjectLiteralNode
```

---

## 🔤 Phase 1: Lexical Analysis

### File: `Lexer.java` (380 lines)

### Key Methods

```java
public List<Token> tokenize()
    // Main entry point - tokenizes entire source
    
private void advance()
    // Move to next character
    
private void handleIndentation()
    // Process Python-style indentation
    
private Token readNumber()
    // Parse numeric literals
    
private Token readString()
    // Parse string literals with escape sequences
    
private Token readIdentifier()
    // Parse identifiers and keywords
    
private Token getNextToken()
    // Get next token from source
```

### State Management

```java
private String source;           // Source code
private int position;            // Current position
private int line;                // Current line
private int column;              // Current column
private char currentChar;        // Current character
private List<Token> tokens;      // Output tokens
private Stack<Integer> indentStack;  // Indentation levels
private int currentIndent;       // Current indentation
private boolean atLineStart;     // At start of line?
private List<String> errors;     // Error messages
```

### Indentation Algorithm

```
1. At line start, count leading spaces
2. If indent > previous:
   - Push to stack
   - Generate INDENT token
3. If indent < previous:
   - Pop from stack (multiple times if needed)
   - Generate DEDENT tokens for each pop
4. If indent == previous:
   - No action
```

### Token Recognition

```
Character Type          → Token Type
─────────────────────────────────────
Digit                   → NUMBER
Letter/Underscore       → IDENTIFIER or KEYWORD
"                       → STRING
#                       → Comment (skip)
+, -, *, /, %          → Arithmetic operators
=, ==, !=, <, >, etc.  → Comparison/assignment
(, ), [, ], {, }       → Delimiters
:, ,, .                → Special delimiters
```

### Error Handling

```java
// Lexical errors
- Unexpected character
- Unterminated string
- Invalid number format
- Indentation error
```

---

## 🌳 Phase 2: Syntax Analysis

### Files: `Parser.java` (580 lines), `ASTNode.java` (650 lines)

### Parser Methods

```java
public ProgramNode parse()
    // Parse entire program
    
private ASTNode parseStatement()
    // Parse single statement
    
private ASTNode parseExpression()
    // Parse expression (entry point)
    
private ASTNode parseLogicalOr()
    // Parse OR expressions
    
private ASTNode parseLogicalAnd()
    // Parse AND expressions
    
private ASTNode parseEquality()
    // Parse == and !=
    
private ASTNode parseComparison()
    // Parse <, >, <=, >=
    
private ASTNode parseTerm()
    // Parse + and -
    
private ASTNode parseFactor()
    // Parse *, /, %
    
private ASTNode parseUnary()
    // Parse unary operators
    
private ASTNode parseCall()
    // Parse function calls and array access
    
private ASTNode parsePrimary()
    // Parse literals and identifiers
```

### Operator Precedence (Lowest to Highest)

```
1. Logical OR (or)
2. Logical AND (and)
3. Equality (==, !=)
4. Comparison (<, >, <=, >=)
5. Term (+, -)
6. Factor (*, /, %)
7. Unary (not, -)
8. Call (function calls, array access, member access)
9. Primary (literals, identifiers, parentheses)
```

### Recursive Descent Pattern

```java
private ASTNode parseExpression() {
    return parseLogicalOr();
}

private ASTNode parseLogicalOr() {
    ASTNode left = parseLogicalAnd();
    
    while (check(TokenType.OR)) {
        Token op = currentToken;
        advance();
        ASTNode right = parseLogicalAnd();
        left = new BinaryOpNode("or", left, right, ...);
    }
    
    return left;
}
```

### AST Node Types

```
Statement Nodes:
- ProgramNode: Root of AST
- VarDeclNode: Variable declaration
- AssignmentNode: Variable assignment
- FuncDeclNode: Function declaration
- IfNode: If/elif/else statement
- WhileNode: While loop
- ForNode: For loop
- ReturnNode: Return statement

Expression Nodes:
- BinaryOpNode: Binary operations
- UnaryOpNode: Unary operations
- CallNode: Function calls
- ArrayAccessNode: Array indexing
- MemberAccessNode: Object member access

Literal Nodes:
- NumberLiteralNode: Numbers
- StringLiteralNode: Strings
- BooleanLiteralNode: Booleans
- IdentifierNode: Variables
- ArrayLiteralNode: Arrays
- ObjectLiteralNode: Objects
```

### Error Recovery

```java
try {
    ASTNode stmt = parseStatement();
    if (stmt != null) {
        program.addStatement(stmt);
    }
} catch (Exception e) {
    error("Unexpected error: " + e.getMessage());
    // Skip to next statement
    while (currentToken != null && 
           !check(TokenType.NEWLINE) && 
           !check(TokenType.EOF)) {
        advance();
    }
}
```

---

## 🔍 Phase 3: Semantic Analysis

### Files: `SemanticAnalyzer.java` (480 lines), `SymbolTable.java` (280 lines)

### Semantic Analyzer Methods

```java
public boolean analyze(ProgramNode program)
    // Main analysis entry point
    
private void collectFunctionDeclaration(FuncDeclNode node)
    // First pass: collect function signatures
    
private void analyzeStatement(ASTNode stmt)
    // Analyze single statement
    
private String analyzeExpression(ASTNode expr)
    // Analyze expression and return type
    
private String analyzeBinaryOp(BinaryOpNode node)
    // Type check binary operations
    
private String analyzeCall(CallNode node)
    // Validate function calls
```

### Symbol Table Methods

```java
public void enterScope(String name)
    // Enter new scope
    
public void exitScope()
    // Exit current scope
    
public boolean defineVariable(String name, String type, int line, int column)
    // Define variable in current scope
    
public Symbol resolve(String name)
    // Resolve variable (searches all scopes)
    
public FunctionSignature resolveFunction(String name)
    // Resolve function
```

### Type Checking Rules

```
Arithmetic Operators (+, -, *, /, %):
  - Operands must be numeric (int or float)
  - Result: int if both int, float otherwise
  - Exception: + supports string concatenation

Comparison Operators (<, >, <=, >=):
  - Operands must be numeric
  - Result: bool

Equality Operators (==, !=):
  - Operands must be compatible types
  - Result: bool

Logical Operators (and, or):
  - Operands must be bool
  - Result: bool

Unary Operators:
  - not: operand must be bool, result bool
  - -: operand must be numeric, result same type

Function Calls:
  - Argument count must match parameter count
  - Argument types must match parameter types
  - Return type from function signature

Array Access:
  - Array must be array type
  - Index must be int
  - Result: element type

Variable Assignment:
  - Variable must be declared
  - Value type must match variable type
```

### Scope Management

```
Global Scope
├── Function: print
├── Function: range
├── Function: factorial
│   └── Local Scope (function body)
│       └── Parameter: n (int)
│       └── Local Scope (if block)
│           └── (no new variables)
│       └── Local Scope (else block)
│           └── (no new variables)
└── Function: main
    └── Local Scope (function body)
        └── Variable: num (int)
        └── Variable: result (int)
```

### Type Inference

```
Expression                  → Inferred Type
────────────────────────────────────────
10                          → int
3.14                        → float
"hello"                     → string
true                        → bool
[1, 2, 3]                   → array<int>
x + y (where x,y: int)      → int
x + y (where x: string)     → string
factorial(5)                → int (from signature)
```

---

## 🎨 GUI Implementation

### File: `CompilerGUI.java` (420 lines)

### Main Components

```java
JTextArea codeEditor
    // Dark-themed code editor with line numbers
    
JTextArea tokensArea
    // Display lexical analysis results
    
JTextArea astArea
    // Display syntax tree
    
JTextArea symbolsArea
    // Display symbol table
    
JButton compileButton
    // Trigger compilation
    
JButton loadButton
    // Load .sl file
    
JButton saveButton
    // Save current code
```

### Layout Structure

```
┌─────────────────────────────────────────────────┐
│              Header (Title)                     │
├──────────────────┬──────────────────────────────┤
│                  │                              │
│   Code Editor    │   Tokens Panel               │
│   (Dark Theme)   ├──────────────────────────────┤
│   + Line Numbers │   AST Panel                  │
│                  ├──────────────────────────────┤
│                  │   Symbols Panel              │
├──────────────────┴──────────────────────────────┤
│  [Compile] [Clear] [Load] [Save]                │
├──────────────────────────────────────────────────┤
│  Status: Ready                                   │
└──────────────────────────────────────────────────┘
```

### Compilation Flow in GUI

```java
private void compileCode() {
    // 1. Get code from editor
    String code = codeEditor.getText();
    
    // 2. Phase 1: Lexical Analysis
    Lexer lexer = new Lexer(code);
    List<Token> tokens = lexer.tokenize();
    displayTokens(tokens);
    
    // 3. Phase 2: Syntax Analysis
    Parser parser = new Parser(tokens);
    ProgramNode ast = parser.parse();
    displayAST(ast);
    
    // 4. Phase 3: Semantic Analysis
    SemanticAnalyzer analyzer = new SemanticAnalyzer();
    boolean success = analyzer.analyze(ast);
    displaySymbolTable(analyzer.getSymbolTable());
    
    // 5. Update status
    updateStatus("✓ Compilation successful!", GREEN);
}
```

### Font Configuration

```java
// Header: 36pt bold
new Font("Arial", Font.BOLD, 36)

// Code editor: 18pt monospace
new Font("Courier New", Font.PLAIN, 18)

// Output panels: 16pt monospace
new Font("Courier New", Font.PLAIN, 16)

// Buttons: 20pt bold
new Font("Arial", Font.BOLD, 20)

// Status: 20pt plain
new Font("Arial", Font.PLAIN, 20)
```

---

## 🧪 Testing Strategy

### Test Files

```
examples/
├── hello.sl          # Basic function and print
├── factorial.sl      # Recursion and control flow
└── variables.sl      # Type inference and operators
```

### Test Coverage

| Feature | Test File | Status |
|---------|-----------|--------|
| Functions | factorial.sl | ✅ |
| Recursion | factorial.sl | ✅ |
| Control Flow | factorial.sl | ✅ |
| Type Inference | variables.sl | ✅ |
| String Concatenation | variables.sl | ✅ |
| Multiple Types | variables.sl | ✅ |
| Print Function | all | ✅ |

### Running Tests

```bash
# Run all tests
./test.sh

# Run single test
java -cp bin Main examples/factorial.sl

# Show AST
java -Dshow.ast=true -cp bin Main examples/factorial.sl

# Show tokens
java -Dshow.tokens=true -cp bin Main examples/factorial.sl
```

---

## 🔧 Extension Points

### Adding New Token Types

1. Add to `TokenType.java`:
```java
public enum TokenType {
    // ... existing types
    NEW_TOKEN,  // Add here
}
```

2. Update `Lexer.java`:
```java
private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
static {
    // ... existing keywords
    KEYWORDS.put("newkeyword", TokenType.NEW_TOKEN);
}
```

### Adding New AST Node Types

1. Create class in `ASTNode.java`:
```java
class NewNodeType extends ASTNode {
    private String field1;
    private ASTNode field2;
    
    public NewNodeType(String field1, ASTNode field2, int line, int column) {
        super(line, column);
        this.field1 = field1;
        this.field2 = field2;
    }
    
    @Override
    public String toString() {
        return "NewNode(" + field1 + ")";
    }
    
    @Override
    public String toTreeString(int indent) {
        // Implementation
    }
}
```

2. Update `Parser.java`:
```java
private ASTNode parseNewConstruct() {
    // Parsing logic
    return new NewNodeType(...);
}
```

3. Update `SemanticAnalyzer.java`:
```java
private void analyzeStatement(ASTNode stmt) {
    // ... existing cases
    else if (stmt instanceof NewNodeType) {
        analyzeNewNode((NewNodeType) stmt);
    }
}

private void analyzeNewNode(NewNodeType node) {
    // Type checking logic
}
```

### Adding New Built-in Functions

1. Update `SymbolTable.java`:
```java
private void addBuiltInFunctions() {
    // ... existing functions
    List<String> newParams = new ArrayList<>();
    newParams.add("int");
    functions.put("newfunction", 
        new FunctionSignature("newfunction", newParams, "int", 0, 0));
}
```

2. Update `SemanticAnalyzer.java`:
```java
private String analyzeCall(CallNode node) {
    // ... existing code
    if (node.getFunctionName().equals("newfunction")) {
        // Special handling if needed
    }
}
```

### Adding New Operators

1. Add to `TokenType.java`:
```java
NEW_OPERATOR,  // Add token type
```

2. Update `Lexer.java`:
```java
case '~': token = new Token(TokenType.NEW_OPERATOR, ...); break;
```

3. Update `Parser.java`:
```java
private ASTNode parseNewLevel() {
    ASTNode left = parseNextLevel();
    while (check(TokenType.NEW_OPERATOR)) {
        // Parsing logic
    }
    return left;
}
```

4. Update `SemanticAnalyzer.java`:
```java
if (op.equals("~")) {
    // Type checking logic
}
```

---

## 📊 Performance Considerations

### Optimization Opportunities

1. **Lexer Optimization**
   - Use character classification arrays instead of method calls
   - Pre-compile keyword map

2. **Parser Optimization**
   - Memoization for recursive descent
   - Token lookahead caching

3. **Semantic Analysis Optimization**
   - Single-pass analysis instead of two passes
   - Lazy type inference

### Current Performance

```
File Size       Tokens  Parse Time  Total Time
─────────────────────────────────────────────
hello.sl        22      <10ms       <50ms
factorial.sl    84      <20ms       <100ms
variables.sl    95      <20ms       <100ms
```

---

## 🐛 Debugging Tips

### Enable Debug Output

```java
// In Lexer.java
System.out.println("Token: " + token);

// In Parser.java
System.out.println("Parsing: " + currentToken);

// In SemanticAnalyzer.java
System.out.println("Type: " + type);
```

### Common Issues

| Issue | Cause | Solution |
|-------|-------|----------|
| Indentation errors | Inconsistent spacing | Use 4 spaces consistently |
| Parse errors | Missing colons | Add `:` after if/while/for/func |
| Type errors | Type mismatch | Check variable types |
| Undefined variable | Not declared | Declare before use |

---

## 📚 References

- **Lexer.java** - Lexical analysis
- **Parser.java** - Syntax analysis
- **SemanticAnalyzer.java** - Semantic analysis
- **SymbolTable.java** - Symbol management
- **ASTNode.java** - AST definitions
- **CompilerGUI.java** - GUI implementation
- **Main.java** - Entry point

---

**SimpleLang Compiler - Implementation Guide**  
Version: 1.0  
Date: May 17, 2026

*This guide provides detailed information for understanding and extending the SimpleLang compiler.*
