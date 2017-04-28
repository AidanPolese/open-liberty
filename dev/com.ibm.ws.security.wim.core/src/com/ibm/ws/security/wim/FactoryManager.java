/************** Begin Copyright - Do not add comments here **************
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 *
 */
package com.ibm.ws.security.wim;

import com.ibm.ws.security.wim.env.ICacheUtil;
import com.ibm.ws.security.wim.env.IEncryptionUtil;
import com.ibm.ws.security.wim.env.ISSLUtil;

/**
 * Factory Manager class to return environment specific classes for WebSphere environment
 */
public class FactoryManager {

    public static ICacheUtil getCacheUtil() {
        return new com.ibm.ws.security.wim.env.was.Cache();
    }

    public static ISSLUtil getSSLUtil() {
        return new com.ibm.ws.security.wim.env.was.SSLUtilImpl();
    }

    public static IEncryptionUtil getEncryptionUtil() {
        return new com.ibm.ws.security.wim.env.was.EncryptionUtilImpl();
    }
}
