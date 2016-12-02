import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class file_search {
	public static String[] execCommand(String cmd) throws IOException,InterruptedException {
	    return execCommand(new String[] { cmd });
	}
	
	public static String[] execCommand(String[] cmds) throws IOException,InterruptedException {
	    String[] returns = new String[1];
	    String LINE_SEPA = System.getProperty("line.separator");
	    Runtime r = Runtime.getRuntime();
	    Process p = r.exec(cmds);
	    InputStream in = null;
	    BufferedReader br = null; 
	    try {
	        in = p.getInputStream();
	        StringBuffer out = new StringBuffer();
	        br = new BufferedReader(new InputStreamReader(in));
	        String line;
	        while ((line = br.readLine()) != null) {
	            out.append(line + LINE_SEPA);
	        }
	        returns[0] = out.toString();
	        br.close();
	        in.close();

	        return returns;
	    } finally {
	        if (br != null) {
	            br.close();
	        }
	        if (in != null) {
	            in.close();
	        }
	    }
	}
	
	public static void main(String[] args) {
	    try {
	    String[] s = execCommand(new String[]{"pwd"});
	    System.out.println(
	            "●[標準出力]\n"+s[0]);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}