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
package com.ibm.ws.javaee.ddmodel.wsbnd.impl;

import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.StringType;
import com.ibm.ws.javaee.ddmodel.DDParser.Diagnostics;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;
import com.ibm.ws.javaee.ddmodel.wsbnd.WebserviceDescription;

/*
 <xsd:complexType name="webserviceType">
 <xsd:attribute name="service-name" type="xsd:string" use="required" />
 <xsd:attribute name="wsdl-publish-location" type="xsd:string" />
 </xsd:complexType>
 */
public class WebserviceDescriptionType extends DDParser.ElementContentParsable implements WebserviceDescription {

    private StringType webserviceDescriptionName;

    private StringType wsdlPublishLocation;

    @Override
    public String getWebserviceDescriptionName() {
        return webserviceDescriptionName != null ? webserviceDescriptionName.getValue() : null;
    }

    @Override
    public String getWsdlPublishLocation() {
        return wsdlPublishLocation != null ? wsdlPublishLocation.getValue() : null;
    }

    @Override
    public boolean handleChild(DDParser parser, String localName) throws ParseException {
        return false;
    }

    @Override
    public boolean handleAttribute(DDParser parser, String nsURI, String localName, int index) throws ParseException {
        boolean result = false;

        if (nsURI != null) {
            return result;
        }

        if (WEBSERVICE_DESCRIPTION_NAME_ATTRIBUTE_NAME.equals(localName)) {
            webserviceDescriptionName = parser.parseStringAttributeValue(index);
            result = true;
        } else if (WSDL_PUBLISH_LOCATION_ATTRIBUTE_NAME.equals(localName)) {
            wsdlPublishLocation = parser.parseStringAttributeValue(index);
            result = true;
        }

        return result;
    }

    @Override
    public void describe(Diagnostics diag) {
        diag.describe(WEBSERVICE_DESCRIPTION_NAME_ATTRIBUTE_NAME, webserviceDescriptionName);
        diag.describe(WSDL_PUBLISH_LOCATION_ATTRIBUTE_NAME, wsdlPublishLocation);
    }
}
