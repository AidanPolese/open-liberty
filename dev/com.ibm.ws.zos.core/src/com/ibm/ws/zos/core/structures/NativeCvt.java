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
package com.ibm.ws.zos.core.structures;

import java.nio.ByteBuffer;

/**
 * Provides access to the z/OS Common Vectory Table (CVT)
 */
public interface NativeCvt {

    /**
     * Get a {@code DirectByteBuffer} that maps the z/OS Common Vector Table (CVT)
     */
    public ByteBuffer mapMyCvt();

    /**
     * Get the z/OS System name from the CVT
     *
     * @return The system name, in EBCDIC
     */
    public byte[] getCVTSNAME();

    /**
     * Get the ECVT Pointer
     *
     * @return The ECVT pointer
     */
    public long getCVTECVT();

    /**
     * Get CVTOSLV6 from the cvt
     *
     * @return CVTOSLV6 and the 3 bytes following it.
     */
    public int getCVTOSLV6();

    /**
     * Get the CVTOPCTP pointer from the CVT -- points to RMCT
     *
     * @return The CVTOPCTP pointer
     */
    public long getCVTOPCTP();

}
