/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.ejb;

import com.ibm.ws.javaee.dd.ejb.EJBJar;

public abstract class EJBJarDDParserVersion {
    /**
     * Service property name with a value corresponding to {@link EJBJar#getVersionID} that
     * indicates the maximum version that the runtime supports.
     */
    public static final String VERSION = "version";
}
