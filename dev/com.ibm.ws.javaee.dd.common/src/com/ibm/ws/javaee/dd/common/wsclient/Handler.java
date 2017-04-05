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
package com.ibm.ws.javaee.dd.common.wsclient;

import java.util.List;

import com.ibm.ws.javaee.dd.common.DescriptionGroup;
import com.ibm.ws.javaee.dd.common.ParamValue;
import com.ibm.ws.javaee.dd.common.QName;

/**
 * Represents &lt;handler>.
 */
public interface Handler
                extends DescriptionGroup
{
    /**
     * @return &lt;handler-name>
     */
    String getHandlerName();

    /**
     * @return &lt;handler-class>
     */
    String getHandlerClassName();

    /**
     * @return &lt;init-param> as a read-only list
     */
    List<ParamValue> getInitParams();

    /**
     * @return &lt;soap-header> as a read-only list
     */
    List<QName> getSoapHeaders();

    /**
     * @return &lt;soap-role> as a read-only list
     */
    List<String> getSoapRoles();

    /**
     * @return &lt;port-name> as a read-only list
     */
    List<String> getPortNames();
}
