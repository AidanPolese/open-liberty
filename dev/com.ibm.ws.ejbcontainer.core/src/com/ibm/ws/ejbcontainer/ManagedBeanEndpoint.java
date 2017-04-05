/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015, 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer;

import com.ibm.websphere.csi.J2EEName;

/**
 * Basic information about a managed bean.
 */
public interface ManagedBeanEndpoint {

    /**
     * @return the j2EE name for managed bean.
     */
    J2EEName getJ2EEName();

    /**
     * @return the managed bean name, unique within the module; may be null.
     */
    String getName();

    /**
     * @return the class name of this managed bean.
     */
    String getClassName();
}
