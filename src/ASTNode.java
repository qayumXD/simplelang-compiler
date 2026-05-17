import java.util.*;

/**
 * Abstract Syntax Tree Node classes for SimpleLang
 * Represents the hierarchical structure of the program
 */

// Base class for all AST nodes
abstract class ASTNode {
    protected int line;
    protected int column;
    
    public ASTNode(int line, int column) {
        this.line = line;
        this.column = column;
    }
    
    public int getLine() { return line; }
    public int getColumn() { return column; }
    
    public abstract String toString();
    public abstract String toTreeString(int indent);
    
    protected String getIndent(int level) {
        return "  ".repeat(level);
    }
}

// Program node (root of AST)
class ProgramNode extends ASTNode {
    private List<ASTNode> statements;
    
    public ProgramNode() {
        super(0, 0);
        this.statements = new ArrayList<>();
    }
    
    public void addStatement(ASTNode stmt) {
        statements.add(stmt);
    }
    
    public List<ASTNode> getStatements() {
        return statements;
    }
    
    @Override
    public String toString() {
        return "Program(" + statements.size() + " statements)";
    }
    
    @Override
    public String toTreeString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent(indent)).append("Program\n");
        for (ASTNode stmt : statements) {
            sb.append(stmt.toTreeString(indent + 1));
        }
        return sb.toString();
    }
}

// Variable declaration node
class VarDeclNode extends ASTNode {
    private String name;
    private String type; // Can be null for type inference
    private ASTNode initializer;
    
    public VarDeclNode(String name, String type, ASTNode initializer, int line, int column) {
        super(line, column);
        this.name = name;
        this.type = type;
        this.initializer = initializer;
    }
    
    public String getName() { return name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public ASTNode getInitializer() { return initializer; }
    
    @Override
    public String toString() {
        return "VarDecl(" + name + ": " + (type != null ? type : "inferred") + ")";
    }
    
    @Override
    public String toTreeString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent(indent)).append("VarDecl: ").append(name);
        if (type != null) {
            sb.append(" : ").append(type);
        }
        sb.append("\n");
        if (initializer != null) {
            sb.append(initializer.toTreeString(indent + 1));
        }
        return sb.toString();
    }
}

// Assignment node
class AssignmentNode extends ASTNode {
    private String name;
    private ASTNode value;
    
    public AssignmentNode(String name, ASTNode value, int line, int column) {
        super(line, column);
        this.name = name;
        this.value = value;
    }
    
    public String getName() { return name; }
    public ASTNode getValue() { return value; }
    
    @Override
    public String toString() {
        return "Assignment(" + name + " = ...)";
    }
    
    @Override
    public String toTreeString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent(indent)).append("Assignment: ").append(name).append("\n");
        sb.append(value.toTreeString(indent + 1));
        return sb.toString();
    }
}

// Function declaration node
class FuncDeclNode extends ASTNode {
    private String name;
    private List<Parameter> parameters;
    private String returnType;
    private List<ASTNode> body;
    
    public FuncDeclNode(String name, List<Parameter> parameters, String returnType, 
                        List<ASTNode> body, int line, int column) {
        super(line, column);
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.body = body;
    }
    
    public String getName() { return name; }
    public List<Parameter> getParameters() { return parameters; }
    public String getReturnType() { return returnType; }
    public List<ASTNode> getBody() { return body; }
    
    @Override
    public String toString() {
        return "FuncDecl(" + name + ", " + parameters.size() + " params)";
    }
    
    @Override
    public String toTreeString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent(indent)).append("FuncDecl: ").append(name);
        sb.append(" -> ").append(returnType).append("\n");
        
        if (!parameters.isEmpty()) {
            sb.append(getIndent(indent + 1)).append("Parameters:\n");
            for (Parameter param : parameters) {
                sb.append(getIndent(indent + 2)).append(param.name).append(": ")
                  .append(param.type).append("\n");
            }
        }
        
        sb.append(getIndent(indent + 1)).append("Body:\n");
        for (ASTNode stmt : body) {
            sb.append(stmt.toTreeString(indent + 2));
        }
        return sb.toString();
    }
    
    public static class Parameter {
        public String name;
        public String type;
        
        public Parameter(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }
}

