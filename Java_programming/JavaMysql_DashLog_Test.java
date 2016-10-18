//DashLogをDBに保存するテスト
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.io.*;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;

 
public class JavaMysql_DashLog_Test {

    static String URL = "";
    static String USERNAME = "";
    static String PASSWORD = "";

    static String sql = "";
    static int result = 0;
    static int times = 1;

    static String sql_st = "SELECT * FROM dashlog_test;";
    static Connection connection; 
    static Statement statement;
    static PreparedStatement statement2;
    static ResultSet resultset;



    public static void main(String[] args) {

        //CSVファイルから接続するMySQLの情報を取得する
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
      	  File file2 = new File("/home/mmk/krswmmk/idea/DashLog/" 
      	                        	+ d.getYear() + "_"
      	                        	+ d.getMonth().getValue() + "_"
      	                        	+ d.getDayOfMonth()
      	                        	+ ".txt");

      	  if (file2.exists()){
            FileInputStream input = new FileInputStream(file2);  
            InputStreamReader stream = new InputStreamReader(input,"UTF-8");
            BufferedReader buffer = new BufferedReader(stream);
        
            String line;
             
            while ((line = buffer.readLine()) != null) {
  
                byte[] b = line.getBytes();
                line = new String(b, "UTF-8");
                String[] columns = line.split("\\.",-1);

                MySql(columns[0],d);
          
            }
      
            input.close();
            stream.close();
            buffer.close();
      	  }

      	}catch (UnsupportedEncodingException | FileNotFoundException e) {
          
          e.printStackTrace();
  
        }catch (IOException e) {

        	System.out.println(e);

      	}
                 
    }

    public static void MySql (String name, LocalDateTime date){


        try {

        		String time = String.valueOf(date.getYear()) 
        									+ String.valueOf(date.getMonth().getValue()) 
        									+ String.valueOf(date.getDayOfMonth());
       			
       			int time_int = Integer.parseInt(time);
            
            if(resultset.next()==true && resultset.getString("name").equals(name)){
            	System.out.println(resultset.getString("times"));
            	times = Integer.parseInt(resultset.getString("times")) + 1;
            	System.out.println(times);

            	//UPDATE uriage SET price = 1000 WHERE id = 10;

            	sql = "UPDATE dashlog_test SET times = "
            					+ times 
            					+ ", date = "
            					+ time_int
            					+ " WHERE name = '" 
            					+ name
            					+ "';";
            	result = statement.executeUpdate(sql);

            }else {
            	times = 1;
            	sql = "INSERT INTO dashlog_test (name, times, date) VALUES ('"
            					+ name 
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
     
}