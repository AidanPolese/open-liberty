// NOTE: This is a generated file. Do not edit it directly.
package com.ibm.ws.javaee.ddmodel.ejbext;

import com.ibm.ws.javaee.ddmodel.DDParser;

public class StartAtAppStartType extends com.ibm.ws.javaee.ddmodel.DDParser.ElementContentParsable implements com.ibm.ws.javaee.dd.ejbext.StartAtAppStart {
    public StartAtAppStartType() {
        this(false);
    }

    public StartAtAppStartType(boolean xmi) {
        this.xmi = xmi;
    }

    protected final boolean xmi;
    com.ibm.ws.javaee.ddmodel.BooleanType value;

    @Override
    public boolean getValue() {
        return value != null ? value.getBooleanValue() : false;
    }

    @Override
    public void finish(DDParser parser) throws DDParser.ParseException {
        if (value == null) {
            throw new DDParser.ParseException(parser.requiredAttributeMissing("value"));
        }
    }

    @Override
    public boolean isIdAllowed() {
        return true;
    }

    @Override
    public boolean handleAttribute(DDParser parser, String nsURI, String localName, int index) throws DDParser.ParseException {
        if (nsURI == null) {
            if ((xmi ? "startEJBAtApplicationStart" : "value").equals(localName)) {
                this.value = parser.parseBooleanAttributeValue(index);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleChild(DDParser parser, String localName) throws DDParser.ParseException {
        return false;
    }

    @Override
    public void describe(com.ibm.ws.javaee.ddmodel.DDParser.Diagnostics diag) {
        diag.describeIfSet(xmi ? "startEJBAtApplicationStart" : "value", value);
    }
}
