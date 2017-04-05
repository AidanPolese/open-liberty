// NOTE: This is a generated file. Do not edit it directly.
package com.ibm.ws.javaee.ddmodel.webext;

import com.ibm.ws.javaee.ddmodel.DDParser;

public class PageType extends com.ibm.ws.javaee.ddmodel.DDParser.ElementContentParsable implements com.ibm.ws.javaee.dd.webext.Page {
    public PageType() {
        this(false);
    }

    public PageType(boolean xmi) {
        this.xmi = xmi;
    }

    protected final boolean xmi;
    com.ibm.ws.javaee.ddmodel.StringType name;
    com.ibm.ws.javaee.ddmodel.StringType uri;

    @Override
    public java.lang.String getName() {
        return name != null ? name.getValue() : null;
    }

    @Override
    public java.lang.String getURI() {
        return uri != null ? uri.getValue() : null;
    }

    @Override
    public boolean isIdAllowed() {
        return true;
    }

    @Override
    public boolean handleAttribute(DDParser parser, String nsURI, String localName, int index) throws DDParser.ParseException {
        if (nsURI == null) {
            // "name" is the same for XML and XMI.
            if ("name".equals(localName)) {
                this.name = parser.parseStringAttributeValue(index);
                return true;
            }
            // "uri" is the same for XML and XMI.
            if ("uri".equals(localName)) {
                this.uri = parser.parseStringAttributeValue(index);
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
        diag.describeIfSet("name", name);
        diag.describeIfSet("uri", uri);
    }
}
