import java.util.*;

/**
 * Semantic Analyzer for SimpleLang
 * Performs type checking, scope resolution, and semantic validation
 */
public class SemanticAnalyzer {
    private SymbolTable symbolTable;
    private List<String> errors;
    private String currentFunctionReturnType;
    
    /**
     * Constructor
     */
    public SemanticAnalyzer() {
        this.symbolTable = new SymbolTable();
        this.errors = new ArrayList<>();
        this.currentFunctionReturnType = null;
    }
    
    /**
     * Analyze the program
     * @param program Program AST node
     * @return true if no errors
     */
    public boolean analyze(ProgramNode program) {
        errors.clear();
        symbolTable.reset();
        
        // First pass: collect function declarations
        for (ASTNode stmt : program.getStatements()) {
            if (stmt instanceof FuncDeclNode) {
                collectFunctionDeclaration((FuncDeclNode) stmt);
            }
        }
        
        // Second pass: analyze all statements
        for (ASTNode stmt : program.getStatements()) {
            analyzeStatement(stmt);
        }
        
        return !hasErrors();
    }
    
    /**
     * Collect function declaration (first pass)
     */
    private void collectFunctionDeclaration(FuncDeclNode node) {
        List<String> paramTypes = new ArrayList<>();
        for (FuncDeclNode.Parameter param : node.getParameters()) {
            paramTypes.add(param.type);
        }
        
        if (!symbolTable.defineFunction(node.getName(), paramTypes, node.getReturnType(), 
                                       node.getLine(), node.getColumn())) {
            error(String.format("Function '%s' already defined", node.getName()), 
                  node.getLine(), node.getColumn());
        }
    }
    
    /**
     * Analyze a statement
     */
    private void analyzeStatement(ASTNode stmt) {
        if (stmt instanceof VarDeclNode) {
            analyzeVarDecl((VarDeclNode) stmt);
        } else if (stmt instanceof AssignmentNode) {
            analyzeAssignment((AssignmentNode) stmt);
        } else if (stmt instanceof FuncDeclNode) {
            analyzeFuncDecl((FuncDeclNode) stmt);
        } else if (stmt instanceof IfNode) {
            analyzeIf((IfNode) stmt);
        } else if (stmt instanceof WhileNode) {
            analyzeWhile((WhileNode) stmt);
        } else if (stmt instanceof ForNode) {
            analyzeFor((ForNode) stmt);
        } else if (stmt instanceof ReturnNode) {
            analyzeReturn((ReturnNode) stmt);
        } else if (stmt != null) {
            // Expression statement
            analyzeExpression(stmt);
        }
    }
    
    /**
     * Analyze variable declaration
     */
    private void analyzeVarDecl(VarDeclNode node) {
        // Analyze initializer
        String initType = analyzeExpression(node.getInitializer());
        
        // Determine variable type
        String varType = node.getType();
        if (varType == null) {
            // Type inference
            varType = initType;
            node.setType(varType);
        } else {
            // Type checking
            if (!typesCompatible(varType, initType)) {
                error(String.format("Type mismatch: cannot assign %s to %s", initType, varType),
                      node.getLine(), node.getColumn());
            }
        }
        
        // Define variable in symbol table
        if (!symbolTable.defineVariable(node.getName(), varType, node.getLine(), node.getColumn())) {
            error(String.format("Variable '%s' already defined in current scope", node.getName()),
                  node.getLine(), node.getColumn());
        }
    }
    
    /**
     * Analyze assignment
     */
    private void analyzeAssignment(AssignmentNode node) {
        // Check if variable exists
        SymbolTable.Symbol symbol = symbolTable.resolve(node.getName());
        if (symbol == null) {
            error(String.format("Variable '%s' not declared", node.getName()),
                  node.getLine(), node.getColumn());
            return;
        }
        
        // Check if it's a function (can't assign to functions)
        if (symbol.getKind() == SymbolTable.SymbolKind.FUNCTION) {
            error(String.format("Cannot assign to function '%s'", node.getName()),
                  node.getLine(), node.getColumn());
            return;
        }
        
        // Type check
        String valueType = analyzeExpression(node.getValue());
        if (!typesCompatible(symbol.getType(), valueType)) {
            error(String.format("Type mismatch: cannot assign %s to %s", valueType, symbol.getType()),
                  node.getLine(), node.getColumn());
        }
    }
    
