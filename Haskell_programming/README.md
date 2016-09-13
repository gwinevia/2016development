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

* ```$ runghc sample.hs```
* コンパイルして実行ファイルを作ることも可能  
	- ```$ ghc sample.hs```
	- ```$ ./sample```

## 参考サイト

* [Haskell 事始め](http://qiita.com/HirofumiYashima/items/c666196515c74bca9d75)
* [Haskell 超入門](http://qiita.com/7shi/items/145f1234f8ec2af923ef)
* [こわくないHaskell入門（初級）](http://qiita.com/arowM/items/9ebfb7cafecd99290663#%E5%AE%9F%E8%A1%8C%E6%96%B9%E6%B3%95)