package dash;

import java.util.Hashtable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class Clipman {
    static private Clipman cm = new Clipman();

    private Hashtable ht = new Hashtable();

    public Clipman() {

    }
    
    
    
    
    public static Clipman getInstance() {
	return cm;
    }

    public void put(Object key, Object o) {
	ht.put(key, o);
    }

    public Object get(Object key) {
	return ht.get(key);
    }


    public void putSerialized(Object key, Object o) {
	try {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.writeObject(o);
	    oos.flush();
	    baos.close();
	    ht.put(key, baos.toByteArray());
	} catch(IOException ioe) {
	    ioe.printStackTrace();
	}
    }

    public ObjectInputStream getObjectInputStream(Object key) {
	try {
	    Object obj = ht.get(key);
	    if(obj != null) {
		ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) obj);
		ObjectInputStream ois = new ObjectInputStream(bais);
		return ois;
	    }
	    else return null;
	} catch(IOException ioe) {
	    ioe.printStackTrace();
	    return null;
	}
    }

    public void remove(Object key) {
    	ht.remove(key);
    }
}