// If statement node
class IfNode extends ASTNode {
    private ASTNode condition;
    private List<ASTNode> thenBlock;
    private List<ElifBranch> elifBranches;
    private List<ASTNode> elseBlock;
    
    public IfNode(ASTNode condition, List<ASTNode> thenBlock, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elifBranches = new ArrayList<>();
        this.elseBlock = new ArrayList<>();
    }
    
    public ASTNode getCondition() { return condition; }
    public List<ASTNode> getThenBlock() { return thenBlock; }
    public List<ElifBranch> getElifBranches() { return elifBranches; }
    public List<ASTNode> getElseBlock() { return elseBlock; }
    
    public void addElifBranch(ASTNode condition, List<ASTNode> block) {
        elifBranches.add(new ElifBranch(condition, block));
    }
    
    public void setElseBlock(List<ASTNode> block) {
        this.elseBlock = block;
    }
    
    @Override
    public String toString() {
        return "If(condition, " + thenBlock.size() + " stmts)";
    }
    
    @Override
    public String toTreeString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent(indent)).append("If\n");
        sb.append(getIndent(indent + 1)).append("Condition:\n");
        sb.append(condition.toTreeString(indent + 2));
        sb.append(getIndent(indent + 1)).append("Then:\n");
        for (ASTNode stmt : thenBlock) {
            sb.append(stmt.toTreeString(indent + 2));
        }
        
        for (ElifBranch elif : elifBranches) {
            sb.append(getIndent(indent + 1)).append("Elif:\n");
            sb.append(getIndent(indent + 2)).append("Condition:\n");
            sb.append(elif.condition.toTreeString(indent + 3));
            sb.append(getIndent(indent + 2)).append("Then:\n");
            for (ASTNode stmt : elif.block) {
                sb.append(stmt.toTreeString(indent + 3));
            }
        }
        
        if (!elseBlock.isEmpty()) {
            sb.append(getIndent(indent + 1)).append("Else:\n");
            for (ASTNode stmt : elseBlock) {
                sb.append(stmt.toTreeString(indent + 2));
            }
        }
        return sb.toString();
    }
    
    public static class ElifBranch {
        public ASTNode condition;
        public List<ASTNode> block;
        
        public ElifBranch(ASTNode condition, List<ASTNode> block) {
            this.condition = condition;
            this.block = block;
        }
    }
}

// While loop node
class WhileNode extends ASTNode {
    private ASTNode condition;
    private List<ASTNode> body;
    
    public WhileNode(ASTNode condition, List<ASTNode> body, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.body = body;
    }
    
    public ASTNode getCondition() { return condition; }
    public List<ASTNode> getBody() { return body; }
    
    @Override
    public String toString() {
        return "While(condition, " + body.size() + " stmts)";
    }
    
    @Override
    public String toTreeString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent(indent)).append("While\n");
        sb.append(getIndent(indent + 1)).append("Condition:\n");
        sb.append(condition.toTreeString(indent + 2));
        sb.append(getIndent(indent + 1)).append("Body:\n");
        for (ASTNode stmt : body) {
            sb.append(stmt.toTreeString(indent + 2));
        }
        return sb.toString();
    }
}

// For loop node
class ForNode extends ASTNode {
    private String variable;
    private ASTNode iterable;
    private List<ASTNode> body;
    
    public ForNode(String variable, ASTNode iterable, List<ASTNode> body, int line, int column) {
        super(line, column);
        this.variable = variable;
        this.iterable = iterable;
        this.body = body;
    }
    
    public String getVariable() { return variable; }
    public ASTNode getIterable() { return iterable; }
    public List<ASTNode> getBody() { return body; }
    
    @Override
    public String toString() {
        return "For(" + variable + " in ..., " + body.size() + " stmts)";
    }
    
    @Override
    public String toTreeString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent(indent)).append("For: ").append(variable).append(" in\n");
        sb.append(iterable.toTreeString(indent + 1));
        sb.append(getIndent(indent + 1)).append("Body:\n");
        for (ASTNode stmt : body) {
            sb.append(stmt.toTreeString(indent + 2));
        }
        return sb.toString();
    }
}

// Return statement node
class ReturnNode extends ASTNode {
    private ASTNode value; // Can be null for void returns
    
    public ReturnNode(ASTNode value, int line, int column) {
        super(line, column);
        this.value = value;
    }
    
