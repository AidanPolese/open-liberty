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
package com.ibm.ws.javaee.ddmodel.common.wsclient;

import com.ibm.ws.javaee.dd.common.wsclient.RespectBinding;
import com.ibm.ws.javaee.ddmodel.AnySimpleType;
import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;
import com.ibm.ws.javaee.ddmodel.common.XSDBooleanType;

/*
 * <xsd:complexType name="respect-bindingType">
 * <xsd:sequence>
 * <xsd:element name="enabled"
 * type="javaee:true-falseType"
 * minOccurs="0"
 * maxOccurs="1"/>
 * </xsd:sequence>
 * </xsd:complexType>
 */
public class RespectBindingType extends DDParser.ElementContentParsable implements RespectBinding {

    @Override
    public boolean isSetEnabled() {
        return AnySimpleType.isSet(enabled);
    }

    @Override
    public boolean isEnabled() {
        return enabled != null && enabled.getBooleanValue();
    }

    XSDBooleanType enabled;

    @Override
    public boolean handleChild(DDParser parser, String localName) throws ParseException {
        if ("enabled".equals(localName)) {
            XSDBooleanType enabled = new XSDBooleanType();
            parser.parse(enabled);
            this.enabled = enabled;
            return true;
        }
        return false;
    }

    @Override
    public void describe(DDParser.Diagnostics diag) {
        diag.describeIfSet("enabled", enabled);
    }
}