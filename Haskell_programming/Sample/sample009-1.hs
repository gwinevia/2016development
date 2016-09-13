-- 1つの関数定義に引数の条件を列挙するガードという書き方があります
fact n
    | n == 0    = 1
    | otherwise = n * fact (n - 1)

main = do
    print $ fact 5