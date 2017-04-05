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
package com.ibm.ws.javaee.ddmodel.wsbnd;

import java.util.List;
import java.util.Map;

public interface ServiceRef {

    public static String NAME_ATTRIBUTE_NAME = "name";

    public static String COMPONENT_NAME_ATTRIBUTE_NAME = "component-name";

    public static String PORT_ADDRESS_ATTRIBUTE_NAME = "port-address";

    public static String WSDL_LOCATION_ATTRIBUTE_NAME = "wsdl-location";

    public static String PORT_ELEMENT_NAME = "port";

    public static String PROPERTIES_ELEMENT_NAME = "properties";

    /**
     * @return name="..." attribute value
     */
    public String getName();

    /**
     * @return component-name="..." attribute value
     */
    public String getComponentName();

    /**
     * @return port-address="..." attribute value
     */
    public String getPortAddress();

    /**
     * @return wsdl-location="..." attribute value
     */
    public String getWsdlLocation();

    /**
     * @return &lt;Port> as a list
     */
    public List<Port> getPorts();

    /**
     * @return &lt;properties> attributes as a list
     * 
     * @return
     */
    public Map<String, String> getProperties();
}
