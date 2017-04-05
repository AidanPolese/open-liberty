/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.ejbext;

import com.ibm.ws.javaee.dd.ejbext.RunAsModeBase;
import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.DDParser.Diagnostics;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;

/**
 * Manual implementation of the runAsMode XMI element.
 */
public class RunAsModeXMIType extends DDParser.ElementContentParsable {
    RunAsModeBaseType parent;

    RunAsModeXMIType(RunAsModeBaseType parent) {
        this.parent = parent;
    }

    @Override
    public boolean isIdAllowed() {
        return true;
    }

    @Override
    public boolean handleAttribute(DDParser parser, String nsURI, String localName, int index) throws ParseException {
        if ("http://www.omg.org/XMI".equals(nsURI)) {
            if ("type".equals(localName)) {
                String type = parser.parseStringAttributeValue(index).getValue();
                int typeIndex = type.lastIndexOf(':');
                String typePrefix = type.substring(0, typeIndex);
                String typeNSURI = parser.getNamespaceURI(typePrefix);

                if ("ejbext.xmi".equals(typeNSURI)) {
                    String typeName = type.substring(typeIndex + 1);
                    if ("UseCallerIdentity".equals(typeName)) {
                        parent.mode = RunAsModeBase.ModeTypeEnum.CALLER_IDENTITY;
                    } else if ("RunAsSpecifiedIdentity".equals(typeName)) {
                        parent.mode = RunAsModeBase.ModeTypeEnum.SPECIFIED_IDENTITY;
                    } else if ("UseSystemIdentity".equals(typeName)) {
                        parent.mode = RunAsModeBase.ModeTypeEnum.SYSTEM_IDENTITY;
                    } else {
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean handleChild(DDParser parser, String localName) throws ParseException {
        if (parent.mode == RunAsModeBase.ModeTypeEnum.SPECIFIED_IDENTITY && localName.equals("runAsSpecifiedIdentity")) {
            SpecifiedIdentityType runAsSpecifiedIdentity = new SpecifiedIdentityType(true);
            parser.parse(runAsSpecifiedIdentity);
            parent.specified_identity = runAsSpecifiedIdentity;
            return true;
        }
        return false;
    }

    @Override
    public void describe(Diagnostics diag) {
        throw new UnsupportedOperationException();
    }
}
