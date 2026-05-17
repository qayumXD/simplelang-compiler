# SimpleLang Language Specification

## Overview

**SimpleLang** is a statically-typed, modern programming language designed for clarity and simplicity. It combines the best features of Python, JavaScript, and Go.

---

## Design Philosophy

1. **Readable** - Python-like syntax, no semicolons
2. **Safe** - Static typing with type inference
3. **Modern** - First-class functions, closures
4. **Simple** - Easy to learn and use

---

## Syntax Overview

### 1. Variables and Types

```simplelang
# Variable declaration with type
let x: int = 10
let name: string = "Alice"
let price: float = 99.99
let isActive: bool = true

# Type inference
let y = 20              # inferred as int
let greeting = "Hello"  # inferred as string
```

### 2. Data Types

**Primitive Types:**
- `int` - Integer numbers (32-bit)
- `float` - Floating-point numbers
- `string` - Text strings
- `bool` - Boolean (true/false)

**Composite Types:**
- `array<T>` - Arrays of type T
- `object` - Key-value objects

### 3. Operators

**Arithmetic:**
```simplelang
let sum = 10 + 5        # Addition
let diff = 10 - 5       # Subtraction
let prod = 10 * 5       # Multiplication
let quot = 10 / 5       # Division
let mod = 10 % 3        # Modulo
```

**Comparison:**
```simplelang
x == y    # Equal
x != y    # Not equal
x < y     # Less than
x > y     # Greater than
x <= y    # Less than or equal
x >= y    # Greater than or equal
```

**Logical:**
```simplelang
x and y   # Logical AND
x or y    # Logical OR
not x     # Logical NOT
```

### 4. Control Flow

**If Statement:**
```simplelang
if x > 10:
    print("x is greater than 10")
elif x > 5:
    print("x is greater than 5")
else:
    print("x is 5 or less")
```

**While Loop:**
```simplelang
let i = 0
while i < 10:
    print(i)
    i = i + 1
```

**For Loop:**
```simplelang
for i in range(0, 10):
    print(i)

for item in items:
    print(item)
```

### 5. Functions

**Function Declaration:**
```simplelang
func add(a: int, b: int) -> int:
    return a + b

func greet(name: string) -> string:
    return "Hello, " + name

func printMessage(msg: string) -> void:
    print(msg)
```

**Function Calls:**
```simplelang
let result = add(5, 3)
let greeting = greet("Alice")
printMessage("Welcome!")
```

### 6. Arrays

```simplelang
# Array declaration
let numbers: array<int> = [1, 2, 3, 4, 5]
let names: array<string> = ["Alice", "Bob", "Charlie"]

# Array access
let first = numbers[0]
numbers[1] = 10

# Array methods
let length = numbers.length()
numbers.append(6)
```

### 7. Objects

```simplelang
# Object declaration
let person = {
    name: "Alice",
    age: 30,
    city: "New York"
}

# Object access
let personName = person.name
person.age = 31
```

### 8. Comments

```simplelang
# This is a single-line comment

# Multi-line comments
# can be written
# with multiple # symbols
```

---

## Grammar (BNF)

```bnf
<program>        ::= <statement>*

<statement>      ::= <var_decl>
                   | <assignment>
                   | <if_stmt>
                   | <while_stmt>
                   | <for_stmt>
                   | <func_decl>
                   | <return_stmt>
                   | <expr_stmt>

<var_decl>       ::= "let" <identifier> (":" <type>)? "=" <expression>

<assignment>     ::= <identifier> "=" <expression>

<if_stmt>        ::= "if" <expression> ":" <block>
                     ("elif" <expression> ":" <block>)*
                     ("else" ":" <block>)?

<while_stmt>     ::= "while" <expression> ":" <block>

<for_stmt>       ::= "for" <identifier> "in" <expression> ":" <block>

<func_decl>      ::= "func" <identifier> "(" <params>? ")" ("->" <type>)? ":" <block>

<return_stmt>    ::= "return" <expression>?

<expr_stmt>      ::= <expression>

<block>          ::= <statement>+

<expression>     ::= <logical_or>

<logical_or>     ::= <logical_and> ("or" <logical_and>)*

<logical_and>    ::= <equality> ("and" <equality>)*

<equality>       ::= <comparison> (("==" | "!=") <comparison>)*

<comparison>     ::= <term> (("<" | ">" | "<=" | ">=") <term>)*

<term>           ::= <factor> (("+" | "-") <factor>)*

<factor>         ::= <unary> (("*" | "/" | "%") <unary>)*

<unary>          ::= ("not" | "-") <unary> | <call>

<call>           ::= <primary> ("(" <arguments>? ")" | "[" <expression> "]" | "." <identifier>)*

<primary>        ::= <number>
                   | <string>
                   | <boolean>
                   | <identifier>
                   | <array_literal>
                   | <object_literal>
                   | "(" <expression> ")"

<array_literal>  ::= "[" (<expression> ("," <expression>)*)? "]"

<object_literal> ::= "{" (<identifier> ":" <expression> ("," <identifier> ":" <expression>)*)? "}"

<type>           ::= "int" | "float" | "string" | "bool" | "void"
                   | "array" "<" <type> ">"

<params>         ::= <identifier> ":" <type> ("," <identifier> ":" <type>)*

<arguments>      ::= <expression> ("," <expression>)*
```

