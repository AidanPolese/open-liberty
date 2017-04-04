// NOTE: This is a generated file. Do not edit it directly.
package com.ibm.ws.javaee.ddmodel.webext;

import com.ibm.ws.javaee.ddmodel.DDParser;

public class MimeFilterType extends com.ibm.ws.javaee.ddmodel.DDParser.ElementContentParsable implements com.ibm.ws.javaee.dd.webext.MimeFilter {
    public MimeFilterType() {
        this(false);
    }

    public MimeFilterType(boolean xmi) {
        this.xmi = xmi;
    }

    protected final boolean xmi;
    com.ibm.ws.javaee.ddmodel.StringType target;
    com.ibm.ws.javaee.ddmodel.StringType mime_type;

    @Override
    public java.lang.String getTarget() {
        return target != null ? target.getValue() : null;
    }

    @Override
    public java.lang.String getMimeType() {
        return mime_type != null ? mime_type.getValue() : null;
    }

    @Override
    public boolean isIdAllowed() {
        return xmi;
    }

    @Override
    public boolean handleAttribute(DDParser parser, String nsURI, String localName, int index) throws DDParser.ParseException {
        if (nsURI == null) {
            // "target" is the same for XML and XMI.
            if ("target".equals(localName)) {
                this.target = parser.parseStringAttributeValue(index);
                return true;
            }
            if ((xmi ? "type" : "mime-type").equals(localName)) {
                this.mime_type = parser.parseStringAttributeValue(index);
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
        diag.describeIfSet("target", target);
        diag.describeIfSet(xmi ? "type" : "mime-type", mime_type);
    }
}
