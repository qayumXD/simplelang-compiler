# LAB_Assignment02_SimpleLang Compiler
## Name: Muhammad Abdul Qayyoum
# Reg # SP23-BCS-033


A complete compiler implementation for **SimpleLang** - a modern, statically-typed programming language with Python-like syntax.
# Drive link:https://drive.google.com/file/d/1ysQUBm6XQFESAlau2BQwi9HV-X0uyWXS/view?usp=sharing
---

## 🎯 Project Overview

This project implements the first three phases of a compiler:

1. **Lexical Analysis** - Tokenization and lexing
2. **Syntax Analysis** - Parsing and AST construction
3. **Semantic Analysis** - Type checking and validation

Plus an interactive GUI for visualization!

---

## 🌟 Language Features

### SimpleLang combines the best of:
- **Python** - Clean, readable syntax with significant indentation
- **TypeScript** - Static typing with type inference
- **Go** - Simple, modern language design

### Key Features:
✅ Static typing with type inference  
✅ First-class functions  
✅ Arrays and objects  
✅ Control flow (if/while/for)  
✅ Python-like syntax (no semicolons!)  
✅ Modern operators  
✅ Clear error messages  

---

## 📝 Example Code

### Hello World
```simplelang
func main() -> void:
    print("Hello, World!")
```

### Factorial Function
```simplelang
func factorial(n: int) -> int:
    if n <= 1:
        return 1
    else:
        return n * factorial(n - 1)

func main() -> void:
    let result = factorial(5)
    print(result)  # Output: 120
```

### Variables and Types
```simplelang
func main() -> void:
    let x: int = 10
    let name: string = "Alice"
    let price: float = 99.99
    let isActive: bool = true
    
    # Type inference
    let y = 20              # inferred as int
    let greeting = "Hello"  # inferred as string
```

---

## 🏗️ Project Structure

```
simplelang-compiler/
├── src/                    # Source code
│   ├── Token.java          # Token representation
│   ├── Lexer.java          # Lexical analyzer
│   ├── ASTNode.java        # AST node classes
│   ├── Parser.java         # Syntax analyzer
│   ├── SymbolTable.java    # Symbol table
│   ├── SemanticAnalyzer.java # Semantic analyzer
│   ├── CompilerGUI.java    # Interactive GUI
│   └── Main.java           # Main entry point
├── docs/                   # Documentation
│   ├── LANGUAGE_SPEC.md    # Language specification
│   ├── GRAMMAR.md          # Formal grammar
│   └── EXAMPLES.md         # Code examples
├── examples/               # Example programs
│   ├── hello.sl
│   ├── factorial.sl
│   └── variables.sl
├── tests/                  # Test files
└── README.md               # This file
```

---

## 🚀 Getting Started

### Prerequisites
- Java 11 or higher
- JDK installed

### Compilation
```bash
cd simplelang-compiler
javac -d bin src/*.java
```

### Run the GUI
```bash
java -cp bin CompilerGUI
```

### Run Command-Line Compiler
```bash
java -cp bin Main examples/hello.sl
```

---

## 📊 Compiler Phases

### Phase 1: Lexical Analysis
- **Input:** Source code (.sl files)
- **Output:** Token stream
- **Features:**
  - Keyword recognition
  - Identifier tokenization
  - Number and string literals
  - Operator recognition
  - Indentation handling (Python-style)
  - Comment removal

### Phase 2: Syntax Analysis
- **Input:** Token stream
- **Output:** Abstract Syntax Tree (AST)
- **Features:**
  - Recursive descent parsing
  - Operator precedence
  - Expression parsing
  - Statement parsing
  - Block structure handling
  - Syntax error detection

### Phase 3: Semantic Analysis
- **Input:** Abstract Syntax Tree
- **Output:** Annotated AST + Symbol Table
- **Features:**
  - Type checking
  - Variable declaration checking
  - Function signature validation
  - Scope resolution
  - Type inference
  - Semantic error detection

---

## 🎨 GUI Features

The interactive GUI provides:

✅ **Code Editor**
- Syntax highlighting
- Line numbers
- Large, readable fonts

✅ **Three Output Panels**
- **Tokens** - Lexical analysis results
- **AST** - Syntax tree visualization
- **Symbols** - Symbol table and types

✅ **Error Display**
- Lexical errors
- Syntax errors
- Semantic errors
- Line and column numbers

✅ **Controls**
- Compile button
- Clear button
- Load file button
- Save file button

---

## 📖 Language Specification

See [LANGUAGE_SPEC.md](docs/LANGUAGE_SPEC.md) for complete language documentation including:
- Syntax overview
- Grammar (BNF)
- Type system
- Semantic rules
- Keywords and operators
- Example programs

---

## 🧪 Testing

Run the test suite:
```bash
java -cp bin TestSuite
```

Test files are in the `tests/` directory.

---

## 🎯 Implementation Status

| Phase | Status | Features |
|-------|--------|----------|
| Lexical Analysis | ✅ Complete | Tokenization, indentation, comments |
| Syntax Analysis | ✅ Complete | Parsing, AST, error handling |
| Semantic Analysis | ✅ Complete | Type checking, symbol table, scopes |
| GUI | ✅ Complete | Interactive editor, visualization |
| Code Generation | 🔄 Future | Bytecode/assembly generation |
| Optimization | 🔄 Future | Code optimization passes |

---

## 📚 Documentation

- **[LANGUAGE_SPEC.md](docs/LANGUAGE_SPEC.md)** - Complete language specification
- **[GRAMMAR.md](docs/GRAMMAR.md)** - Formal grammar definition
- **[EXAMPLES.md](docs/EXAMPLES.md)** - Code examples and tutorials

---

## 🎓 Learning Outcomes

This project demonstrates:
- Lexical analysis and tokenization
- Recursive descent parsing
- Abstract syntax trees
- Symbol tables and scopes
- Type systems and type checking
- Semantic analysis
- Error handling and reporting
- GUI development with Swing

---

## 🔮 Future Enhancements

1. **Code Generation**
   - Bytecode generation
   - Assembly code generation
   - LLVM IR generation

2. **Optimization**
   - Constant folding
   - Dead code elimination
   - Common subexpression elimination

3. **Language Features**
   - Classes and objects
   - Modules and imports
   - Generics
   - Pattern matching
   - Async/await

4. **Tooling**
   - Debugger
   - REPL (Read-Eval-Print Loop)
   - Package manager
   - Standard library

---

## 🤝 Contributing

This is an educational project. Feel free to:
- Add new language features
- Improve error messages
- Add more examples
- Enhance the GUI
- Write tests

---

## 📄 License

Educational project for Compiler Construction course.

---

## 👨‍💻 Author

**Qayum**  
Compiler Construction - Spring 2026

---

## 🙏 Acknowledgments

- Inspired by Python, TypeScript, and Go
- Built for educational purposes
- Part of Compiler Construction course

---

**SimpleLang** - Simple, Safe, Modern

Version: 1.0  
Date: May 16, 2026
