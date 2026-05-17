import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * CompilerGUI for SimpleLang
 * Interactive GUI for compiling SimpleLang programs
 */
public class CompilerGUI extends JFrame {
    private JTextArea codeEditor;
    private JTextArea tokensArea;
    private JTextArea astArea;
    private JTextArea symbolsArea;
    private JLabel statusLabel;
    private JButton compileButton;
    private JButton clearButton;
    private JButton loadButton;
    private JButton saveButton;
    private File currentFile;
    
    public CompilerGUI() {
        setTitle("SimpleLang Compiler - Lexical, Syntax, and Semantic Analysis");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1200);
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Fix Ubuntu scaling issues
        System.setProperty("sun.java2d.uiScale", "2.0");
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // Add header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Add split pane with editor and output
        mainPanel.add(createSplitPane(), BorderLayout.CENTER);
        
        // Add status panel
        mainPanel.add(createStatusPanel(), BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
        
        // Load example program
        loadExampleProgram();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(new EmptyBorder(40, 0, 40, 0));
        
        JLabel titleLabel = new JLabel("SimpleLang Compiler");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        
        panel.add(titleLabel);
        return panel;
    }
    
    private JSplitPane createSplitPane() {
        // Left side: Code editor
        JPanel editorPanel = createEditorPanel();
        
        // Right side: Output panels
        JPanel outputPanel = createOutputPanel();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorPanel, outputPanel);
        splitPane.setDividerLocation(800);
        splitPane.setResizeWeight(0.4);
        
