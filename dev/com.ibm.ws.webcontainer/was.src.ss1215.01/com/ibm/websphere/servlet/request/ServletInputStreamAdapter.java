// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.websphere.servlet.request;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletInputStream;

/**
 * 
 * Adapter class creates a ServletInputStream from a java.io.InputStream.
 * This class will proxy all method calls to the underlying stream.
 *
 * @ibm-api 
 */
public class ServletInputStreamAdapter extends ServletInputStream{
    private InputStream _in;

    /**
     * Creates a ServletInputStream from an InputStream.
     */
    public ServletInputStreamAdapter(InputStream in){
        _in = in;
    }
    public int read() throws IOException{
        return _in.read();
    }
    public int available() throws IOException{
        return _in.available();
    }
    public int read(byte b[]) throws IOException{
        return _in.read(b);
    }
    public int read(byte b[], int off, int len) throws IOException{
        return _in.read(b, off, len);
    }
    public synchronized void mark(int readlimit){
        _in.mark(readlimit);
    }
    public long skip(long n) throws IOException{
        return _in.skip(n);
    }
    public boolean markSupported() {
        return _in.markSupported();
    }
    public void close() throws IOException{
        _in.close();
    }
    public synchronized void reset() throws IOException{
        _in.reset();
    }
}
