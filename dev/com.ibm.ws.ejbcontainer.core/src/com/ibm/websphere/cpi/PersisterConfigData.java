/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 1999, 2000
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.cpi;

import java.util.*;

/**
 * Persister specific config information which will be returned by
 * EJBConfigData. Can't think of anything more generic than a
 * Properties object for now.
 * 
 * @see com.ibm.websphere.csi.EJBConfigData
 */

public interface PersisterConfigData {

    /**
     * getProperties returns config properties for the Persister
     * 
     * @return Properties the properties for this persister.
     */
    public Properties getProperties();
}
