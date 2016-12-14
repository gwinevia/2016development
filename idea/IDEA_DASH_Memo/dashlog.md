#	DASHのエージェント使用ログについて
*	ps / ProdSys.javaにてDBへのデータ書き込み処理を実行

### MySQLに接続

```java
     try {  
       File file = new File(getFilePath() + "mysql_dashlog.csv");
       FileInputStream input = new FileInputStream(file);
       InputStreamReader stream = new InputStreamReader(input,"UTF-8");
       BufferedReader buffer = new BufferedReader(stream);
        
       String line = buffer.readLine();
             
       byte[] b = line.getBytes();
       line = new String(b, "UTF-8");
       String[] columns = line.split(",",-1);
                  
       URL = "jdbc:mysql://" + columns[0] + "/" + columns[1];
       USERNAME = columns[2];
       PASSWORD = columns[3];

       connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); 
       statement = connection.createStatement();
       statement2 = connection.prepareStatement(sql_st);
       resultset = statement2.executeQuery();
       input.close();
       stream.close();
       buffer.close();
  
     } catch (SQLException e) {                
       e.printStackTrace();        
     } catch (UnsupportedEncodingException | FileNotFoundException e) {
       e.printStackTrace();
     } catch (IOException e) {
       e.printStackTrace();
     }
```

### 日付，バージョン，エージェント名

```java
       LocalDateTime d = LocalDateTime.now();
       String time = DateForLog(d);  			
       int time_int = Integer.parseInt(time);
       
       int i = oav.lastIndexOf(":agname");
       int j = oav.lastIndexOf(")");

       String name = oav.substring(i+8,j);
       String version = oav.substring(14,54);
```

### データをDBに保存する処理

* DBに既にエージェント名が登録されている時
	- 前回使ったバージョンと同じ
		- " tmp_version "がNULLではない
		- " tmp_version "がNULL
	- " tmp_version "がNULLではない
		- " tmp_version "と今回のバージョンが一緒
		- " checkout_ver "がNULL：バージョンがいくつか進んでしまっていた時のため
		- " checkout_ver "がNULLではない(※不必要な気がする)
	- " tmp_version "がNULLかつ今回のバージョンと" checkout_ver "が同じ
	- その他：前回使ったバージョンではないバージョンの1回目の使用
* DBに初めてそのエージェントの情報を登録する時

```java
       int flag = 0;
       while(resultset.next()==true){

         if(resultset.getString("name").equals(name) && version.length()==40 && !agname_tmp.equals(name)){
            agname_tmp = name; //flag
            if(resultset.getString("version").equals(version)){
              if(resultset.getString("tmp_version") != null){
                times = Integer.parseInt(resultset.getString("times")) + 1;
                sql = "UPDATE dashlog SET times = "
                    + times 
                    + ", date = "
                    + time_int
                    + ", tmp_version = NULL"
                    + " WHERE name = '" 
                    + name
                    + "';";
                result = statement.executeUpdate(sql);
                flag = 1;
              }else{
                times = Integer.parseInt(resultset.getString("times")) + 1;
                sql = "UPDATE dashlog SET times = "
                    + times 
                    + ", date = "
                    + time_int
                    + " WHERE name = '" 
                    + name
                    + "';";
                result = statement.executeUpdate(sql);
                flag = 1;
              }
            }else if(resultset.getString("tmp_version") != null){
              if(resultset.getString("tmp_version").equals(version)){
                times = Integer.parseInt(resultset.getString("times")) + 1;
                sql = "UPDATE dashlog SET times = "
                    + times 
                    + ", date = "
                    + time_int
                    + ", version = '"
                    + version
                    + "' , tmp_version = NULL"
                    + " WHERE name = '" 
                    + name
                    + "';";
                result = statement.executeUpdate(sql);
                flag = 1;
              }else if(resultset.getString("checkout_ver")==null){
                times = Integer.parseInt(resultset.getString("times")) + 1;
                sql = "UPDATE dashlog SET times = "
                    + times 
                    + ", date = "
                    + time_int
                    + ", tmp_version = '"
                    + version
                    + "' WHERE name = '" 
                    + name
                    + "';";
                result = statement.executeUpdate(sql);
                flag = 1;              
	      }else{
                times = Integer.parseInt(resultset.getString("times")) + 1;
                sql = "UPDATE dashlog SET times = "
                    + times 
                    + ", date = "
                    + time_int
                    + ", version = '"
                    + version
                    + "', tmp_version = NULL"
                    + ", checkout_after = '"
                    + version
                    + "' WHERE name = '" 
                    + name
                    + "';";
                result = statement.executeUpdate(sql);
                flag = 1;              
              }
             //first use
            }else if(resultset.getString("tmp_version")==null 
                     && resultset.getString("version").equals(resultset.getString("checkout_ver"))){
              times = Integer.parseInt(resultset.getString("times")) + 1;
              sql = "UPDATE dashlog SET times = "
                  + times 
                  + ", date = "
                  + time_int
                  + ", version = '"
                  + version
                  + "' ,checkout_after = '"
                  + version
                  + "' WHERE name = '" 
                  + name
                  + "';";
              result = statement.executeUpdate(sql);
              flag = 1;			
            }else{
              times = Integer.parseInt(resultset.getString("times")) + 1;
              sql = "UPDATE dashlog SET times = "
                  + times 
                  + ", date = "
                  + time_int
                  + ", tmp_version = '"
                  + version
                  + "' WHERE name = '" 
                  + name
                  + "';";
              result = statement.executeUpdate(sql);
              flag = 1;
            }
         }
       }

       resultset.close();

       //new agent
       if(flag==0 && !agname_tmp.equals(name)){
         agname_tmp = name;
         times = 1;
         sql = "INSERT INTO dashlog (name, times, date, version) VALUES ('"
                + name
                + "'," 
                + times 
                + "," 
                + time_int
                + ",'"
                + version
                + "');";
         result = statement.executeUpdate(sql); 
       }
     } catch (SQLException e) {                
       e.printStackTrace();
     }
```