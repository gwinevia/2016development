## 全データ削除
テーブルに含まれる全データを削除する場合：```TRUNCATE TABLE (テーブル名);```

## カラムの追加と削除

* 作成済みのテーブルにカラムを追加する：```ALTER TABLE (テーブル名) ADD (カラム名) (追加するカラム定義);```  
	- デフォルトでは追加したカラムは既存のテーブルの最後に追加される  
	- カラムの先頭に表示する場合は次のように「FIRST」を指定：  
```ALTER TABLE (テーブル名) ADD (カラム名) (追加するカラム定義) FIRST;```
	- 指定したカラムの後に挿入する場合は次のように「AFTER」の後にカラム名を指定：  
```ALTER TABLE (テーブル名) ADD (カラム名) (追加するカラム定義) AFTER (カラム名);```

## 参考
* [全データの削除](http://www.dbonline.jp/mysql/insert/index12.html)
* [カラムの追加と削除](http://www.dbonline.jp/mysql/table/index20.html)