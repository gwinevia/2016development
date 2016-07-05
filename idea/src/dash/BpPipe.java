package dash;

import java.io.*;

// auther "yu"
// BpPipe means BaseprocessPipe
public class BpPipe {
    private PipedOutputStream os;
    private PipedInputStream is;
    
    public BpPipe() {
	try {
	    os = new PipedOutputStream();
	    is = new PipedInputStream(os);
	}
	catch(IOException e) { e.printStackTrace(); }
    }
    
    public void close() {
	try {
	    os.close();
	    is.close();
	}
	catch(IOException e) { e.printStackTrace(); }
    }
    
    public InputStream getInputStream() {
	return is;
    }
    
    public OutputStream getOutputStream() {
	return os;
    }
    
}
