// NOTE: This is a generated file. Do not edit it directly.
package com.ibm.ws.javaee.ddmodel.appbnd;

import com.ibm.ws.javaee.ddmodel.DDParser;

public class RunAsType extends com.ibm.ws.javaee.ddmodel.DDParser.ElementContentParsable implements com.ibm.ws.javaee.dd.appbnd.RunAs {
    com.ibm.ws.javaee.ddmodel.StringType userid;
    com.ibm.ws.javaee.ddmodel.ProtectedStringType password;

    @Override
    public java.lang.String getUserid() {
        return userid != null ? userid.getValue() : null;
    }

    @Override
    @com.ibm.websphere.ras.annotation.Sensitive
    public java.lang.String getPassword() {
        return password != null ? password.getValue() : null;
    }

    @Override
    public boolean isIdAllowed() {
        return true;
    }

    @Override
    public boolean handleAttribute(DDParser parser, String nsURI, String localName, int index) throws DDParser.ParseException {
        if (nsURI == null) {
            if ("userid".equals(localName)) {
                this.userid = parser.parseStringAttributeValue(index);
                return true;
            }
            if ("password".equals(localName)) {
                this.password = parser.parseProtectedStringAttributeValue(index);
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
        diag.describeIfSet("userid", userid);
        diag.describeIfSet("password", password);
    }
}
