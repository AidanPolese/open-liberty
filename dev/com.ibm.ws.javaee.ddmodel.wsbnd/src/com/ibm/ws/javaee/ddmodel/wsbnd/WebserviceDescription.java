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

public interface WebserviceDescription {

    public static String WEBSERVICE_DESCRIPTION_NAME_ATTRIBUTE_NAME = "webservice-description-name";

    public static String WSDL_PUBLISH_LOCATION_ATTRIBUTE_NAME = "wsdl-publish-location";

    /**
     * @return webservice-description-name="..." attribute value
     */
    public String getWebserviceDescriptionName();

    /**
     * @return wsdl-publish-location="..." attribute value
     */
    public String getWsdlPublishLocation();
}
