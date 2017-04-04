/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2002, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ejs.util.dopriv;

import java.security.PrivilegedAction;

/**
 * This class gets a system property while in privileged mode. Its purpose
 * is to eliminate the need to use an anonymous inner class in multiple modules
 * throughout the product, when the only privileged action required is to
 * get the value of a system property.
 */
public class SystemGetPropertyPrivileged implements PrivilegedAction<String> {
    private final String propertyName;
    private String propertyValue;
    private String propertyDefault = null;//d138969

    public SystemGetPropertyPrivileged(String propName) {
        propertyName = propName;
    }

    //d138969
    public SystemGetPropertyPrivileged(String propName, String propDefault) {
        propertyName = propName;
        propertyDefault = propDefault;
    }

    //d138969
    @Override
    public String run() {
        propertyValue = System.getProperty(propertyName, propertyDefault);//d138969
        return propertyValue;
    }

    public String getValue() {
        return propertyValue;
    }
} // SystemGetPropertyPrivileged

