/*
 * IBM Confidential
 * 
 * OCO Source Materials
 * 
 * Copyright IBM Corp. 2010
 * 
 * The source code for this program is not published or other- wise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */
package com.ibm.ws.webcontainer31.util;

import javax.servlet.WriteListener;

import com.ibm.wsspi.webcontainer.util.ByteBufferOutputStream;

public class ByteBufferOutputStream31 extends ByteBufferOutputStream {

    //private static TraceNLS nls = TraceNLS.getTraceNLS(ByteBufferOutputStream31.class, "com.ibm.ws.webcontainer31.resources.Messages");

    public ByteBufferOutputStream31() {
        super();
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
