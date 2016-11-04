# Memo

## ブランチ関連 

* リモートにブランチをpushしてそのままトラックする  
```git push```に```-u```あるいは```--set-upstream```を付けると  
push先のブランチをトラック(デフォルトでpushやpullの対象に)するように設定される
	- ```git push -u origin master```  
	- ```git push --set-upstream origin development```


## commit関連

* 特定のファイルを，あるコミットの時の状態に戻したい時  
```git checkout [コミット番号] [ファイルパス]```


## ファイル管理関連

* 既に git 管理しているファイルをあえて無視したい  
gitでファイルを無視するには,通常は```.gitignore```や```.git/info/exclude```を使う  
しかし,既にgit管理下にあるファイルは,これらの設定があっても無視されない  
以下の方法を使えば,git管理下にあるファイルをあえて無視することが可能  
	- ```git update-index --assume-unchanged [ファイル名]```
	- この設定を取り消す時：```git update-index --no-assume-unchanged [ファイル名]```
	- 上記の設定がされているファイルを確認する時：```git ls-files -v```