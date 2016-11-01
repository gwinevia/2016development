# Memo

## ブランチ関連 

* リモートにブランチを```push```してそのままトラックする  
```git push```に```-u```あるいは```--set-upstream```を付けると  
```push```先のブランチをトラック(デフォルトで```push```や```pull```の対象に)するように設定される
	- ```git push -u origin master```  
	- ```git push --set-upstream origin development```

<!-- 
## commit関連

* 特定のファイルを，あるコミットの時の状態に戻したい時  
```git checkout [コミット番号] [ファイルパス]``` -->