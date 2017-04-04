// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
//382943        08/09/06    todkap             remove SUN dependencies from core webcontainer
//LIDB3518-1.1  06-23-07    mmolden             ARD
//

package com.ibm.wsspi.webcontainer.util;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

import com.ibm.websphere.servlet.response.IResponse;

public abstract class WSServletOutputStream extends ServletOutputStream
        implements ResponseBuffer {

    /**
     * Clear all data written to the response.
     * This method can only be called if the response is uncommitted.
     */
    public abstract void clearBuffer();

    /**
     * Query the response to determine if any response data has been
     * written to the client.  A response is considered committed once
     * any data from the response has been sent to the client.
     */
    public abstract boolean isCommitted();

    /**
     * Flush any buffered data that has been written to the response.
     * After this method is called, the response will be considered
     * committed (if it wasn't already).
     */
    public abstract void flushBuffer() throws IOException;

    /**
     * Set the size of the output buffer for this response.
     * @param bufferSize the size of the buffer
     */
    public abstract void setBufferSize(int size);

    /**
     * Get the size of the output buffer for this response.
     */
    public abstract int getBufferSize();

    /**
     * Sets an observer for this output stream. The observer will be
     * notified when the stream is first written to.
     * @param obs the IOutpuStreamObserver associated with this response
     */
    public abstract void setObserver(IOutputStreamObserver obs);
    
    /**
     * Sets an observer for this output stream. The observer will be
     * notified when the stream is first written to.
     * @param obs the IOutpuStreamObserver associated with this response
     */
    public abstract void addObserver(IOutputStreamObserver obs);

    
    /**
     * Resets the output stream for a new connection.
     */
    public abstract void reset();
    
    /**
     * Initializes the output stream with the specified raw output stream.
     * @param out the raw output stream
     * @param bufferSize the size of the buffer
     */
    public abstract void init(OutputStream out, int bufferSize);
    
    /**
     * Sets the maximum number of bytes that can be written. This is initially
     * set to -1 in order to indicate that observers must be notified. A value of
     * -1 is also used to indicate no limit).
     * @param contentLength the max amount of data this buffer can hold. Over this 
     * buffer size will result in an IOException being thrown.
     */
    public abstract void setLimit( int contentLength);
    
    /**
     * Initializes the response with the underlying transport or channel response object
     * @param response the underlying IResponse object
     */
    public abstract void setResponse(IResponse response);
    public abstract int getTotal();

}
