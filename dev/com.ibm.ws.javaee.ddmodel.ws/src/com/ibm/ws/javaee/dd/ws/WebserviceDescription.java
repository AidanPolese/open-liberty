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
package com.ibm.ws.javaee.dd.ws;

import java.util.List;

import com.ibm.ws.javaee.dd.common.Description;
import com.ibm.ws.javaee.dd.common.DisplayName;
import com.ibm.ws.javaee.dd.common.Icon;

public interface WebserviceDescription {

    /**
     * @return &lt;description>, or null if unspecified
     */
    public Description getDescription();

    /**
     * @return &lt;display-name>, or null if unspecified
     */
    public DisplayName getDisplayName();

    /**
     * @return &lt;icon>, or null if unspecified
     */
    public Icon getIcon();

    public String getWebserviceDescriptionName();

    public String getWSDLFile();

    public List<PortComponent> getPortComponents();

    public String getJAXRPCMappingFile();
}
