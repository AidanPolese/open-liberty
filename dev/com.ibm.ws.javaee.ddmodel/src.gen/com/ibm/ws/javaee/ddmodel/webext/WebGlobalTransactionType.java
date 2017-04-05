// NOTE: This is a generated file. Do not edit it directly.
package com.ibm.ws.javaee.ddmodel.webext;

import com.ibm.ws.javaee.ddmodel.DDParser;

public class WebGlobalTransactionType extends com.ibm.ws.javaee.ddmodel.DDParser.ElementContentParsable implements com.ibm.ws.javaee.dd.webext.WebGlobalTransaction {
    public WebGlobalTransactionType() {
        this(false);
    }

    public WebGlobalTransactionType(boolean xmi) {
        this.xmi = xmi;
    }

    protected final boolean xmi;
    com.ibm.ws.javaee.ddmodel.BooleanType execute_using_wsat;

    @Override
    public boolean isExecuteUsingWSAT() {
        return execute_using_wsat != null ? execute_using_wsat.getBooleanValue() : false;
    }

    @Override
    public boolean isIdAllowed() {
        return true;
    }

    @Override
    public boolean handleAttribute(DDParser parser, String nsURI, String localName, int index) throws DDParser.ParseException {
        if (nsURI == null) {
            if ((xmi ? "supportsWSAT" : "execute-using-wsat").equals(localName)) {
                this.execute_using_wsat = parser.parseBooleanAttributeValue(index);
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
        diag.describeIfSet(xmi ? "supportsWSAT" : "execute-using-wsat", execute_using_wsat);
    }
}