---

## Example Programs

### Example 1: Hello World
```simplelang
func main() -> void:
    print("Hello, World!")
```

### Example 2: Factorial
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

### Example 3: Array Sum
```simplelang
func sum(numbers: array<int>) -> int:
    let total = 0
    for num in numbers:
        total = total + num
    return total

func main() -> void:
    let nums = [1, 2, 3, 4, 5]
    let result = sum(nums)
    print(result)  # Output: 15
```

### Example 4: Person Object
```simplelang
func main() -> void:
    let person = {
        name: "Alice",
        age: 30,
        city: "New York"
    }
    
    print(person.name)  # Output: Alice
    print(person.age)   # Output: 30
```

---

## Type System

### Type Checking Rules

1. **Variable Declaration:**
   - Must have explicit type or initializer
   - Type must match initializer if both present

2. **Assignment:**
   - Variable must be declared
   - Right-hand side type must match variable type

3. **Function Calls:**
   - Argument count must match parameter count
   - Argument types must match parameter types
   - Return type must match function signature

4. **Operators:**
   - Arithmetic: operands must be int or float
   - Comparison: operands must be same type
   - Logical: operands must be bool

5. **Arrays:**
   - All elements must be same type
   - Index must be int

6. **Objects:**
   - Field access must be valid
   - Field types are inferred

---

## Semantic Rules

1. **Variable Scope:**
   - Variables are block-scoped
   - Inner scopes can shadow outer scopes
   - Variables must be declared before use

2. **Function Scope:**
   - Functions are globally scoped
   - Functions can be called before declaration (hoisting)
   - Recursive calls are allowed

3. **Type Inference:**
   - Variables without explicit type get type from initializer
   - Function return types can be inferred from return statements

4. **Control Flow:**
   - If/elif/else conditions must be bool
   - While conditions must be bool
   - For loops iterate over arrays or ranges

---

## Keywords

```
let       # Variable declaration
func      # Function declaration
if        # Conditional
elif      # Else if
else      # Else
while     # While loop
for       # For loop
in        # For loop iterator
return    # Return statement
true      # Boolean true
false     # Boolean false
and       # Logical AND
or        # Logical OR
not       # Logical NOT
int       # Integer type
float     # Float type
string    # String type
bool      # Boolean type
void      # Void type
array     # Array type
object    # Object type
range     # Range function
print     # Print function
```

---

## Token Types

```
# Literals
NUMBER          # 123, 45.67
STRING          # "hello", 'world'
BOOLEAN         # true, false

# Identifiers
IDENTIFIER      # variable names, function names

# Keywords
LET, FUNC, IF, ELIF, ELSE, WHILE, FOR, IN, RETURN
TRUE, FALSE, AND, OR, NOT
INT, FLOAT, STRING, BOOL, VOID, ARRAY, OBJECT

# Operators
PLUS            # +
MINUS           # -
STAR            # *
SLASH           # /
PERCENT         # %
EQUAL           # =
EQUAL_EQUAL     # ==
NOT_EQUAL       # !=
LESS            # <
GREATER         # >
LESS_EQUAL      # <=
GREATER_EQUAL   # >=

# Delimiters
LPAREN          # (
RPAREN          # )
LBRACKET        # [
RBRACKET        # ]
LBRACE          # {
RBRACE          # }
COLON           # :
COMMA           # ,
DOT             # .
ARROW           # ->

# Special
NEWLINE         # \n (significant for indentation)
INDENT          # Indentation increase
DEDENT          # Indentation decrease
EOF             # End of file
```

---

## Implementation Phases

### Phase 1: Lexical Analysis
- Tokenize source code
- Handle indentation (Python-style)
- Recognize keywords, identifiers, literals
- Track line and column numbers

### Phase 2: Syntax Analysis
- Build Abstract Syntax Tree (AST)
- Implement recursive descent parser
- Handle operator precedence
- Validate syntax

### Phase 3: Semantic Analysis
- Type checking
- Symbol table management
- Scope resolution
- Function signature validation
- Variable declaration checking

---

## Error Messages

### Lexical Errors
```
Error: Unexpected character '@' at line 5, column 10
Error: Unterminated string at line 3
Error: Invalid number format at line 7
```

### Syntax Errors
```
Error: Expected ':' after if condition at line 10
Error: Expected expression after '=' at line 15
Error: Unmatched parenthesis at line 20
```

### Semantic Errors
```
Error: Variable 'x' not declared at line 12
Error: Type mismatch: expected int, got string at line 18
Error: Function 'foo' expects 2 arguments, got 3 at line 25
Error: Cannot assign to constant at line 30
```

---

## Future Extensions

1. **Classes and Objects**
2. **Modules and Imports**
3. **Generics**
4. **Pattern Matching**
5. **Async/Await**
6. **Error Handling (try/catch)**

---

**SimpleLang** - Simple, Safe, Modern

Version: 1.0  
Date: May 16, 2026