    /**
     * Analyze function declaration
     */
    private void analyzeFuncDecl(FuncDeclNode node) {
        // Enter function scope
        symbolTable.enterScope("func_" + node.getName());
        currentFunctionReturnType = node.getReturnType();
        
        // Define parameters
        for (FuncDeclNode.Parameter param : node.getParameters()) {
            if (!symbolTable.defineParameter(param.name, param.type, node.getLine(), node.getColumn())) {
                error(String.format("Parameter '%s' already defined", param.name),
                      node.getLine(), node.getColumn());
            }
        }
        
        // Analyze function body
        for (ASTNode stmt : node.getBody()) {
            analyzeStatement(stmt);
        }
        
        // Exit function scope
        symbolTable.exitScope();
        currentFunctionReturnType = null;
    }
    
    /**
     * Analyze if statement
     */
    private void analyzeIf(IfNode node) {
        // Check condition type
        String condType = analyzeExpression(node.getCondition());
        if (!condType.equals("bool")) {
            error(String.format("If condition must be bool, got %s", condType),
                  node.getLine(), node.getColumn());
        }
        
        // Analyze then block
        symbolTable.enterScope("if_then");
        for (ASTNode stmt : node.getThenBlock()) {
            analyzeStatement(stmt);
        }
        symbolTable.exitScope();
        
        // Analyze elif branches
        for (IfNode.ElifBranch elif : node.getElifBranches()) {
            String elifCondType = analyzeExpression(elif.condition);
            if (!elifCondType.equals("bool")) {
                error("Elif condition must be bool", node.getLine(), node.getColumn());
            }
            
            symbolTable.enterScope("elif");
            for (ASTNode stmt : elif.block) {
                analyzeStatement(stmt);
            }
            symbolTable.exitScope();
        }
        
        // Analyze else block
        if (!node.getElseBlock().isEmpty()) {
            symbolTable.enterScope("else");
            for (ASTNode stmt : node.getElseBlock()) {
                analyzeStatement(stmt);
            }
            symbolTable.exitScope();
        }
    }
    
    /**
     * Analyze while statement
     */
    private void analyzeWhile(WhileNode node) {
        // Check condition type
        String condType = analyzeExpression(node.getCondition());
        if (!condType.equals("bool")) {
            error(String.format("While condition must be bool, got %s", condType),
                  node.getLine(), node.getColumn());
        }
        
        // Analyze body
        symbolTable.enterScope("while");
        for (ASTNode stmt : node.getBody()) {
            analyzeStatement(stmt);
        }
        symbolTable.exitScope();
    }
    
    /**
     * Analyze for statement
     */
    private void analyzeFor(ForNode node) {
        // Analyze iterable
        String iterableType = analyzeExpression(node.getIterable());
        
        // Check if iterable is an array
        String elementType = "int"; // Default for range
        if (iterableType.startsWith("array<")) {
            elementType = iterableType.substring(6, iterableType.length() - 1);
        } else if (!iterableType.equals("array<int>")) {
            error(String.format("For loop requires array, got %s", iterableType),
                  node.getLine(), node.getColumn());
        }
        
        // Enter loop scope and define loop variable
        symbolTable.enterScope("for");
        symbolTable.defineVariable(node.getVariable(), elementType, node.getLine(), node.getColumn());
        
        // Analyze body
        for (ASTNode stmt : node.getBody()) {
            analyzeStatement(stmt);
        }
        
        symbolTable.exitScope();
    }
    
    /**
     * Analyze return statement
     */
    private void analyzeReturn(ReturnNode node) {
        if (currentFunctionReturnType == null) {
            error("Return statement outside function", node.getLine(), node.getColumn());
            return;
        }
        
        if (node.getValue() == null) {
            // Void return
            if (!currentFunctionReturnType.equals("void")) {
                error(String.format("Function must return %s", currentFunctionReturnType),
                      node.getLine(), node.getColumn());
            }
        } else {
            // Value return
            String returnType = analyzeExpression(node.getValue());
            if (!typesCompatible(currentFunctionReturnType, returnType)) {
                error(String.format("Return type mismatch: expected %s, got %s", 
                      currentFunctionReturnType, returnType),
                      node.getLine(), node.getColumn());
            }
        }
    }
    
