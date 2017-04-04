// NOTE: This is a generated file. Do not edit it directly.
package com.ibm.ws.javaee.ddmodel.ejbext;

import com.ibm.ws.javaee.ddmodel.DDParser;

public class TimeOutType extends com.ibm.ws.javaee.ddmodel.DDParser.ElementContentParsable implements com.ibm.ws.javaee.dd.ejbext.TimeOut {
    public TimeOutType() {
        this(false);
    }

    public TimeOutType(boolean xmi) {
        this.xmi = xmi;
    }

    protected final boolean xmi;
    com.ibm.ws.javaee.ddmodel.IntegerType value;

    @Override
    public int getValue() {
        return value != null ? value.getIntValue() : 0;
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
            if ((xmi ? "timeout" : "value").equals(localName)) {
                this.value = parser.parseIntegerAttributeValue(index);
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
        diag.describeIfSet(xmi ? "timeout" : "value", value);
    }
}
