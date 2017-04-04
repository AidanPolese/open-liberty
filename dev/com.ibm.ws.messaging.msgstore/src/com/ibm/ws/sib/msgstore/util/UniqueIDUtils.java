package com.ibm.ws.sib.msgstore.util;
/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 *  Reason         Date    Origin    Description
 * ------------- -------- --------- -------------------------------------------
 * SIB0003.ms.1  23/05/05  gareth   Turn on SpillDispatcher
 * 306998.20     09/01/06  gareth   Add new guard condition to trace statements
 * ============================================================================
 */

import com.ibm.ws.sib.msgstore.MessageStoreConstants;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.utils.ras.SibTr;

public class UniqueIDUtils
{
    private static TraceComponent tc = SibTr.register(UniqueIDUtils.class,
                                                      MessageStoreConstants.MSG_GROUP,
                                                      MessageStoreConstants.MSG_BUNDLE);

    /**
     * The UUID for this incarnation of ME is generated
     * using the hashcode of this object and the least
     * significant four bytes of the current time in
     * milliseconds.
     * 
     * @param caller The calling object used in the generation of the incarnation UUID
     * 
     * @return The generated incarnation UUID
     */
    public static String generateIncUuid(Object caller)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "generateIncUuid", "Caller="+caller);

        java.util.Date time = new java.util.Date();

        int  hash   = caller.hashCode();
        long millis = time.getTime();

        byte[] data = new byte[] {(byte)(hash>>24),
            (byte)(hash>>16),
            (byte)(hash>>8),
            (byte)(hash),
            (byte)(millis>>24), 
            (byte)(millis>>16), 
            (byte)(millis>>8), 
            (byte)(millis)};

        String digits = "0123456789ABCDEF";
        StringBuffer retval = new StringBuffer(data.length*2);

        for (int i = 0; i < data.length; i++)
        {
            retval.append(digits.charAt((data[i] >> 4) & 0xf));
            retval.append(digits.charAt(data[i] & 0xf));
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "generateIncUuid", "return="+retval);
        return retval.toString();
    }
}
