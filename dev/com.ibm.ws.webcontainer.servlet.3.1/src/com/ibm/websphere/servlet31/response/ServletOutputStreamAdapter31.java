// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.websphere.servlet31.response;
import java.io.OutputStream;

import javax.servlet.WriteListener;

import com.ibm.websphere.servlet.response.ServletOutputStreamAdapter;

/**
 * @ibm-api
 * Adapter class creates a ServletOutputStream from a java.io.OutputStream.
 * This class will proxy all method calls to the underlying stream.
 */
public class ServletOutputStreamAdapter31 extends ServletOutputStreamAdapter {

    //protected static final TraceNLS nls = TraceNLS.getTraceNLS(ServletOutputStreamAdapter31.class, "com.ibm.ws.webcontainer.resources.Messages");

    /**
     * Creates a ServletOutputStream31 from an OutputStream.
     */
    public ServletOutputStreamAdapter31(OutputStream out){
        super(out);
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletOutputStream#isReady()
     */
    @Override
    public boolean isReady() {
        return false;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletOutputStream#setWriteListener(javax.servlet.WriteListener)
     */
    @Override
    public void setWriteListener(WriteListener arg0) {
        return;        
    }
}
