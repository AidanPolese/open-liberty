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
package com.ibm.ws.javaee.ddmodel.wsbnd.internal;

import javax.xml.namespace.QName;

/**
 *
 */
public class StringUtils {

    public static String getEJBServiceRefKey(String serviceRefName, String componentName) {
        return new StringBuilder().append(componentName.trim()).append(".").append(serviceRefName.trim()).toString();
    }

    /**
     * Check whether the target string is empty
     *
     * @param str
     * @return true if the string is null or the length is zero after trimming spaces.
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }

        int len = str.length();
        for (int x = 0; x < len; ++x) {
            if (str.charAt(x) > ' ') {
                return false;
            }
        }

        return true;
    }

    /**
     * remove the blank characters in the left and right for a given value.
     *
     * @param value
     * @return
     */
    public final static String trim(String value) {
        String result = null;
        if (null != value) {
            result = value.trim();
        }

        return result;
    }

    /**
     * build the qname with the given, and make sure the namespace is ended with "/" if specified.
     *
     * @param portNameSpace
     * @param portLocalName
     * @return
     */
    public static QName buildQName(String namespace, String localName) {
        String namespaceURI = namespace;
        if (!isEmpty(namespace) && !namespace.trim().endsWith("/")) {
            namespaceURI += "/";
        }

        return new QName(namespaceURI, localName);
    }
}
