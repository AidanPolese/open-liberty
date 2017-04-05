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
import com.ibm.ws.javaee.ddmodel.DDParser.Diagnostics;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;
import com.ibm.ws.javaee.ddmodel.StringType;
import com.ibm.ws.javaee.ddmodel.wsbnd.HttpPublishing;
import com.ibm.ws.javaee.ddmodel.wsbnd.WebserviceSecurity;

/*
 <xsd:complexType name="httpPublishingType">
 <xsd:sequence>
 <xsd:element name="webservice-security" type="ws:webserviceSecurityType" minOccurs="0" />
 </xsd:sequence>
 <xsd:attribute name="context-root" type="xsd:string" />
 </xsd:complexType>
 */
public class HttpPublishingType extends DDParser.ElementContentParsable implements HttpPublishing {

    private StringType contextRoot;

    private WebserviceSecurityType webserviceSecurityType;

    @Override
    public String getContextRoot() {
        return contextRoot != null ? contextRoot.getValue() : null;
    }

    @Override
    public WebserviceSecurity getWebserviceSecurity() {
        return this.webserviceSecurityType;
    }

    /**
     * parse the context-root attribute defined in the element.
     */
    @Override
    public boolean handleAttribute(DDParser parser, String nsURI, String localName, int index) throws ParseException {
        boolean result = false;

        if (nsURI != null) {
            return result;
        }

        if (CONTEXT_ROOT_ATTRIBUTE_NAME.equals(localName)) {
            this.contextRoot = parser.parseStringAttributeValue(index);
            result = true;
        }

        return result;

    }

    @Override
    public boolean handleChild(DDParser parser, String localName) throws ParseException {
        if (WEBSERVICE_SECURITY_ELEMENT_NAME.equals(localName)) {
            this.webserviceSecurityType = new WebserviceSecurityType();
            parser.parse(webserviceSecurityType);
            return true;
        }
        return false;
    }

    @Override
    public void describe(Diagnostics diag) {
        diag.describeIfSet(CONTEXT_ROOT_ATTRIBUTE_NAME, contextRoot);

        diag.append("[" + WEBSERVICE_SECURITY_ELEMENT_NAME + "<");
        if (null != this.webserviceSecurityType) {
            this.webserviceSecurityType.describe(diag);
        }
        diag.append(">]");
    }
}
