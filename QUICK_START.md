# SimpleLang Compiler - Quick Start Guide

## 🚀 Getting Started in 3 Steps

### Step 1: Compile the Compiler
```bash
cd simplelang-compiler
javac -d bin src/*.java
```

### Step 2: Run the GUI
```bash
java -cp bin Main
```

### Step 3: Try an Example
```bash
java -cp bin Main examples/factorial.sl
```

---

## 📝 Example Programs

### Hello World (`examples/hello.sl`)
```simplelang
func main() -> void:
    print("Hello, World!")
```

### Factorial (`examples/factorial.sl`)
```simplelang
func factorial(n: int) -> int:
    if n <= 1:
        return 1
    else:
        return n * factorial(n - 1)

func main() -> void:
    let num = 5
    let result = factorial(num)
    print(result)
```

### Variables (`examples/variables.sl`)
```simplelang
func main() -> void:
    let x: int = 10
    let name: string = "Alice"
    let greeting = "Hello, " + name
    print(greeting)
```

---

## 🎮 Usage

### GUI Mode (Recommended)
```bash
java -cp bin Main
```
- Load `.sl` files
- Edit code with syntax highlighting
- See tokens, AST, and symbol table
- Real-time compilation

### Command-Line Mode
```bash
# Compile a file
java -cp bin Main examples/factorial.sl

# Show AST
java -Dshow.ast=true -cp bin Main examples/factorial.sl

# Show tokens
java -Dshow.tokens=true -cp bin Main examples/factorial.sl

# Show both
java -Dshow.ast=true -Dshow.tokens=true -cp bin Main examples/factorial.sl
```

---

## 📊 Compiler Phases

### Phase 1: Lexical Analysis
- Tokenizes source code
- Handles Python-style indentation (INDENT/DEDENT tokens)
- Recognizes keywords, identifiers, literals, operators
- Tracks line and column numbers

**Output:** Token stream

### Phase 2: Syntax Analysis
- Builds Abstract Syntax Tree (AST)
- Recursive descent parsing
- Operator precedence handling
- Syntax error detection

**Output:** AST

### Phase 3: Semantic Analysis
- Type checking
- Symbol table management
- Scope resolution
- Function signature validation
- Type inference

**Output:** Annotated AST + Symbol Table

---

## 🎨 GUI Features

### Code Editor
- Dark theme
- Line numbers
- Large, readable fonts (18pt)
- Tab support (4 spaces)

### Output Panels
1. **Tokens** - Lexical analysis results
2. **AST** - Syntax tree visualization
3. **Symbol Table** - Functions and variables with types

### Controls
- **Compile** - Run all three phases
- **Clear** - Clear editor and output
- **Load File** - Open `.sl` files
- **Save File** - Save current code

---

## 🧪 Testing

### Test All Examples
```bash
# Test factorial
java -cp bin Main examples/factorial.sl

# Test hello world
java -cp bin Main examples/hello.sl

# Test variables
java -cp bin Main examples/variables.sl
```

### Expected Output
```
SimpleLang Compiler
===================

Compiling: examples/factorial.sl

Phase 1: Lexical Analysis
--------------------------------------------------
✓ Lexical analysis successful
  Tokens: 84

Phase 2: Syntax Analysis
--------------------------------------------------
✓ Syntax analysis successful
  Statements: 2

Phase 3: Semantic Analysis
--------------------------------------------------
✓ Semantic analysis successful

Symbol Table:
--------------------------------------------------
Functions: 4
  print(any) -> void
  range(int, int) -> array<int>
  factorial(int) -> int
  main() -> void
Variables: 0

==================================================
✓ COMPILATION SUCCESSFUL
==================================================
```

---

## 🐛 Error Handling

### Lexical Errors
```simplelang
let x = @invalid
```
**Output:** `Unexpected character '@' at line 1, column 9`

### Syntax Errors
```simplelang
let x = 10 20
```
**Output:** `Expected operator after expression`

### Semantic Errors
```simplelang
let x: int = "hello"
```
**Output:** `Type mismatch: cannot assign string to int`

---

## 📚 Language Features

### ✅ Implemented
- Variables with type inference
- Functions with parameters and return types
- Control flow (if/elif/else, while, for)
- Operators (arithmetic, comparison, logical)
- Data types (int, float, string, bool)
- Arrays and objects
- Type checking
- Scope management
- Built-in functions (print, range)

### 🔮 Future
- Code generation (bytecode/assembly)
- Optimization passes
- Classes and objects
- Modules and imports
- Error handling (try/catch)

---

## 💡 Tips

1. **Use the GUI** for interactive development
2. **Start with examples** to learn the syntax
3. **Check error messages** - they include line and column numbers
4. **Use type inference** - `let x = 10` instead of `let x: int = 10`
5. **Indentation matters** - Use 4 spaces (like Python)

---

## 🎓 Learning Path

1. **Start:** Run `java -cp bin Main` to launch GUI
2. **Load:** Open `examples/hello.sl`
3. **Compile:** Click "Compile" button
4. **Explore:** Check tokens, AST, and symbol table
5. **Modify:** Change the code and recompile
6. **Create:** Write your own SimpleLang program!

---

## 🔧 Troubleshooting

### GUI doesn't start
- Make sure you compiled: `javac -d bin src/*.java`
- Check Java version: `java -version` (need Java 11+)

### Fonts too small
- The GUI uses large fonts (18-36pt) for Ubuntu compatibility
- If still too small, edit `CompilerGUI.java` and increase font sizes

### Compilation errors
- Check indentation (must be consistent)
- Check for missing colons after if/while/for/func
- Check type compatibility

---

**SimpleLang** - Simple, Safe, Modern

Version: 1.0  
Date: May 17, 2026
