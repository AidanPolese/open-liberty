/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2000, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ejs.csi;

import com.ibm.websphere.csi.StatefulSessionKey;
import com.ibm.websphere.csi.StatefulSessionKeyFactory;
import com.ibm.ws.util.UUID;

/**
 * This class provides a simple factory for keys for stateful session beans.
 */
public class SessionKeyFactoryImpl implements StatefulSessionKeyFactory
{
    /**
     * Return newly created <code>StatefulSessionKey</code> instance.
     * Note, the returned object will be for a key that is unique
     * within a cluster.
     */
    @Override
    public StatefulSessionKey create()
    {
        UUID uuid = new UUID(); // d204278
        return new StatefulSessionKeyImpl(uuid);
    }

    /**
     * Create using bytes from a previously generated unique UUID.
     *
     * @param bytes are the bytes from the unique UUID.
     */
    @Override
    public StatefulSessionKey create(byte[] bytes)
    {
        return new StatefulSessionKeyImpl(new UUID(bytes));
    }

} // SessionKeyFactoryImpl