    public ASTNode getValue() { return value; }
    
    @Override
    public String toString() {
        return "Return(" + (value != null ? "value" : "void") + ")";
    }
    
    @Override
    public String toTreeString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent(indent)).append("Return\n");
        if (value != null) {
            sb.append(value.toTreeString(indent + 1));
        }
        return sb.toString();
    }
}

// Binary operation node
class BinaryOpNode extends ASTNode {
    private String operator;
    private ASTNode left;
    private ASTNode right;
    private String resultType; // For semantic analysis
    
    public BinaryOpNode(String operator, ASTNode left, ASTNode right, int line, int column) {
        super(line, column);
        this.operator = operator;
        this.left = left;
        this.right = right;
    }
    
    public String getOperator() { return operator; }
    public ASTNode getLeft() { return left; }
    public ASTNode getRight() { return right; }
    public String getResultType() { return resultType; }
    public void setResultType(String type) { this.resultType = type; }
    
    @Override
    public String toString() {
        return "BinaryOp(" + operator + ")";
    }
    
    @Override
    public String toTreeString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent(indent)).append("BinaryOp: ").append(operator).append("\n");
        sb.append(left.toTreeString(indent + 1));
        sb.append(right.toTreeString(indent + 1));
        return sb.toString();
    }
}

// Unary operation node
class UnaryOpNode extends ASTNode {
    private String operator;
    private ASTNode operand;
    private String resultType;
    
    public UnaryOpNode(String operator, ASTNode operand, int line, int column) {
        super(line, column);
        this.operator = operator;
        this.operand = operand;
    }
    
    public String getOperator() { return operator; }
    public ASTNode getOperand() { return operand; }
    public String getResultType() { return resultType; }
    public void setResultType(String type) { this.resultType = type; }
    
    @Override
    public String toString() {
        return "UnaryOp(" + operator + ")";
    }
    
    @Override
    public String toTreeString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent(indent)).append("UnaryOp: ").append(operator).append("\n");
        sb.append(operand.toTreeString(indent + 1));
        return sb.toString();
    }
}

// Function call node
class CallNode extends ASTNode {
    private String functionName;
    private List<ASTNode> arguments;
    private String returnType; // For semantic analysis
    
    public CallNode(String functionName, List<ASTNode> arguments, int line, int column) {
        super(line, column);
        this.functionName = functionName;
        this.arguments = arguments;
    }
    
    public String getFunctionName() { return functionName; }
    public List<ASTNode> getArguments() { return arguments; }
    public String getReturnType() { return returnType; }
    public void setReturnType(String type) { this.returnType = type; }
    
    @Override
    public String toString() {
        return "Call(" + functionName + ", " + arguments.size() + " args)";
    }
    
    @Override
    public String toTreeString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent(indent)).append("Call: ").append(functionName).append("\n");
        if (!arguments.isEmpty()) {
            sb.append(getIndent(indent + 1)).append("Arguments:\n");
            for (ASTNode arg : arguments) {
                sb.append(arg.toTreeString(indent + 2));
            }
        }
        return sb.toString();
    }
}

// Array access node
class ArrayAccessNode extends ASTNode {
    private ASTNode array;
    private ASTNode index;
    private String elementType;
    
    public ArrayAccessNode(ASTNode array, ASTNode index, int line, int column) {
        super(line, column);
        this.array = array;
        this.index = index;
    }
    
    public ASTNode getArray() { return array; }
    public ASTNode getIndex() { return index; }
    public String getElementType() { return elementType; }
    public void setElementType(String type) { this.elementType = type; }
    
    @Override
    public String toString() {
        return "ArrayAccess";
    }
    
    @Override
    public String toTreeString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent(indent)).append("ArrayAccess\n");
        sb.append(getIndent(indent + 1)).append("Array:\n");
        sb.append(array.toTreeString(indent + 2));
        sb.append(getIndent(indent + 1)).append("Index:\n");
        sb.append(index.toTreeString(indent + 2));
        return sb.toString();
    }
}

// Member access node (object.field)
class MemberAccessNode extends ASTNode {
    private ASTNode object;
    private String member;
    private String memberType;
    
    public MemberAccessNode(ASTNode object, String member, int line, int column) {
        super(line, column);
        this.object = object;
        this.member = member;
    }
    
