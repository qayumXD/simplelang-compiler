#!/bin/bash

# SimpleLang Compiler Test Script
# Tests all three example programs

echo "======================================"
echo "SimpleLang Compiler - Test Suite"
echo "======================================"
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Compile the compiler
echo "Step 1: Compiling SimpleLang compiler..."
javac -d bin src/*.java 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Compilation successful${NC}"
else
    echo -e "${RED}✗ Compilation failed${NC}"
    exit 1
fi
echo ""

# Test counter
PASSED=0
FAILED=0

# Test function
test_file() {
    local file=$1
    local name=$2
    
    echo "--------------------------------------"
    echo "Testing: $name"
    echo "--------------------------------------"
    
    java -cp bin Main "$file" > /tmp/simplelang_test.out 2>&1
    
    if [ $? -eq 0 ] && grep -q "COMPILATION SUCCESSFUL" /tmp/simplelang_test.out; then
        echo -e "${GREEN}✓ PASSED${NC}"
        PASSED=$((PASSED + 1))
    else
        echo -e "${RED}✗ FAILED${NC}"
        cat /tmp/simplelang_test.out
        FAILED=$((FAILED + 1))
    fi
    echo ""
}

# Run tests
echo "Step 2: Running tests..."
echo ""

test_file "examples/hello.sl" "Hello World"
test_file "examples/factorial.sl" "Factorial (Recursion)"
test_file "examples/variables.sl" "Variables and Types"

# Summary
echo "======================================"
echo "Test Summary"
echo "======================================"
echo -e "Passed: ${GREEN}$PASSED${NC}"
echo -e "Failed: ${RED}$FAILED${NC}"
echo "Total:  $((PASSED + FAILED))"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✓ All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}✗ Some tests failed${NC}"
    exit 1
fi
