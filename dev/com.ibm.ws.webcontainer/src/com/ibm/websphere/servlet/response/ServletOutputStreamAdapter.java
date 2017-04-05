// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.websphere.servlet.response;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

import com.ibm.ejs.ras.TraceNLS;

/**
 * @ibm-api
 * Adapter class creates a ServletOutputStream from a java.io.OutputStream.
 * This class will proxy all method calls to the underlying stream.
 */
public class ServletOutputStreamAdapter extends ServletOutputStream{
    private OutputStream _out;
    protected static final TraceNLS nls = TraceNLS.getTraceNLS(ServletOutputStreamAdapter.class, "com.ibm.ws.webcontainer.resources.Messages");

    /**
     * Creates a ServletOutputStream from an OutputStream.
     */
    public ServletOutputStreamAdapter(OutputStream out){
        _out = out;
    }

    public void write(int b) throws IOException{
        _out.write(b);
    }

    public void close() throws IOException{
        _out.close();
    }

    public void write(byte b[]) throws IOException{
        _out.write(b);
    }

    public void write(byte b[], int off, int len) throws IOException{
        _out.write(b, off, len);
    }

    public void flush() throws IOException{
        _out.flush();
    }

}
