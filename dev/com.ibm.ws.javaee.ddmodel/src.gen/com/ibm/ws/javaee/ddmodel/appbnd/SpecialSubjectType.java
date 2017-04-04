// NOTE: This is a generated file. Do not edit it directly.
package com.ibm.ws.javaee.ddmodel.appbnd;

import com.ibm.ws.javaee.ddmodel.DDParser;

public class SpecialSubjectType extends com.ibm.ws.javaee.ddmodel.DDParser.ElementContentParsable implements com.ibm.ws.javaee.dd.appbnd.SpecialSubject {
    com.ibm.ws.javaee.dd.appbnd.SpecialSubject.Type type;

    @Override
    public com.ibm.ws.javaee.dd.appbnd.SpecialSubject.Type getType() {
        return type;
    }

    @Override
    public boolean isIdAllowed() {
        return true;
    }

    @Override
    public boolean handleAttribute(DDParser parser, String nsURI, String localName, int index) throws DDParser.ParseException {
        if (nsURI == null) {
            if ("type".equals(localName)) {
                this.type = parser.parseEnumAttributeValue(index, com.ibm.ws.javaee.dd.appbnd.SpecialSubject.Type.class);
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
        diag.describeEnumIfSet("type", type);
    }
}
