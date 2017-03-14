/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

package com.ibm.ws.bytebuffer.internal;

import java.nio.ByteBuffer;
import java.util.Map;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.kernel.zos.NativeMethodManager;

/**
 * Implementation of a WsByteBuffer pool manager with native extensions.
 */
public class ZOSWsByteBufferPoolManagerImpl extends WsByteBufferPoolManagerImpl {

    private static final TraceComponent tc = Tr.register(ZOSWsByteBufferPoolManagerImpl.class,
                                                         MessageConstants.WSBB_TRACE_NAME,
                                                         MessageConstants.WSBB_BUNDLE);

    protected static native ByteBuffer allocateDirectByteBuffer(long size);

    protected static native void releaseDirectByteBuffer(ByteBuffer buffer);

    /**
     * Create the one WsByteBufferPool Manager that is to be used.
     * 
     * @param properties
     * @throws WsBBConfigException
     */
    public ZOSWsByteBufferPoolManagerImpl(Map<String, Object> properties, NativeMethodManager nativeMethodManager) throws WsBBConfigException {
        super(properties);
        nativeMethodManager.registerNatives(ZOSWsByteBufferPoolManagerImpl.class);
    }

    /**
     * Create a pool manager with the default configuration.
     * 
     * @throws WsBBConfigException
     */
    public ZOSWsByteBufferPoolManagerImpl() throws WsBBConfigException {
        super();
    }

    public ZOSWsByteBufferPoolManagerImpl(Map<String, Object> properties) throws WsBBConfigException {
        super(properties);
    }

    /**
     * Allocate the direct ByteBuffer that will be wrapped by the
     * input WsByteBuffer object.
     * 
     * @param buffer
     * @param size
     * @param overrideRefCount
     */
    @Override
    protected WsByteBufferImpl allocateBufferDirect(WsByteBufferImpl buffer,
                                                    int size, boolean overrideRefCount) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "allocateBufferDirect: " + size);
        }
        buffer.setByteBufferNonSafe(allocateDirectByteBuffer(size));

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "allocateBufferDirect");
        }
        return buffer;
    }

    /**
     * Method called when a buffer is being destroyed to allow any
     * additional cleanup that might be required.
     * 
     * @param buffer
     */
    @Override
    protected void releasing(ByteBuffer buffer) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "releasing: " + buffer);
        }
        if (buffer != null && buffer.isDirect())
            releaseDirectByteBuffer(buffer);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "releasing");
        }
    }

}
