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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.StringType;
import com.ibm.ws.javaee.ddmodel.DDParser.Diagnostics;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;
import com.ibm.ws.javaee.ddmodel.wsbnd.WebserviceEndpointProperties;

/*
 <xsd:complexType name="webserviceEndpointPropertiesType">
 <xsd:anyAttribute namespace="##local" processContents="skip"/>
 </xsd:complexType>
 */
public class WebserviceEndpointPropertiesType extends DDParser.ElementContentParsable implements WebserviceEndpointProperties {
    Map<String, String> attributes;

    @Override
    public Map<String, String> getAttributes() {
        Map<String, String> map = null != attributes ? new HashMap<String, String>() : null;

        if (null != map) {
            map.putAll(attributes);
        }
        return map;
    }

    @Override
    public boolean handleChild(DDParser parser, String localName) throws ParseException {
        return false;
    }

    @Override
    public boolean handleAttribute(DDParser parser, String nsURI, String localName, int index) throws ParseException {

        if (nsURI != null) {
            return false;
        }

        StringType valueType = parser.parseStringAttributeValue(index);
        if (null == attributes) {
            attributes = new HashMap<String, String>();
        }

        attributes.put(localName, valueType.getValue());

        return true;
    }

    @Override
    public void describe(Diagnostics diag) {
        if (null != attributes) {
            String prefix = "";
            for (Entry<String, String> entry : attributes.entrySet()) {
                diag.append(prefix);
                diag.append(entry.getKey());
                diag.append("=");
                diag.append(entry.getValue());

                prefix = ",";
            }
        }
    }
}
