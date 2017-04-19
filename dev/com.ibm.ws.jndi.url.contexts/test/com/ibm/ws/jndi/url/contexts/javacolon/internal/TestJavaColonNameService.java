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
package com.ibm.ws.jndi.url.contexts.javacolon.internal;

import javax.naming.NamingException;

import com.ibm.ws.container.service.naming.NamingConstants;

public class TestJavaColonNameService extends JavaColonNameService {
    String moduleName;
    String appName;

    void setModuleName(String name) {
        moduleName = name;
    }

    void setAppName(String name) {
        appName = name;
    }

    @Override
    protected String getModuleName(NamingConstants.JavaColonNamespace namespace, String name) throws NamingException {
        return moduleName;
    }

    @Override
    protected String getAppName(NamingConstants.JavaColonNamespace namespace, String name) throws NamingException {
        return appName;
    }
}
