// NOTE: This is a generated file. Do not edit it directly.
package com.ibm.ws.javaee.ddmodel.commonext;

import com.ibm.ws.javaee.ddmodel.DDParser;

public class GlobalTransactionType extends com.ibm.ws.javaee.ddmodel.DDParser.ElementContentParsable implements com.ibm.ws.javaee.dd.commonext.GlobalTransaction {
    public GlobalTransactionType() {
        this(false);
    }

    public GlobalTransactionType(boolean xmi) {
        this.xmi = xmi;
    }

    protected final boolean xmi;
    com.ibm.ws.javaee.ddmodel.BooleanType send_wsat_context;
    com.ibm.ws.javaee.ddmodel.IntegerType transaction_time_out;

    @Override
    public boolean isSetSendWSATContext() {
        return send_wsat_context != null;
    }

    @Override
    public boolean isSendWSATContext() {
        return send_wsat_context != null ? send_wsat_context.getBooleanValue() : false;
    }

    @Override
    public boolean isSetTransactionTimeOut() {
        return transaction_time_out != null;
    }

    @Override
    public int getTransactionTimeOut() {
        return transaction_time_out != null ? transaction_time_out.getIntValue() : 0;
    }

    @Override
    public boolean isIdAllowed() {
        return true;
    }

    @Override
    public boolean handleAttribute(DDParser parser, String nsURI, String localName, int index) throws DDParser.ParseException {
        if (nsURI == null) {
            if ((xmi ? "sendWSAT" : "send-wsat-context").equals(localName)) {
                this.send_wsat_context = parser.parseBooleanAttributeValue(index);
                return true;
            }
            if ((xmi ? "componentTransactionTimeout" : "transaction-time-out").equals(localName)) {
                this.transaction_time_out = parser.parseIntegerAttributeValue(index);
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
        diag.describeIfSet(xmi ? "sendWSAT" : "send-wsat-context", send_wsat_context);
        diag.describeIfSet(xmi ? "componentTransactionTimeout" : "transaction-time-out", transaction_time_out);
    }
}
