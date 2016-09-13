# Haskell_programming

## インストール

* 環境

```
$ cat /etc/lsb-release
DISTRIB_ID=Ubuntu
DISTRIB_RELEASE=15.10
DISTRIB_CODENAME=wily
DISTRIB_DESCRIPTION="Ubuntu 15.10"
```

* インストール   
```sudo apt-get install haskell-platform```  
上記のコマンドだけで次の全てが一括でインストールされる  
	- GHC コンパイラ
	- GHCi 対話的インタプリタ
	- GHC ランタイム
	- Cabal ライブラリ管理ツール
	- Haddock ドキュメンテーション・ツール
	- GHCi debugger
	- Happy parser generator
	- Alex lexer generator
	- hsc2hs foreign language bindig tool
	- GHC profiler
	- Haskel Code Coverage

## 実行方法