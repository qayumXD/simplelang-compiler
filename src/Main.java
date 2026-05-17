import java.io.*;
import java.util.*;

/**
 * Main entry point for SimpleLang compiler
 * Supports both command-line and GUI modes
 */
public class Main {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            // No arguments - launch GUI
            System.out.println("SimpleLang Compiler - Starting GUI...");
            javax.swing.SwingUtilities.invokeLater(() -> new CompilerGUI());
        } else if (args[0].equals("--help") || args[0].equals("-h")) {
            printHelp();
        } else {
            // Compile file from command line
            compileFile(args[0]);
        }
    }
    
    /**
     * Compile a SimpleLang file from command line
     */
    private static void compileFile(String filename) {
        System.out.println("SimpleLang Compiler");
        System.out.println("===================\n");
        System.out.println("Compiling: " + filename + "\n");
        
        try {
            // Read source file
            String source = readFile(filename);
            
            // Phase 1: Lexical Analysis
            System.out.println("Phase 1: Lexical Analysis");
            System.out.println("-".repeat(50));
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.tokenize();
            
            if (lexer.hasErrors()) {
                System.out.println("✗ Lexical errors found:");
                for (String error : lexer.getErrors()) {
                    System.out.println("  " + error);
                }
                System.exit(1);
            }
            
            System.out.println("✓ Lexical analysis successful");
            System.out.println("  Tokens: " + tokens.size());
            System.out.println();
            
            // Phase 2: Syntax Analysis
            System.out.println("Phase 2: Syntax Analysis");
            System.out.println("-".repeat(50));
            Parser parser = new Parser(tokens);
            ProgramNode ast = parser.parse();
            
            if (parser.hasErrors()) {
                System.out.println("✗ Syntax errors found:");
                for (String error : parser.getErrors()) {
                    System.out.println("  " + error);
                }
                System.exit(1);
            }
            
            System.out.println("✓ Syntax analysis successful");
            System.out.println("  Statements: " + ast.getStatements().size());
            System.out.println();
            
            // Phase 3: Semantic Analysis
            System.out.println("Phase 3: Semantic Analysis");
            System.out.println("-".repeat(50));
            SemanticAnalyzer analyzer = new SemanticAnalyzer();
            boolean success = analyzer.analyze(ast);
            
            if (!success) {
                System.out.println("✗ Semantic errors found:");
                for (String error : analyzer.getErrors()) {
                    System.out.println("  " + error);
                }
                System.exit(1);
            }
            
            System.out.println("✓ Semantic analysis successful");
            
            // Display symbol table
            SymbolTable symbolTable = analyzer.getSymbolTable();
            System.out.println("\nSymbol Table:");
            System.out.println("-".repeat(50));
            
            Map<String, SymbolTable.FunctionSignature> functions = symbolTable.getAllFunctions();
            System.out.println("Functions: " + functions.size());
            for (SymbolTable.FunctionSignature func : functions.values()) {
                System.out.println("  " + func.toString());
            }
            
            List<SymbolTable.Symbol> symbols = symbolTable.getAllSymbols();
            int varCount = 0;
            for (SymbolTable.Symbol symbol : symbols) {
                if (symbol.getKind() != SymbolTable.SymbolKind.FUNCTION) {
                    varCount++;
                }
            }
            System.out.println("Variables: " + varCount);
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("✓ COMPILATION SUCCESSFUL");
            System.out.println("=".repeat(50));
            
            // Optionally display AST
            if (System.getProperty("show.ast") != null) {
                System.out.println("\nAbstract Syntax Tree:");
                System.out.println("-".repeat(50));
                System.out.println(ast.toTreeString(0));
            }
            
            // Optionally display tokens
            if (System.getProperty("show.tokens") != null) {
                System.out.println("\nTokens:");
                System.out.println("-".repeat(50));
                for (int i = 0; i < tokens.size(); i++) {
                    System.out.println(String.format("%3d. %s", i + 1, tokens.get(i).toDisplayString()));
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Compilation error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Read file contents
     */
    private static String readFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        reader.close();
        return content.toString();
    }
    
    /**
     * Print help message
     */
    private static void printHelp() {
        System.out.println("SimpleLang Compiler");
        System.out.println("===================\n");
        System.out.println("Usage:");
        System.out.println("  java Main                  - Launch GUI");
        System.out.println("  java Main <file.sl>        - Compile file");
        System.out.println("  java Main --help           - Show this help\n");
        System.out.println("Options:");
        System.out.println("  -Dshow.ast=true           - Display AST");
        System.out.println("  -Dshow.tokens=true        - Display tokens\n");
        System.out.println("Examples:");
        System.out.println("  java Main examples/factorial.sl");
        System.out.println("  java -Dshow.ast=true Main examples/hello.sl");
        System.out.println("  java Main  # Launch GUI\n");
    }
}
