/*
 * @start_prolog@
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 * 
 * Change activity:
 * 
 * Issue       Date        Name        Description
 * ----------- ----------- --------    ------------------------------------
 */
package com.ibm.wsspi.classloading;

import java.io.Serializable;

/**
 * This interface represents the identity of a classloader. An identity
 * consists of a domain, for example ear, war, osgi and a domain specific identity.
 * This allows two applications with the same identity to not clash.
 * 
 * @see ClassLoadingService#createIdentity(String, String)
 */
public interface ClassLoaderIdentity extends Serializable {
    String getDomain();

    String getId();
}