    /**
     * Analyze expression and return its type
     */
    private String analyzeExpression(ASTNode expr) {
        if (expr instanceof NumberLiteralNode) {
            return ((NumberLiteralNode) expr).getType();
        } else if (expr instanceof StringLiteralNode) {
            return "string";
        } else if (expr instanceof BooleanLiteralNode) {
            return "bool";
        } else if (expr instanceof IdentifierNode) {
            return analyzeIdentifier((IdentifierNode) expr);
        } else if (expr instanceof BinaryOpNode) {
            return analyzeBinaryOp((BinaryOpNode) expr);
        } else if (expr instanceof UnaryOpNode) {
            return analyzeUnaryOp((UnaryOpNode) expr);
        } else if (expr instanceof CallNode) {
            return analyzeCall((CallNode) expr);
        } else if (expr instanceof ArrayAccessNode) {
            return analyzeArrayAccess((ArrayAccessNode) expr);
        } else if (expr instanceof MemberAccessNode) {
            return analyzeMemberAccess((MemberAccessNode) expr);
        } else if (expr instanceof ArrayLiteralNode) {
            return analyzeArrayLiteral((ArrayLiteralNode) expr);
        } else if (expr instanceof ObjectLiteralNode) {
            return "object";
        }
        
        return "error";
    }
    
    /**
     * Analyze identifier
     */
    private String analyzeIdentifier(IdentifierNode node) {
        SymbolTable.Symbol symbol = symbolTable.resolve(node.getName());
        if (symbol == null) {
            error(String.format("Variable '%s' not declared", node.getName()),
                  node.getLine(), node.getColumn());
            return "error";
        }
        
        node.setType(symbol.getType());
        return symbol.getType();
    }
    
    /**
     * Analyze binary operation
     */
    private String analyzeBinaryOp(BinaryOpNode node) {
        String leftType = analyzeExpression(node.getLeft());
        String rightType = analyzeExpression(node.getRight());
        String op = node.getOperator();
        
        // Addition operator: + (supports both numeric and string concatenation)
        if (op.equals("+")) {
            // String concatenation
            if (leftType.equals("string") || rightType.equals("string")) {
                node.setResultType("string");
                return "string";
            }
            // Numeric addition
            if (!isNumericType(leftType) || !isNumericType(rightType)) {
                error(String.format("Operator '+' requires numeric or string operands, got %s and %s",
                      leftType, rightType), node.getLine(), node.getColumn());
                return "error";
            }
            String resultType = (leftType.equals("float") || rightType.equals("float")) ? "float" : "int";
            node.setResultType(resultType);
            return resultType;
        }
        
        // Arithmetic operators: -, *, /, %
        if (op.equals("-") || op.equals("*") || op.equals("/") || op.equals("%")) {
            if (!isNumericType(leftType) || !isNumericType(rightType)) {
                error(String.format("Arithmetic operator '%s' requires numeric operands, got %s and %s",
                      op, leftType, rightType), node.getLine(), node.getColumn());
                return "error";
            }
            
            // Result type is float if either operand is float
            String resultType = (leftType.equals("float") || rightType.equals("float")) ? "float" : "int";
            node.setResultType(resultType);
            return resultType;
        }
        
        // Comparison operators: <, >, <=, >=
        if (op.equals("<") || op.equals(">") || op.equals("<=") || op.equals(">=")) {
            if (!isNumericType(leftType) || !isNumericType(rightType)) {
                error(String.format("Comparison operator '%s' requires numeric operands", op),
                      node.getLine(), node.getColumn());
                return "error";
            }
            node.setResultType("bool");
            return "bool";
        }
        
        // Equality operators: ==, !=
        if (op.equals("==") || op.equals("!=")) {
            if (!typesCompatible(leftType, rightType)) {
                error(String.format("Cannot compare %s and %s", leftType, rightType),
                      node.getLine(), node.getColumn());
            }
            node.setResultType("bool");
            return "bool";
        }
        
        // Logical operators: and, or
        if (op.equals("and") || op.equals("or")) {
            if (!leftType.equals("bool") || !rightType.equals("bool")) {
                error(String.format("Logical operator '%s' requires bool operands", op),
                      node.getLine(), node.getColumn());
                return "error";
            }
            node.setResultType("bool");
            return "bool";
        }
        
        return "error";
    }
    
    /**
     * Analyze unary operation
     */
    private String analyzeUnaryOp(UnaryOpNode node) {
        String operandType = analyzeExpression(node.getOperand());
        String op = node.getOperator();
        
        if (op.equals("not")) {
            if (!operandType.equals("bool")) {
                error(String.format("Operator 'not' requires bool operand, got %s", operandType),
                      node.getLine(), node.getColumn());
                return "error";
            }
            node.setResultType("bool");
            return "bool";
        }
        
        if (op.equals("-")) {
            if (!isNumericType(operandType)) {
                error(String.format("Unary minus requires numeric operand, got %s", operandType),
                      node.getLine(), node.getColumn());
                return "error";
            }
            node.setResultType(operandType);
            return operandType;
        }
        
        return "error";
    }
    
