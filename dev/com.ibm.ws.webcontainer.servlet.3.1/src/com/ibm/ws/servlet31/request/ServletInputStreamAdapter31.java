// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.servlet31.request;
import java.io.InputStream;

import javax.servlet.ReadListener;

import com.ibm.websphere.servlet.request.ServletInputStreamAdapter;

/**
 * 
 * Adapter class creates a ServletInputStream from a java.io.InputStream.
 * This class will proxy all method calls to the underlying stream.
 *
 * @ibm-api 
 */
public class ServletInputStreamAdapter31 extends ServletInputStreamAdapter {
    //protected static final TraceNLS nls = TraceNLS.getTraceNLS(ServletInputStreamAdapter31.class, "com.ibm.ws.webcontainer.resources.Messages");

    /**
     * Creates a ServletInputStream from an InputStream.
     */
    public ServletInputStreamAdapter31(InputStream in){
        super(in);
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletInputStream#isFinished()
     */
    @Override
    public boolean isFinished() {
        return false;
    }
    /* (non-Javadoc)
     * @see javax.servlet.ServletInputStream#isReady()
     */
    @Override
    public boolean isReady() {
        return false;
    }
    /* (non-Javadoc)
     * @see javax.servlet.ServletInputStream#setReadListener(javax.servlet.ReadListener)
     */
    @Override
    public void setReadListener(ReadListener arg0) {
        return;        
    }
}
