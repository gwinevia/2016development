## DVM.java
機能追加(2016-10-22)

```java
  /**
   * ワークプレースにエージェントを呼び出した際に
   * エージェント名を日付.ｔｘｔに書き込み保存する
   * また,エージェント毎に呼び出した回数と最終使用日を
   * DBに保存する　　
   */
 void DashLog(String filename) {

   //ファイルにエージェント名を書き込む(使用エージェントログ)　
   try{
     LocalDateTime d = LocalDateTime.now();
     File file = new File("/home/mmk/krswmmk/idea/DashLog/" 
                            + d.getYear() + "_"
                            + d.getMonth().getValue() + "_"
                            + d.getDayOfMonth()
                            + ".txt");

     if (file.exists()){
        //
     }else{
        file.createNewFile();
     }

     FileWriter filewriter = new FileWriter(file, true);
     filewriter.write(filename + "¥n");

     filewriter.close();

   }catch(IOException e){
     System.out.println(e);
   }

   /** DBに使用回数と最終使用日を保存する
     * CSVファイルから接続するMySQLの情報を取得する
     */
   try {  
     File file = new File("/home/mmk/krswmmk/Java_programming/mysql_dashlog_test.csv");
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

   //DashLogを読み取りDBに格納
   try {
     LocalDateTime d = LocalDateTime.now();
     String time = String.valueOf(d.getYear()) 
        	   + String.valueOf(d.getMonth().getValue()) 
        	   + String.valueOf(d.getDayOfMonth());  			
     int time_int = Integer.parseInt(time);

     String[] name = filename.split("\\.",-1);

     int flag = 0;
     while(resultset.next()==true){
       if(resultset.getString("name").equals(name[0])){
          times = Integer.parseInt(resultset.getString("times")) + 1;
          sql = "UPDATE dashlog_test SET times = "
              + times 
              + ", date = "
              + time_int
              + " WHERE name = '" 
              + name[0]
              + "';";
          result = statement.executeUpdate(sql);
          flag = 1;
        }
     }

     resultset.close();

     if(flag==0){
       times = 1;
       sql = "INSERT INTO dashlog_test (name, times, date) VALUES ('"
              + name[0] 
              + "'," 
              + times 
              + "," 
              + time_int
              + ");";
       result = statement.executeUpdate(sql); 
     }
   } catch (SQLException e) {                
     e.printStackTrace();
   }
 }
 ```