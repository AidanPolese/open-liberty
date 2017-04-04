/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.wsbnd;

import java.util.Map;

public interface WebserviceEndpoint {

    public static String PORT_COMPONENT_NAME_ATTRIBUTE_NAME = "port-component-name";

    public static String ADDRESS_ATTRIBUTE_NAME = "address";

    public static String PROPERTIES_ELEMENT_NAME = "properties";

    /**
     * @return port-component-name="..." attribute value
     */
    public String getPortComponentName();

    /**
     * @return address="..." attribute value
     */
    public String getAddress();

    /**
     * @return all attributes defined in the properties element
     */
    public Map<String, String> getProperties();
}
