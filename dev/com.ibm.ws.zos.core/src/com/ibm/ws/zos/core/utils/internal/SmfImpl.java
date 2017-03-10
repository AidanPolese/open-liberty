/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.core.utils.internal;

import java.util.Map;

import com.ibm.ws.kernel.zos.NativeMethodManager;
import com.ibm.ws.zos.core.utils.Smf;

/**
 *
 */
public class SmfImpl implements Smf {

    /**
     * NativeMethodManager reference.
     */
    protected NativeMethodManager nativeMethodManager = null;

    /**
     * DS method to activate this component.
     * 
     * @param properties
     * 
     * @throws Exception
     */
    protected void activate(Map<String, Object> properties) throws Exception {
        this.nativeMethodManager.registerNatives(SmfImpl.class);
    }

    /**
     * DS method to deactivate this component.
     * 
     * @param reason The representation of reason the component is stopping
     */
    protected void deactivate() {}

    /**
     * Default constructor to enable extension in test and OSGi instantiation.
     */
    public SmfImpl() {}

    /**
     * Sets the NativeMethodManager object reference.
     * 
     * @param nativeMethodManager The NativeMethodManager reference.
     */
    protected void setNativeMethodManager(NativeMethodManager nativeMethodManager) {
        this.nativeMethodManager = nativeMethodManager;
    }

    /**
     * Unsets the NativeMethodManager object reference.
     * 
     * @param nativeMethodManager The NativeMethodManager reference.
     */
    protected void unsetNativeMethodManager(NativeMethodManager nativeMethodManager) {
        if (this.nativeMethodManager == nativeMethodManager) {
            this.nativeMethodManager = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.zos.core.utils.Smf#SmfRecordT120S11Write(byte[])
     */
    @Override
    public int smfRecordT120S11Write(byte[] data) {
        return ntv_SmfRecordT120S11Write(data);
    }

    /**
     * Native Services
     */

    protected native int ntv_SmfRecordT120S11Write(byte[] data);

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.zos.core.utils.Smf#SmfRecordT120S12Write(byte[])
     */
    @Override
    public int smfRecordT120S12Write(byte[] data) {
        return ntv_SmfRecordT120S12Write(data);
    }

    /**
     * Native Services
     */

    protected native int ntv_SmfRecordT120S12Write(byte[] data);

}
