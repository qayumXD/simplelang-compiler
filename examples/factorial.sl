# SimpleLang Example: Factorial

func factorial(n: int) -> int:
    if n <= 1:
        return 1
    else:
        return n * factorial(n - 1)

func main() -> void:
    let num = 5
    let result = factorial(num)
    print("Factorial of")
    print(num)
    print("is")
    print(result)
