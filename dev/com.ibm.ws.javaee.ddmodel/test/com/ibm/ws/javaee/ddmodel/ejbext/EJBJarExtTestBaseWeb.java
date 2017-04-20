/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.ejbext;

/**
 * Test the WEB-INF/ejb-jar-ext.xml parser
 */

public class EJBJarExtTestBaseWeb extends EJBJarExtTestBase {

    public EJBJarExtTestBaseWeb() {
        super();
        isWarModule = true;
    }

}
