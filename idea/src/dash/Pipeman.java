package dash;

import java.io.*;
import java.util.*;

// author "yu"
public class Pipeman {
    static private Pipeman pm = new Pipeman();

    private Hashtable ht = new Hashtable();

    public Pipeman() {

    }
    
    public static Pipeman getInstance() {
	return pm;
    }
    
    public void create(String key) {
	ht.put(key, new BpPipe());
    }
    
    public void close(String key) {
	((BpPipe)ht.remove(key)).close();
    }
    
    public InputStream getInputStream(String key) {
	return ((BpPipe)ht.get(key)).getInputStream();
    }

    public OutputStream getOutputStream(String key) {
	return ((BpPipe)ht.get(key)).getOutputStream();
    }
}



