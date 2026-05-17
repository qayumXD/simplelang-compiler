import java.util.*;

/**
 * Symbol Table for SimpleLang
 * Manages variable and function declarations with scope support
 */
public class SymbolTable {
    
    /**
     * Symbol class representing a variable or function
     */
    public static class Symbol {
        private String name;
        private String type;
        private SymbolKind kind;
        private int line;
        private int column;
        private boolean initialized;
        
        public Symbol(String name, String type, SymbolKind kind, int line, int column) {
            this.name = name;
            this.type = type;
            this.kind = kind;
            this.line = line;
            this.column = column;
            this.initialized = false;
        }
        
        public String getName() { return name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public SymbolKind getKind() { return kind; }
        public int getLine() { return line; }
        public int getColumn() { return column; }
        public boolean isInitialized() { return initialized; }
        public void setInitialized(boolean initialized) { this.initialized = initialized; }
        
        @Override
        public String toString() {
            return String.format("%s: %s (%s) [%d:%d]", name, type, kind, line, column);
        }
    }
    
    /**
     * Symbol kind enumeration
     */
    public enum SymbolKind {
        VARIABLE,
        PARAMETER,
        FUNCTION
    }
    
    /**
     * Function signature for type checking
     */
    public static class FunctionSignature {
        private String name;
        private List<String> parameterTypes;
        private String returnType;
        private int line;
        private int column;
        
        public FunctionSignature(String name, List<String> parameterTypes, String returnType, int line, int column) {
            this.name = name;
            this.parameterTypes = parameterTypes;
            this.returnType = returnType;
            this.line = line;
            this.column = column;
        }
        
        public String getName() { return name; }
        public List<String> getParameterTypes() { return parameterTypes; }
        public String getReturnType() { return returnType; }
        public int getLine() { return line; }
        public int getColumn() { return column; }
        
        @Override
        public String toString() {
            return String.format("%s(%s) -> %s", name, String.join(", ", parameterTypes), returnType);
        }
    }
    
    /**
     * Scope class representing a lexical scope
     */
    private static class Scope {
        private Map<String, Symbol> symbols;
        private Scope parent;
        private String name;
        
        public Scope(String name, Scope parent) {
            this.name = name;
            this.parent = parent;
            this.symbols = new LinkedHashMap<>();
        }
        
        public void define(Symbol symbol) {
            symbols.put(symbol.getName(), symbol);
        }
        
        public Symbol resolve(String name) {
            if (symbols.containsKey(name)) {
                return symbols.get(name);
            }
            if (parent != null) {
                return parent.resolve(name);
            }
            return null;
        }
        
        public Symbol resolveLocal(String name) {
            return symbols.get(name);
        }
        
        public Map<String, Symbol> getSymbols() {
            return symbols;
        }
        
        public Scope getParent() {
            return parent;
        }
        
        public String getName() {
            return name;
        }
    }
    
    private Scope globalScope;
    private Scope currentScope;
    private Map<String, FunctionSignature> functions;
    private int scopeCounter;
    
    /**
     * Constructor
     */
    public SymbolTable() {
        this.globalScope = new Scope("global", null);
        this.currentScope = globalScope;
        this.functions = new LinkedHashMap<>();
        this.scopeCounter = 0;
        
        // Add built-in functions
        addBuiltInFunctions();
    }
    
    /**
     * Add built-in functions to the symbol table
     */
    private void addBuiltInFunctions() {
        // print function - accepts any type
        List<String> printParams = new ArrayList<>();
        printParams.add("any");
        functions.put("print", new FunctionSignature("print", printParams, "void", 0, 0));
        
        // range function - returns array<int>
        List<String> rangeParams = new ArrayList<>();
        rangeParams.add("int");
        rangeParams.add("int");
        functions.put("range", new FunctionSignature("range", rangeParams, "array<int>", 0, 0));
    }
    
    /**
     * Enter a new scope
     * @param name Name of the scope
     */
    public void enterScope(String name) {
        scopeCounter++;
        String scopeName = name + "_" + scopeCounter;
        currentScope = new Scope(scopeName, currentScope);
    }
    
    /**
     * Exit the current scope
     */
    public void exitScope() {
        if (currentScope.getParent() != null) {
            currentScope = currentScope.getParent();
        }
    }
    
