package com.ibm.ws.sib.msgstore.persistence.objectManager;
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
 *  Reason         Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 * 251161          14/04/05 gareth   Add ObjectManager code to CMVC
 * 292187          21/07/05 gareth   Improve toString() for trace
 * 306998.20       09/01/06 gareth   Add new guard condition to trace statements
 * SIB0112b.ms.1   07/08/06 gareth   Large message support.
 * SIB0112b.ms.2   08/09/06 gareth   Large message support, v61 to v70 migration
 * ============================================================================
 */

import com.ibm.ws.objectManager.*;

import com.ibm.ws.sib.msgstore.MessageStoreConstants;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.utils.ras.SibTr;


public class PersistableRawData extends ManagedObject
{
    private static final long serialVersionUID = -3290688556719366254L;

    private static TraceComponent tc = SibTr.register(PersistableRawData.class, 
                                                      MessageStoreConstants.MSG_GROUP, 
                                                      MessageStoreConstants.MSG_BUNDLE);

    private byte[] _data;


    public void setData(byte[] data)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "setData", "Data="+toHexString(data));

        _data = data;

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "setData");
    }

    public byte[] getData()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        {
            SibTr.entry(tc, "getData");
            SibTr.exit(tc, "getData", "return="+toHexString(_data));
        }
        return _data;
    }

    /**
     * Replace the state of this object with the same object in some other state.
     * Used for to restore the before image if a transaction rolls back.
     * @param other is the object this object is to become a clone of.
     */
    public void becomeCloneOf(ManagedObject clone)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "becomeCloneOf", "Clone="+clone);

        PersistableRawData object = (PersistableRawData)clone;

        _data = object._data;

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "becomeCloneOf");
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());

        // Defect 292187
        // don't output the byte array on toString as this causes
        // excessive output in trace.
        buffer.append("(PersistableRawData[ BINARYDATA ])");

        return buffer.toString();
    }

    /**
     * Utility method that allows us to have at least one point in the 
     * trace whee we get an output of the byte contents of a persistable's
     * data.
     */
    private String toHexString(byte [] b)
    {
        StringBuffer retval = null;

        if (b != null)
        {
            if (b.length > 0)
            {
                String digits = "0123456789abcdef";
                retval = new StringBuffer(b.length*2);

                for (int i = 0; i < b.length; i++)
                {
                    retval.append(digits.charAt((b[i] >> 4) & 0xf));
                    retval.append(digits.charAt(b[i] & 0xf));
                }
            }
            else
            {
                retval = new StringBuffer("empty");
            }
        }
        else
        {
            retval = new StringBuffer("null");
        }

        return(retval.toString());
    }
}

