/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2000, 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.csi;

import com.ibm.websphere.csi.StatefulSessionKey;
import com.ibm.ws.util.UUID;

/**
 * Stateful session key instance just wraps a UUID and correctly
 * implements the hashCode() and equals() methods. <p>
 */
final class StatefulSessionKeyImpl implements StatefulSessionKey
{
    private static final long serialVersionUID = 9171900298892460198L;

    /**
     * Univeral Unique Identifier. The UUID is unique within a cluster.
     */
    transient private UUID ivUuid;

    /**
     * Construct a new instance using a specified UUID.
     * 
     * @param uuid is the UUID to use as the key.
     */
    public StatefulSessionKeyImpl(UUID uuid)
    {
        ivUuid = uuid;
    }

    public int hashCode()
    {
        if (ivUuid != null)
        {
            return ivUuid.hashCode();
        }
        else
        {
            return 0;
        }
    }

    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof StatefulSessionKeyImpl))
        {
            if (ivUuid != null)
            {
                StatefulSessionKeyImpl key = (StatefulSessionKeyImpl) obj;
                return ivUuid.equals(key.ivUuid);
            }
        }
        return false;
    }

    /**
     * Returns true iff given object represents the same Stateful Session
     * EJB. <p>
     * 
     * This type specific version is provided for performance, and avoids
     * any instanceof or casting. <p>
     */
    // d195605
    public boolean equals(StatefulSessionKeyImpl key)
    {
        if (key != null)
        {
            if (ivUuid != null)
            {
                return ivUuid.equals(key.ivUuid);
            }
        }
        return false;
    }

    public byte[] getBytes()
    {
        if (ivUuid != null)
        {
            return ivUuid.toByteArray();
        }
        else
        {
            return null;
        }
    }

    /**
     * The value returned by toString must be a legal filename.
     */
    public String toString()
    {
        if (ivUuid != null)
        {
            return ivUuid.toString();
        }
        else
        {
            return null;
        }
    }

} // StatefulSessionKeyImpl