        return splitPane;
    }
    
    private JPanel createEditorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(new TitledBorder(new LineBorder(new Color(100, 100, 100), 3),
                "Code Editor", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 28)));
        
        // Code editor
        codeEditor = new JTextArea();
        codeEditor.setFont(new Font("Courier New", Font.PLAIN, 24));
        codeEditor.setBackground(new Color(39, 40, 34)); // Dark theme
        codeEditor.setForeground(new Color(248, 248, 242));
        codeEditor.setCaretColor(Color.WHITE);
        codeEditor.setLineWrap(false);
        codeEditor.setTabSize(4);
        codeEditor.setMargin(new Insets(20, 20, 20, 20));
        
        JScrollPane scrollPane = new JScrollPane(codeEditor);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 3));
        
        // Line numbers (simple implementation)
        JTextArea lineNumbers = new JTextArea("1\n");
        lineNumbers.setFont(new Font("Courier New", Font.PLAIN, 24));
        lineNumbers.setBackground(new Color(60, 60, 60));
        lineNumbers.setForeground(new Color(150, 150, 150));
        lineNumbers.setEditable(false);
        lineNumbers.setMargin(new Insets(20, 15, 20, 15));
        scrollPane.setRowHeaderView(lineNumbers);
        
        // Update line numbers on text change
        codeEditor.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateLineNumbers(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateLineNumbers(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateLineNumbers(); }
            
            private void updateLineNumbers() {
                int lines = codeEditor.getLineCount();
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= lines; i++) {
                    sb.append(i).append("\n");
                }
                lineNumbers.setText(sb.toString());
            }
        });
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        compileButton = new JButton("Compile");
        compileButton.setFont(new Font("Arial", Font.BOLD, 24));
        compileButton.setBackground(new Color(46, 204, 113));
        compileButton.setForeground(Color.WHITE);
        compileButton.setFocusPainted(false);
        compileButton.setBorder(new LineBorder(new Color(39, 174, 96), 3));
        compileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        compileButton.setPreferredSize(new Dimension(220, 80));
        compileButton.addActionListener(e -> compileCode());
        
        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.BOLD, 24));
        clearButton.setBackground(new Color(231, 76, 60));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setBorder(new LineBorder(new Color(192, 57, 43), 3));
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.setPreferredSize(new Dimension(220, 80));
        clearButton.addActionListener(e -> clearAll());
        
        loadButton = new JButton("Load File");
        loadButton.setFont(new Font("Arial", Font.BOLD, 24));
        loadButton.setBackground(new Color(52, 152, 219));
        loadButton.setForeground(Color.WHITE);
        loadButton.setFocusPainted(false);
        loadButton.setBorder(new LineBorder(new Color(41, 128, 185), 3));
        loadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loadButton.setPreferredSize(new Dimension(220, 80));
        loadButton.addActionListener(e -> loadFile());
        
        saveButton = new JButton("Save File");
        saveButton.setFont(new Font("Arial", Font.BOLD, 24));
        saveButton.setBackground(new Color(155, 89, 182));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(new LineBorder(new Color(142, 68, 173), 3));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(220, 80));
        saveButton.addActionListener(e -> saveFile());
        
        buttonPanel.add(compileButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(saveButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBackground(new Color(240, 240, 240));
        
        // Tokens panel
        panel.add(createTokensPanel());
        
        // AST panel
        panel.add(createASTPanel());
        
        // Symbols panel
        panel.add(createSymbolsPanel());
        
        return panel;
    }
    
    private JPanel createTokensPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(new LineBorder(new Color(100, 100, 100), 3),
                "Tokens (Lexical Analysis)", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 24)));
        panel.setBackground(Color.WHITE);
        
        tokensArea = new JTextArea();
        tokensArea.setFont(new Font("Courier New", Font.PLAIN, 20));
        tokensArea.setEditable(false);
        tokensArea.setBackground(new Color(245, 245, 245));
        tokensArea.setForeground(new Color(50, 50, 50));
        tokensArea.setLineWrap(false);
        tokensArea.setMargin(new Insets(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(tokensArea);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 3));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createASTPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(new LineBorder(new Color(100, 100, 100), 3),
                "AST (Syntax Analysis)", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 24)));
        panel.setBackground(Color.WHITE);
        
        astArea = new JTextArea();
        astArea.setFont(new Font("Courier New", Font.PLAIN, 20));
        astArea.setEditable(false);
        astArea.setBackground(new Color(245, 245, 245));
        astArea.setForeground(new Color(50, 50, 50));
        astArea.setLineWrap(false);
        astArea.setMargin(new Insets(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(astArea);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 3));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createSymbolsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(new LineBorder(new Color(100, 100, 100), 3),
                "Symbol Table (Semantic Analysis)", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 24)));
        panel.setBackground(Color.WHITE);
        
        symbolsArea = new JTextArea();
        symbolsArea.setFont(new Font("Courier New", Font.PLAIN, 20));
        symbolsArea.setEditable(false);
        symbolsArea.setBackground(new Color(245, 245, 245));
        symbolsArea.setForeground(new Color(50, 50, 50));
        symbolsArea.setLineWrap(false);
        symbolsArea.setMargin(new Insets(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(symbolsArea);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 3));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(new TitledBorder(new LineBorder(new Color(100, 100, 100), 3),
                "Status", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 24)));
        panel.setPreferredSize(new Dimension(0, 100));
        
        statusLabel = new JLabel("Ready - Load a .sl file or write code");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        statusLabel.setForeground(new Color(46, 204, 113));
        
        panel.add(statusLabel);
        return panel;
    }
    
    private void compileCode() {
        String code = codeEditor.getText().trim();
        
        if (code.isEmpty()) {
            showError("Please enter some code");
            return;
        }
        
        try {
            updateStatus("Compiling...", new Color(241, 196, 15));
            
            // Phase 1: Lexical Analysis
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();
            
            if (lexer.hasErrors()) {
                displayErrors("Lexical Errors", lexer.getErrors());
                updateStatus("✗ Lexical analysis failed", new Color(231, 76, 60));
                return;
            }
            
            displayTokens(tokens);
            
            // Phase 2: Syntax Analysis
            Parser parser = new Parser(tokens);
            ProgramNode ast = parser.parse();
            
            if (parser.hasErrors()) {
                displayErrors("Syntax Errors", parser.getErrors());
                updateStatus("✗ Syntax analysis failed", new Color(231, 76, 60));
                return;
            }
            
            displayAST(ast);
            
            // Phase 3: Semantic Analysis
            SemanticAnalyzer analyzer = new SemanticAnalyzer();
            boolean success = analyzer.analyze(ast);
            
            if (!success) {
                displayErrors("Semantic Errors", analyzer.getErrors());
                updateStatus("✗ Semantic analysis failed", new Color(231, 76, 60));
                return;
            }
            
            displaySymbolTable(analyzer.getSymbolTable());
            
            updateStatus("✓ Compilation successful! All phases passed.", new Color(46, 204, 113));
            
        } catch (Exception e) {
            showError("Compilation Error: " + e.getMessage());
            e.printStackTrace();
            updateStatus("✗ Compilation failed", new Color(231, 76, 60));
        }
    }
    
    private void displayTokens(List<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TOKENS ===\n");
        sb.append(String.format("Total: %d tokens\n\n", tokens.size()));
        
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            sb.append(String.format("%3d. %s\n", i + 1, token.toDisplayString()));
        }
        
        tokensArea.setText(sb.toString());
        tokensArea.setCaretPosition(0);
    }
    
    private void displayAST(ProgramNode ast) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ABSTRACT SYNTAX TREE ===\n\n");
        sb.append(ast.toTreeString(0));
        
        astArea.setText(sb.toString());
        astArea.setCaretPosition(0);
    }
    
    private void displaySymbolTable(SymbolTable symbolTable) {
        symbolsArea.setText(symbolTable.toDisplayString());
        symbolsArea.setCaretPosition(0);
    }
    
    private void displayErrors(String title, List<String> errors) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(title.toUpperCase()).append(" ===\n\n");
        
        for (String error : errors) {
            sb.append("• ").append(error).append("\n");
        }
        
        // Display in all panels
        tokensArea.setText(sb.toString());
        astArea.setText(sb.toString());
        symbolsArea.setText(sb.toString());
    }
    
    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("SimpleLang Files (*.sl)", "sl"));
        fileChooser.setCurrentDirectory(new File("examples"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try {
                StringBuilder content = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(currentFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                
                codeEditor.setText(content.toString());
                updateStatus("Loaded: " + currentFile.getName(), new Color(52, 152, 219));
            } catch (IOException e) {
                showError("Error loading file: " + e.getMessage());
            }
        }
    }
    
    private void saveFile() {
        if (currentFile == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("SimpleLang Files (*.sl)", "sl"));
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
                if (!currentFile.getName().endsWith(".sl")) {
                    currentFile = new File(currentFile.getAbsolutePath() + ".sl");
                }
            } else {
                return;
            }
        }
        
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile));
            writer.write(codeEditor.getText());
            writer.close();
            updateStatus("Saved: " + currentFile.getName(), new Color(46, 204, 113));
        } catch (IOException e) {
            showError("Error saving file: " + e.getMessage());
        }
    }
    
    private void loadExampleProgram() {
        String example = "# SimpleLang Example: Factorial\n\n" +
                        "func factorial(n: int) -> int:\n" +
                        "    if n <= 1:\n" +
                        "        return 1\n" +
                        "    else:\n" +
                        "        return n * factorial(n - 1)\n\n" +
                        "func main() -> void:\n" +
                        "    let num = 5\n" +
                        "    let result = factorial(num)\n" +
                        "    print(result)\n";
        codeEditor.setText(example);
    }
    
    private void clearAll() {
        codeEditor.setText("");
        tokensArea.setText("");
        astArea.setText("");
        symbolsArea.setText("");
        currentFile = null;
        updateStatus("Ready - Load a .sl file or write code", new Color(46, 204, 113));
        codeEditor.requestFocus();
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void updateStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CompilerGUI());
    }
}
