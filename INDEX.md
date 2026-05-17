# SimpleLang Compiler - File Index

Quick reference guide to all project files.

---

## 📁 Project Structure

```
simplelang-compiler/
├── src/                    # Source code
├── examples/               # Example programs
├── docs/                   # Documentation
├── bin/                    # Compiled classes
├── tests/                  # Test files (empty)
└── [documentation files]   # README, guides, etc.
```

---

## 🔧 Source Code (`src/`)

### Core Compiler Files

| File | Lines | Description |
|------|-------|-------------|
| **TokenType.java** | 80 | Enumeration of all token types (keywords, operators, literals, etc.) |
| **Token.java** | 90 | Token class with type, value, line, and column information |
| **Lexer.java** | 380 | Lexical analyzer - tokenizes source code with indentation handling |
| **ASTNode.java** | 650 | Abstract Syntax Tree node classes (20+ node types) |
| **Parser.java** | 580 | Recursive descent parser - builds AST from tokens |
| **SymbolTable.java** | 280 | Symbol table with scope management for variables and functions |
| **SemanticAnalyzer.java** | 480 | Type checker and semantic validator |
| **CompilerGUI.java** | 420 | Interactive GUI with code editor and output panels |
| **Main.java** | 140 | Entry point - supports both GUI and command-line modes |

### Quick Navigation

**Start here:**
- New to the project? → `Main.java`
- Understanding tokens? → `TokenType.java` → `Token.java`
- Learning lexical analysis? → `Lexer.java`
- Understanding parsing? → `Parser.java` → `ASTNode.java`
- Learning type checking? → `SemanticAnalyzer.java` → `SymbolTable.java`
- Want to use the GUI? → `CompilerGUI.java`

---

## 📝 Example Programs (`examples/`)

| File | Lines | Description | Features Demonstrated |
|------|-------|-------------|----------------------|
| **hello.sl** | 3 | Hello World program | Basic function, print statement |
| **factorial.sl** | 15 | Recursive factorial | Functions, recursion, if/else, parameters |
| **variables.sl** | 25 | Variable types demo | Type inference, multiple types, operators |

### Usage
```bash
java -cp bin Main examples/factorial.sl
```

---

## 📚 Documentation Files

### Main Documentation

| File | Purpose | Read When... |
|------|---------|--------------|
| **README.md** | Project overview | You want a high-level introduction |
| **QUICK_START.md** | Getting started guide | You want to start using the compiler |
| **PROJECT_SUMMARY.md** | Complete project details | You want comprehensive information |
| **INDEX.md** | This file | You want to navigate the project |

### Technical Documentation

| File | Purpose | Read When... |
|------|---------|--------------|
| **docs/LANGUAGE_SPEC.md** | Complete language specification | You want to learn SimpleLang syntax and grammar |

---

## 🧪 Testing

| File | Purpose | Usage |
|------|---------|-------|
| **test.sh** | Automated test script | `./test.sh` |

---

## 📖 Reading Order

### For Learning Compiler Construction

1. **README.md** - Understand the project
2. **LANGUAGE_SPEC.md** - Learn the language
3. **TokenType.java** - See what tokens exist
4. **Lexer.java** - Learn tokenization
5. **ASTNode.java** - Understand AST structure
6. **Parser.java** - Learn parsing
7. **SymbolTable.java** - Understand symbol management
8. **SemanticAnalyzer.java** - Learn type checking

### For Using the Compiler

1. **QUICK_START.md** - Get started quickly
2. **examples/*.sl** - See example programs
3. **Main.java** - Understand usage options
4. **CompilerGUI.java** - Learn GUI features

### For Understanding Implementation

1. **PROJECT_SUMMARY.md** - Get complete overview
2. **Source code** - Read in order above
3. **Test examples** - See it in action

---

## 🎯 File Purposes

### Phase 1: Lexical Analysis
- `TokenType.java` - Defines token types
- `Token.java` - Token data structure
- `Lexer.java` - Tokenization logic

### Phase 2: Syntax Analysis
- `ASTNode.java` - AST node definitions
- `Parser.java` - Parsing logic

### Phase 3: Semantic Analysis
- `SymbolTable.java` - Symbol storage
- `SemanticAnalyzer.java` - Type checking

### User Interface
- `CompilerGUI.java` - Graphical interface
- `Main.java` - Command-line interface

### Testing & Examples
- `test.sh` - Test automation
- `examples/*.sl` - Example programs

### Documentation
- `README.md` - Overview
- `QUICK_START.md` - Quick guide
- `PROJECT_SUMMARY.md` - Detailed summary
- `LANGUAGE_SPEC.md` - Language reference
- `INDEX.md` - This file

---

## 🔍 Finding Specific Information

### "How do I...?"

| Question | File to Check |
|----------|---------------|
| Run the compiler? | QUICK_START.md, Main.java |
| Write SimpleLang code? | LANGUAGE_SPEC.md, examples/*.sl |
| Understand tokens? | TokenType.java, Lexer.java |
| See the AST structure? | ASTNode.java |
| Learn about type checking? | SemanticAnalyzer.java |
| Use the GUI? | CompilerGUI.java, QUICK_START.md |
| Run tests? | test.sh, QUICK_START.md |

### "What does this file do?"

| File Pattern | Purpose |
|--------------|---------|
| `*.java` | Source code implementation |
| `*.sl` | SimpleLang example programs |
| `*.md` | Documentation in Markdown |
| `*.sh` | Shell scripts for automation |

---

## 📊 File Statistics

### Source Code
- **Total Files:** 9
- **Total Lines:** ~2,800
- **Language:** Java

### Documentation
- **Total Files:** 5
- **Total Lines:** ~2,500
- **Format:** Markdown

### Examples
- **Total Files:** 3
- **Total Lines:** ~45
- **Language:** SimpleLang

### Tests
- **Test Scripts:** 1
- **Test Cases:** 3

---

## 🚀 Quick Commands

### Compile Everything
```bash
javac -d bin src/*.java
```

### Run GUI
```bash
java -cp bin Main
```

### Compile a File
```bash
java -cp bin Main examples/factorial.sl
```

### Run Tests
```bash
./test.sh
```

### Show AST
```bash
java -Dshow.ast=true -cp bin Main examples/factorial.sl
```

---

## 📞 Quick Reference

### Most Important Files

1. **Main.java** - Start here to run the compiler
2. **QUICK_START.md** - Start here to learn usage
3. **LANGUAGE_SPEC.md** - Start here to learn the language
4. **Lexer.java** - Core lexical analysis
5. **Parser.java** - Core syntax analysis
6. **SemanticAnalyzer.java** - Core semantic analysis

### Entry Points

- **GUI Mode:** `CompilerGUI.main()`
- **CLI Mode:** `Main.main()`
- **Testing:** `test.sh`

---

## 🎓 Learning Path

### Beginner
1. Read README.md
2. Read QUICK_START.md
3. Run the GUI
4. Try examples

### Intermediate
1. Read LANGUAGE_SPEC.md
2. Study example programs
3. Write your own .sl programs
4. Explore the GUI features

### Advanced
1. Read PROJECT_SUMMARY.md
2. Study source code in order
3. Understand each phase
4. Modify and extend

---

**SimpleLang Compiler**  
Version: 1.0  
Date: May 17, 2026

*Use this index to quickly navigate the project and find what you need!*
