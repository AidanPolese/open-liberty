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

import com.ibm.ws.zos.core.structures.NativeCvt;
import com.ibm.ws.zos.core.structures.NativePsa;
import com.ibm.ws.zos.core.utils.DirectBufferHelper;

/**
 * Provides access to the z/OS CVT.
 */
public class NativeCvtImpl implements NativeCvt {

    /**
     * PSA Object
     */
    private NativePsa nativePsa = null;

    /**
     * Direct Buffer Helper object reference
     */
    private DirectBufferHelper directBufferHelper = null;

    /**
     * Control block constants, taken from the Data Areas books (z/OS 1.12 level if anybody cares)
     */
    protected static final int CVT_LENGTH = 1280;
    protected static final int CVT_CVTSNAME_OFFSET = 0x154;
    protected static final int CVT_CVTSNAME_LENGTH = 8;
    protected static final int CVT_CVTECVT_OFFSET = 0x8C;
    protected static final int CVT_CVTOSLV6_OFFSET = 0x4F6;
    protected static final int CVT_CVTOPCTP_OFFSET = 0x25C; // Page 1063 - z/OS V2R2 MVS Data Areas Volume 1 (ABE - IAX)

    /**
     * Default constructor to enable extension in test and needed for OSGi instantiation
     */
    public NativeCvtImpl() {}

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
     * Sets the NativePsa object reference.
     *
     * @param nativePsa The NativePsa reference.
     */
    protected void setNativePsa(NativePsa nativePsa) {
        this.nativePsa = nativePsa;
    }

    /**
     * Unsets the NativePsa object reference.
     *
     * @param nativePsa The NativePsa reference.
     */
    protected void unsetNativePsa(NativePsa nativePsa) {
        if (this.nativePsa == nativePsa) {
            this.nativePsa = null;
        }
    }

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
    public ByteBuffer mapMyCvt() {
        return directBufferHelper.getSlice(nativePsa.getFLCCVT(), CVT_LENGTH);
    }

    @Override
    public byte[] getCVTSNAME() {
        byte[] cvtsname = new byte[CVT_CVTSNAME_LENGTH];
        directBufferHelper.get(nativePsa.getFLCCVT() + CVT_CVTSNAME_OFFSET, cvtsname);
        return cvtsname;
    }

    @Override
    public long getCVTECVT() {
        return directBufferHelper.getInt(nativePsa.getFLCCVT() + CVT_CVTECVT_OFFSET);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.zos.core.structures.NativeCvt#getCVTOSLV6()
     */
    @Override
    public int getCVTOSLV6() {
        return directBufferHelper.getInt(nativePsa.getFLCCVT() + CVT_CVTOSLV6_OFFSET);
    }

    @Override
    public long getCVTOPCTP() {
        return directBufferHelper.getInt(nativePsa.getFLCCVT() + CVT_CVTOPCTP_OFFSET);
    }
}
