-- フィボナッチ数
fib 0 = 0
fib 1 = 1
fib n | n > 1 = fib (n - 2) + fib (n - 1)

main = do
    print $ fib 6