    public ASTNode getObject() { return object; }
    public String getMember() { return member; }
    public String getMemberType() { return memberType; }
    public void setMemberType(String type) { this.memberType = type; }
    
    @Override
    public String toString() {
        return "MemberAccess(." + member + ")";
    }
    
    @Override
    public String toTreeString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent(indent)).append("MemberAccess: .").append(member).append("\n");
        sb.append(object.toTreeString(indent + 1));
        return sb.toString();
    }
}

// Literal nodes
class NumberLiteralNode extends ASTNode {
    private String value;
    private boolean isFloat;
    
    public NumberLiteralNode(String value, int line, int column) {
        super(line, column);
        this.value = value;
        this.isFloat = value.contains(".");
    }
    
    public String getValue() { return value; }
    public boolean isFloat() { return isFloat; }
    public String getType() { return isFloat ? "float" : "int"; }
    
    @Override
    public String toString() {
        return "Number(" + value + ")";
    }
    
    @Override
    public String toTreeString(int indent) {
        return getIndent(indent) + "Number: " + value + " (" + getType() + ")\n";
    }
}

class StringLiteralNode extends ASTNode {
    private String value;
    
    public StringLiteralNode(String value, int line, int column) {
        super(line, column);
        this.value = value;
    }
    
    public String getValue() { return value; }
    public String getType() { return "string"; }
    
    @Override
    public String toString() {
        return "String(\"" + value + "\")";
    }
    
    @Override
    public String toTreeString(int indent) {
        return getIndent(indent) + "String: \"" + value + "\"\n";
    }
}

class BooleanLiteralNode extends ASTNode {
    private boolean value;
    
    public BooleanLiteralNode(boolean value, int line, int column) {
        super(line, column);
        this.value = value;
    }
    
    public boolean getValue() { return value; }
    public String getType() { return "bool"; }
    
    @Override
    public String toString() {
        return "Boolean(" + value + ")";
    }
    
    @Override
    public String toTreeString(int indent) {
        return getIndent(indent) + "Boolean: " + value + "\n";
    }
}

class IdentifierNode extends ASTNode {
    private String name;
    private String type; // Set during semantic analysis
    
    public IdentifierNode(String name, int line, int column) {
        super(line, column);
        this.name = name;
    }
    
    public String getName() { return name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    @Override
    public String toString() {
        return "Identifier(" + name + ")";
    }
    
    @Override
    public String toTreeString(int indent) {
        String typeInfo = type != null ? " : " + type : "";
        return getIndent(indent) + "Identifier: " + name + typeInfo + "\n";
    }
}

class ArrayLiteralNode extends ASTNode {
    private List<ASTNode> elements;
    private String elementType;
    
    public ArrayLiteralNode(List<ASTNode> elements, int line, int column) {
        super(line, column);
        this.elements = elements;
    }
    
    public List<ASTNode> getElements() { return elements; }
    public String getElementType() { return elementType; }
    public void setElementType(String type) { this.elementType = type; }
    public String getType() { return "array<" + (elementType != null ? elementType : "?") + ">"; }
    
    @Override
    public String toString() {
        return "Array(" + elements.size() + " elements)";
    }
    
    @Override
    public String toTreeString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent(indent)).append("Array [").append(elements.size()).append(" elements]\n");
        for (ASTNode elem : elements) {
            sb.append(elem.toTreeString(indent + 1));
        }
        return sb.toString();
    }
}

class ObjectLiteralNode extends ASTNode {
    private Map<String, ASTNode> fields;
    
    public ObjectLiteralNode(Map<String, ASTNode> fields, int line, int column) {
        super(line, column);
        this.fields = fields;
    }
    
    public Map<String, ASTNode> getFields() { return fields; }
    public String getType() { return "object"; }
    
    @Override
    public String toString() {
        return "Object(" + fields.size() + " fields)";
    }
    
    @Override
    public String toTreeString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent(indent)).append("Object {").append(fields.size()).append(" fields}\n");
        for (Map.Entry<String, ASTNode> entry : fields.entrySet()) {
            sb.append(getIndent(indent + 1)).append(entry.getKey()).append(":\n");
            sb.append(entry.getValue().toTreeString(indent + 2));
        }
        return sb.toString();
    }
}
