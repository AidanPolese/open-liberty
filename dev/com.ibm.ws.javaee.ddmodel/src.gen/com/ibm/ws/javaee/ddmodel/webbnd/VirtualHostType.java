// NOTE: This is a generated file. Do not edit it directly.
package com.ibm.ws.javaee.ddmodel.webbnd;

import com.ibm.ws.javaee.ddmodel.DDParser;

public class VirtualHostType extends com.ibm.ws.javaee.ddmodel.DDParser.ElementContentParsable implements com.ibm.ws.javaee.dd.webbnd.VirtualHost {
    public VirtualHostType() {
        this(false);
    }

    public VirtualHostType(boolean xmi) {
        this.xmi = xmi;
    }

    protected final boolean xmi;
    com.ibm.ws.javaee.ddmodel.StringType name;

    @Override
    public java.lang.String getName() {
        return name != null ? name.getValue() : null;
    }

    @Override
    public boolean isIdAllowed() {
        return true;
    }

    @Override
    public boolean handleAttribute(DDParser parser, String nsURI, String localName, int index) throws DDParser.ParseException {
        if (nsURI == null) {
            if ((xmi ? "virtualHostName" : "name").equals(localName)) {
                this.name = parser.parseStringAttributeValue(index);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleChild(DDParser parser, String localName) throws DDParser.ParseException {
        if (xmi && "virtualHostName".equals(localName)) {
            com.ibm.ws.javaee.ddmodel.StringType name = new com.ibm.ws.javaee.ddmodel.StringType();
            parser.parse(name);
            if (!name.isNil()) {
                this.name = name;
            }
            return true;
        }
        return false;
    }

    @Override
    public void describe(com.ibm.ws.javaee.ddmodel.DDParser.Diagnostics diag) {
        diag.describeIfSet(xmi ? "virtualHostName" : "name", name);
    }
}
