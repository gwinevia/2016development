import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.*;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;

 
public class JavaMysqlTest {

    static String URL = "";
    static String USERNAME = "";
    static String PASSWORD = "";

    public static void main(String[] args) {

        //CSVファイルから接続するMySQLの情報を取得する
        try {
      
            File file = new File("/home/mmk/krswmmk/Java_programming/mysql.csv");
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
      
            input.close();
            stream.close();
            buffer.close();
  
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
  
        } catch (IOException e) {
            e.printStackTrace();
  
        }
         
        try (
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); 
                Statement statement = connection.createStatement();
                ) {
       
            String sql = "INSERT INTO music (name, title, year) VALUES ('sample1', 'Butterflies', 2001);";
            int result = statement.executeUpdate(sql);
 
            sql = "INSERT INTO music (name, title, year) VALUES ('sample2', 'Groove La Chord', 1998);";
            result = statement.executeUpdate(sql);
             
        } catch (SQLException e) {
                 
            e.printStackTrace();
                 
        }
         
    }
     
}