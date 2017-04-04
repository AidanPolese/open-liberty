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

import java.util.Map;

import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.StringType;
import com.ibm.ws.javaee.ddmodel.DDParser.Diagnostics;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;
import com.ibm.ws.javaee.ddmodel.wsbnd.WebserviceEndpoint;

/*
 <xsd:complexType name="webserviceEndpointType">
 <xsd:attribute name="port-component-name" type="xsd:string" use="required" />
 <xsd:attribute name="address" type="xsd:string" />
 </xsd:complexType>
 */
public class WebserviceEndpointType extends DDParser.ElementContentParsable implements WebserviceEndpoint {

    private StringType portComponentName;

    private StringType address;

    private PropertiesType properties;

    @Override
    public String getPortComponentName() {
        return portComponentName != null ? portComponentName.getValue() : null;
    }

    @Override
    public String getAddress() {
        return address != null ? address.getValue() : null;
    }

    @Override
    public Map<String, String> getProperties() {
        return null != properties ? properties.getAttributes() : null;
    }

    @Override
    public boolean handleChild(DDParser parser, String localName) throws ParseException {
        if (PROPERTIES_ELEMENT_NAME.equals(localName)) {
            properties = new PropertiesType();
            parser.parse(properties);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleAttribute(DDParser parser, String nsURI, String localName, int index) throws ParseException {
        boolean result = false;

        if (nsURI != null) {
            return result;
        }

        if (PORT_COMPONENT_NAME_ATTRIBUTE_NAME.equals(localName)) {
            portComponentName = parser.parseStringAttributeValue(index);
            result = true;
        } else if (ADDRESS_ATTRIBUTE_NAME.equals(localName)) {
            address = parser.parseStringAttributeValue(index);
            result = true;
        }

        return result;
    }

    @Override
    public void describe(Diagnostics diag) {
        diag.describe(PORT_COMPONENT_NAME_ATTRIBUTE_NAME, portComponentName);
        diag.describe(ADDRESS_ATTRIBUTE_NAME, address);
        diag.describe(PROPERTIES_ELEMENT_NAME, properties);
    }
}
