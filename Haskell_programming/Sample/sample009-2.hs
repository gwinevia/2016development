-- sample008-2.hsの書き換えver.
fib n
    | n == 0 = 0
    | n == 1 = 1
    | n >  1 = fib (n - 2) + fib (n - 1)

main = do
    print $ fib 6