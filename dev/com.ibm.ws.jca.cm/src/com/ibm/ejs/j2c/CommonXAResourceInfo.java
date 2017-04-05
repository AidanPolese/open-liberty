/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.j2c;

import java.io.Serializable;

/**
 * This interface contains elements of the XAResourceInfo that are common between
 * the server and the Embeddable EJB Container.
 */
public interface CommonXAResourceInfo extends Serializable {
    String getCfName();

    CMConfigData getCmConfig();

}
