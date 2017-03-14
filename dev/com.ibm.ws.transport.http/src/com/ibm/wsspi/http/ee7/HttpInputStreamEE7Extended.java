/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.http.ee7;

import java.io.IOException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.http.channel.internal.HttpMessages;
import com.ibm.ws.http.dispatcher.internal.HttpDispatcher;
import com.ibm.wsspi.channelfw.InterChannelCallback;

/**
 *
 */
public class HttpInputStreamEE7Extended extends HttpInputStreamEE7 {

    /** trace variable */
    private static final TraceComponent tc = Tr.register(HttpInputStreamEE7Extended.class,
                                                         HttpMessages.HTTP_TRACE_NAME,
                                                         HttpMessages.HTTP_BUNDLE);

    byte[][] postData;
    int postDataPos;

    public HttpInputStreamEE7Extended(byte[][] data) {
        super(null);
        postData = data;
        postDataPos = 0;
        buffer = null;
        closed = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.http.ee7.HttpInputStreamExtended#initialRead()
     */
    @Override
    public void initialRead() {
        if (this.postDataPos == 0 && this.postData[0].length > 0) {
            this.setNextBuffer();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.http.ee7.HttpInputStreamExtended#asyncCheckBuffers(com.ibm.wsspi.channelfw.InterChannelCallback)
     */
    @Override
    public boolean asyncCheckBuffers(InterChannelCallback callback) {
        try {
            return checkBuffer();
        } catch (IOException ioe) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "We should never get to this point, exception was : " + ioe);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.http.HttpInputStream#checkBuffer()
     */
    @Override
    protected boolean checkBuffer() throws IOException {
        if (null != this.buffer) {
            if (this.buffer.hasRemaining()) {
                return true;
            }
            this.buffer.release();
            this.buffer = null;
        }
        return setNextBuffer();
    }

    @Override
    public boolean isFinished() {
        if (null != this.buffer && this.buffer.hasRemaining())
            return false;
        return (this.postDataPos >= this.postData.length);
    }

    private boolean setNextBuffer() {
        if (this.postDataPos < this.postData.length) {
            this.buffer = HttpDispatcher.getBufferManager().allocate(this.postData[this.postDataPos].length);
            this.buffer.put(this.postData[this.postDataPos]);
            this.buffer.flip();
            this.postDataPos++;
            this.bytesRead += this.buffer.remaining();

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "SetNextBuffer", "Use Buffer " + postDataPos + " of " + this.postData.length + " buffer length = " + this.buffer.remaining());
            }

            return true;
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "SetNextBuffer", "AllDataRead");
        }

        return false;
    }

}
