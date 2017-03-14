/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.http.channel.internal.inbound;

import java.io.IOException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.http.channel.internal.HttpMessages;
import com.ibm.wsspi.bytebuffer.WsByteBuffer;
import com.ibm.wsspi.http.HttpInputStream;
import com.ibm.wsspi.http.channel.inbound.HttpInboundServiceContext;

/**
 * Wrapper for an incoming HTTP request message body that provides the input
 * stream interface.
 */
public class HttpInputStreamImpl extends HttpInputStream {
    /** trace variable */
    private static final TraceComponent tc = Tr.register(HttpInputStreamImpl.class,
                                                         HttpMessages.HTTP_TRACE_NAME,
                                                         HttpMessages.HTTP_BUNDLE);

    protected HttpInboundServiceContext isc = null;
    protected WsByteBuffer buffer = null;
    private IOException error = null;
    protected boolean closed = false;
    protected long bytesRead = 0L;
    private long bytesToCaller = 0L;

    /**
     * Constructor.
     *
     * @param context
     */
    public HttpInputStreamImpl(HttpInboundServiceContext context) {
        this.isc = context;
    }

    /*
     * @see java.lang.Object#toString()
     */
    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.http.channel.internal.inbound.HttpInputStreamX#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append(getClass().getSimpleName());
        sb.append('@').append(Integer.toHexString(hashCode()));
        sb.append(" closed=").append(this.closed);
        sb.append(" error=").append(this.error);
        sb.append(" received=").append(this.bytesRead);
        sb.append(" given=").append(this.bytesToCaller);
        sb.append(" buffer=").append(this.buffer);
        return sb.toString();
    }

    /**
     * Perform validation of the stream before processing external requests
     * to read data.
     *
     * @throws IOException
     */
    protected void validate() throws IOException {
        if (isClosed()) {
            throw new IOException("Stream is closed");
        }
        if (null != this.error) {
            throw this.error;
        }
    }

    /**
     * Check the input buffer for data. If necessary, attempt a read for a new
     * buffer.
     *
     * @return boolean - true means data is ready
     * @throws IOException
     */
    protected boolean checkBuffer() throws IOException {
        if (null != this.buffer) {
            if (this.buffer.hasRemaining()) {
                return true;
            }
            this.buffer.release();
            this.buffer = null;
        }
        try {
            this.buffer = this.isc.getRequestBodyBuffer();
            if (null != this.buffer) {
                // record the new amount of data read from the channel
                this.bytesRead += this.buffer.remaining();
                // Tr.debug(tc, "Buffer=" + WsByteBufferUtils.asString(this.buffer));
                return true;
            }
            return false;
        } catch (IOException e) {
            this.error = e;
            throw e;
        }
    }

    @Override
    public int available() throws IOException {
        validate();
        int rc = (null == this.buffer) ? 0 : this.buffer.remaining();
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "available: " + rc);
        }
        return rc;
    }

    @Override
    public void close() throws IOException {
        if (isClosed()) {
            return;
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Closing stream: " + this);
        }
        if (null != this.buffer) {
            this.buffer.release();
            this.buffer = null;
        }
        validate();
        this.closed = true;
    }

    @Override
    final public boolean isClosed() {
        return this.closed;
    }

    @Override
    public synchronized void mark(int readlimit) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Ignoring call to mark()");
        }
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public int read() throws IOException {
        validate();
        int rc = -1;
        if (checkBuffer()) {
            rc = this.buffer.get() & 0x000000FF;
            this.bytesToCaller++;
        }
        // if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
        // Tr.debug(tc, "read() rc=" + rc);
        // }
        return rc;
    }

    @Override
    public int read(byte[] output, int offset, int length) throws IOException {
        validate();
        if (0 == length) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "read(byte[],int,int), target length was 0");
            }
            return 0;
        }
        if (!checkBuffer()) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "read(byte[],int,int), EOF");
            }
            return -1;
        }
        int avail = this.buffer.remaining();
        int amount = (length > avail) ? avail : length;
        this.buffer.get(output, offset, amount);
        // if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
        // Tr.debug(tc, "read(byte[],int,int) rc=" + amount);
        // }
        this.bytesToCaller += amount;
        return amount;
    }

    @Override
    public int read(byte[] output) throws IOException {
        return read(output, 0, output.length);
    }

    @Override
    public synchronized void reset() throws IOException {
        throw new IOException("Mark not supported");
    }

    @Override
    public long skip(long target) throws IOException {
        validate();
        // if we're at EOF already, return -1
        if (!checkBuffer()) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "skip(" + target + "), EOF");
            }
            return -1L;
        }
        // otherwise cycle through buffers until we reach the target or EOF
        long total = 0L;
        long remaining = target;
        while (total < target) {
            if (!checkBuffer()) {
                // reached EOF
                break; // out of while
            }
            int avail = this.buffer.remaining();
            if (avail > remaining) {
                // this buffer satisfies the remaining length
                this.buffer.position(this.buffer.position() + (int) remaining);
                total += remaining;
            } else {
                // we're skipping the entire contents of this buffer
                this.buffer.release();
                this.buffer = null;
                total += avail;
                remaining -= avail;
            }

        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "skip(" + target + ") rc=" + total);
        }
        // while we didn't actually give them to the caller, we have "used"
        // these bytes from the input stream
        this.bytesToCaller += total;
        return total;
    }
}