    /**
     * Define a variable in the current scope
     * @param name Variable name
     * @param type Variable type
     * @param line Line number
     * @param column Column number
     * @return true if successful, false if already defined
     */
    public boolean defineVariable(String name, String type, int line, int column) {
        if (currentScope.resolveLocal(name) != null) {
            return false; // Already defined in current scope
        }
        
        Symbol symbol = new Symbol(name, type, SymbolKind.VARIABLE, line, column);
        symbol.setInitialized(true);
        currentScope.define(symbol);
        return true;
    }
    
    /**
     * Define a parameter in the current scope
     * @param name Parameter name
     * @param type Parameter type
     * @param line Line number
     * @param column Column number
     * @return true if successful
     */
    public boolean defineParameter(String name, String type, int line, int column) {
        if (currentScope.resolveLocal(name) != null) {
            return false;
        }
        
        Symbol symbol = new Symbol(name, type, SymbolKind.PARAMETER, line, column);
        symbol.setInitialized(true);
        currentScope.define(symbol);
        return true;
    }
    
    /**
     * Define a function in the global scope
     * @param name Function name
     * @param parameterTypes Parameter types
     * @param returnType Return type
     * @param line Line number
     * @param column Column number
     * @return true if successful
     */
    public boolean defineFunction(String name, List<String> parameterTypes, String returnType, int line, int column) {
        if (functions.containsKey(name)) {
            return false; // Already defined
        }
        
        FunctionSignature signature = new FunctionSignature(name, parameterTypes, returnType, line, column);
        functions.put(name, signature);
        
        // Also add to global scope as a symbol
        Symbol symbol = new Symbol(name, "function", SymbolKind.FUNCTION, line, column);
        globalScope.define(symbol);
        
        return true;
    }
    
    /**
     * Resolve a variable or parameter
     * @param name Variable name
     * @return Symbol or null if not found
     */
    public Symbol resolve(String name) {
        return currentScope.resolve(name);
    }
    
    /**
     * Resolve a function
     * @param name Function name
     * @return FunctionSignature or null if not found
     */
    public FunctionSignature resolveFunction(String name) {
        return functions.get(name);
    }
    
    /**
     * Get all symbols in the current scope
     * @return Map of symbols
     */
    public Map<String, Symbol> getCurrentScopeSymbols() {
        return currentScope.getSymbols();
    }
    
    /**
     * Get all symbols (for display)
     * @return List of all symbols
     */
    public List<Symbol> getAllSymbols() {
        List<Symbol> allSymbols = new ArrayList<>();
        collectSymbols(globalScope, allSymbols);
        return allSymbols;
    }
    
    /**
     * Recursively collect symbols from all scopes
     */
    private void collectSymbols(Scope scope, List<Symbol> symbols) {
        symbols.addAll(scope.getSymbols().values());
    }
    
    /**
     * Get all functions
     * @return Map of function signatures
     */
    public Map<String, FunctionSignature> getAllFunctions() {
        return functions;
    }
    
    /**
     * Get current scope name
     * @return Scope name
     */
    public String getCurrentScopeName() {
        return currentScope.getName();
    }
    
    /**
     * Check if in global scope
     * @return true if in global scope
     */
    public boolean isGlobalScope() {
        return currentScope == globalScope;
    }
    
    /**
     * Get a formatted string representation of the symbol table
     * @return Formatted string
     */
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== SYMBOL TABLE ===\n\n");
        
        // Functions
        sb.append("Functions:\n");
        sb.append("-".repeat(60)).append("\n");
        for (FunctionSignature func : functions.values()) {
            sb.append(String.format("  %-20s %s\n", func.getName(), func.toString()));
        }
        sb.append("\n");
        
        // Variables
        sb.append("Variables:\n");
        sb.append("-".repeat(60)).append("\n");
        displayScope(globalScope, sb, 0);
        
        return sb.toString();
    }
    
    /**
     * Recursively display scope contents
     */
    private void displayScope(Scope scope, StringBuilder sb, int indent) {
        String indentStr = "  ".repeat(indent);
        
        if (indent > 0) {
            sb.append(indentStr).append("Scope: ").append(scope.getName()).append("\n");
        }
        
        for (Symbol symbol : scope.getSymbols().values()) {
            if (symbol.getKind() != SymbolKind.FUNCTION) {
                sb.append(indentStr).append("  ").append(symbol.toString()).append("\n");
            }
        }
    }
    
    /**
     * Reset the symbol table
     */
    public void reset() {
        this.globalScope = new Scope("global", null);
        this.currentScope = globalScope;
        this.functions = new LinkedHashMap<>();
        this.scopeCounter = 0;
        addBuiltInFunctions();
    }
}
