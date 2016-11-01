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