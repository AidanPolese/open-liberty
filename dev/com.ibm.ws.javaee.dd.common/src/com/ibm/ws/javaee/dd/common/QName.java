/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.common;

/**
 * Represents an XML qualified name. Examples:
 * 
 * <ol>
 * <li>&lt;service-name-pattern xmlns:ns1="http://test.ibm.com">ns1:EchoService&lt;/service-name-pattern>
 * <ul>
 * <li>getNamespaceURI() returns "http://test.ibm.com"
 * <li>getLocalPart() returns "EchoService"
 * </ul>
 * </li>
 * <li>&lt;service-name-pattern xmlns:ns1="http://test.ibm.com">ns1:EchoService*&lt;/service-name-pattern>
 * <ul>
 * <li>getNamespaceURI() returns "http://test.ibm.com"
 * <li>getLocalPart() returns "EchoService*"
 * </ul>
 * </li>
 * <li>&lt;service-name-pattern>EchoService&lt;/service-name-pattern>
 * <ul>
 * <li>getNamespaceURI() returns null
 * <li>getLocalPart() returns "EchoService"
 * </ul>
 * </li>
 * <li>&lt;service-name-pattern>*&lt;/service-name-pattern>
 * <ul>
 * <li>getNamespaceURI() returns null
 * <li>getLocalPart() returns "*"
 * </ul>
 * </li>
 * </ol>
 * 
 * @see javax.xml.namespace.QName
 */
public interface QName
{
    /**
     * @return the namespace URI associated with the QName prefix, or null if
     *         this QName does not have a prefix
     */
    String getNamespaceURI();

    /**
     * @return the local name
     */
    String getLocalPart();
}
