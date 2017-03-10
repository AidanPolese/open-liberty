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
package com.ibm.ws.zos.core.structures.internal;

import java.nio.ByteBuffer;
import java.util.Map;

import com.ibm.ws.zos.core.structures.NativePsa;
import com.ibm.ws.zos.core.utils.DirectBufferHelper;

/**
 * Provides access to the z/OS Prefix Save Area (PSA)
 */
public class NativePsaImpl implements NativePsa {

    /**
     * Direct Buffer Helper object reference
     */
    private DirectBufferHelper directBufferHelper = null;

    /**
     * Control block constants, taken from the Data Areas books (z/OS 1.12 level if anybody cares)
     */
    protected static final int PSA_LENGTH = 4096;
    protected static final int PSA_FLCCVT_OFFSET = 0x10;
    protected static final int PSA_PSAAOLD_OFFSET = 0x224;
    protected static final int PSA_PSATOLD_OFFSET = 0x21C;

    /**
     * Default constructor to enable extension in test and needed for OSGi instantiation
     */
    public NativePsaImpl() {}

    /**
     * DS method to activate this component.
     * 
     * @param properties
     * 
     * @throws Exception
     */
    protected void activate(Map<String, Object> properties) throws Exception {}

    /**
     * DS method to deactivate this component.
     * 
     * @param reason The representation of reason the component is stopping
     */
    protected void deactivate() {}

    /**
     * Sets the DirectBufferHelper object reference
     * 
     * @param directBufferHelper The DirectBufferHelper reference
     */
    protected void setDirectBufferHelper(DirectBufferHelper directBufferHelper) {
        this.directBufferHelper = directBufferHelper;
    }

    protected void unsetDirectBufferHelper(DirectBufferHelper directBufferHelper) {
        if (this.directBufferHelper == directBufferHelper) {
            this.directBufferHelper = null;
        }
    }

    @Override
    public ByteBuffer mapMyPsa() {
        return directBufferHelper.getSlice(0L, PSA_LENGTH);
    }

    @Override
    public long getFLCCVT() {
        return directBufferHelper.getInt(PSA_FLCCVT_OFFSET);
    }

    @Override
    public long getPSAAOLD() {
        return directBufferHelper.getInt(PSA_PSAAOLD_OFFSET);
    }

    @Override
    public long getPSATOLD() {
        return directBufferHelper.getInt(PSA_PSATOLD_OFFSET);
    }
}