    /**
     * Analyze function call
     */
    private String analyzeCall(CallNode node) {
        SymbolTable.FunctionSignature func = symbolTable.resolveFunction(node.getFunctionName());
        if (func == null) {
            error(String.format("Function '%s' not declared", node.getFunctionName()),
                  node.getLine(), node.getColumn());
            return "error";
        }
        
        // Check argument count
        List<String> paramTypes = func.getParameterTypes();
        List<ASTNode> args = node.getArguments();
        
        if (args.size() != paramTypes.size()) {
            error(String.format("Function '%s' expects %d arguments, got %d",
                  node.getFunctionName(), paramTypes.size(), args.size()),
                  node.getLine(), node.getColumn());
            return func.getReturnType();
        }
        
        // Check argument types
        for (int i = 0; i < args.size(); i++) {
            String argType = analyzeExpression(args.get(i));
            String paramType = paramTypes.get(i);
            
            // Special case: print accepts any type
            if (node.getFunctionName().equals("print")) {
                continue;
            }
            
            if (!typesCompatible(paramType, argType)) {
                error(String.format("Argument %d type mismatch: expected %s, got %s",
                      i + 1, paramType, argType),
                      node.getLine(), node.getColumn());
            }
        }
        
        node.setReturnType(func.getReturnType());
        return func.getReturnType();
    }
    
    /**
     * Analyze array access
     */
    private String analyzeArrayAccess(ArrayAccessNode node) {
        String arrayType = analyzeExpression(node.getArray());
        String indexType = analyzeExpression(node.getIndex());
        
        // Check index type
        if (!indexType.equals("int")) {
            error(String.format("Array index must be int, got %s", indexType),
                  node.getLine(), node.getColumn());
        }
        
        // Extract element type
        if (arrayType.startsWith("array<")) {
            String elementType = arrayType.substring(6, arrayType.length() - 1);
            node.setElementType(elementType);
            return elementType;
        } else {
            error(String.format("Cannot index non-array type %s", arrayType),
                  node.getLine(), node.getColumn());
            return "error";
        }
    }
    
    /**
     * Analyze member access
     */
    private String analyzeMemberAccess(MemberAccessNode node) {
        String objectType = analyzeExpression(node.getObject());
        
        // For now, we only support .length() on arrays
        if (objectType.startsWith("array<") && node.getMember().equals("length")) {
            node.setMemberType("int");
            return "int";
        }
        
        // For objects, we'd need more sophisticated type tracking
        if (objectType.equals("object")) {
            node.setMemberType("any");
            return "any";
        }
        
        error(String.format("Type %s has no member '%s'", objectType, node.getMember()),
              node.getLine(), node.getColumn());
        return "error";
    }
    
    /**
     * Analyze array literal
     */
    private String analyzeArrayLiteral(ArrayLiteralNode node) {
        if (node.getElements().isEmpty()) {
            node.setElementType("any");
            return "array<any>";
        }
        
        // All elements must have the same type
        String firstType = analyzeExpression(node.getElements().get(0));
        for (int i = 1; i < node.getElements().size(); i++) {
            String elemType = analyzeExpression(node.getElements().get(i));
            if (!typesCompatible(firstType, elemType)) {
                error(String.format("Array elements must have same type, got %s and %s",
                      firstType, elemType), node.getLine(), node.getColumn());
            }
        }
        
        node.setElementType(firstType);
        return "array<" + firstType + ">";
    }
    
    /**
     * Check if two types are compatible
     */
    private boolean typesCompatible(String expected, String actual) {
        if (expected.equals(actual)) {
            return true;
        }
        
        // int can be promoted to float
        if (expected.equals("float") && actual.equals("int")) {
            return true;
        }
        
        // any type accepts anything
        if (expected.equals("any") || actual.equals("any")) {
            return true;
        }
        
        // error type is compatible with anything (for error recovery)
        if (expected.equals("error") || actual.equals("error")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if type is numeric
     */
    private boolean isNumericType(String type) {
        return type.equals("int") || type.equals("float");
    }
    
    /**
     * Report a semantic error
     */
    private void error(String message, int line, int column) {
        errors.add(String.format("Semantic error at line %d, column %d: %s", line, column, message));
    }
    
    /**
     * Get semantic errors
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
    
    /**
     * Get the symbol table
     * @return Symbol table
     */
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
}
