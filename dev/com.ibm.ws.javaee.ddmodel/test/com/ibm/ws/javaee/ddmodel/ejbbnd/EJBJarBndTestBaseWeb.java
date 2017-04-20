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
package com.ibm.ws.javaee.ddmodel.ejbbnd;

/**
 * Test the WEB-INF/ejb-jar-bnd.xml parser
 */

public class EJBJarBndTestBaseWeb extends EJBJarBndTestBase {

    public EJBJarBndTestBaseWeb() {
        super();
        isWarModule = true;
    }

}
