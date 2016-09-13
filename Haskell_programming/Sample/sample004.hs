add x y = x + y
-- 2つの引数を取る関数を`で囲むと中置演算子として使用できます
main = do
    print $ add 1 2
    print $ 1 `add` 2

{-	
	中置演算子を()で囲むと関数として使用できます
	main = do
    print $ 1 + 2
    print $ (+) 1 2
-}