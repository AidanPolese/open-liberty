// NOTE: This is a generated file. Do not edit it directly.
package com.ibm.ws.javaee.ddmodel.commonbnd;

import com.ibm.ws.javaee.ddmodel.DDParser;

public class EnvEntryType extends com.ibm.ws.javaee.ddmodel.DDParser.ElementContentParsable implements com.ibm.ws.javaee.dd.commonbnd.EnvEntry {
    com.ibm.ws.javaee.ddmodel.StringType name;
    com.ibm.ws.javaee.ddmodel.StringType value;
    com.ibm.ws.javaee.ddmodel.StringType binding_name;

    @Override
    public java.lang.String getName() {
        return name != null ? name.getValue() : null;
    }

    @Override
    public java.lang.String getValue() {
        return value != null ? value.getValue() : null;
    }

    @Override
    public java.lang.String getBindingName() {
        return binding_name != null ? binding_name.getValue() : null;
    }

    @Override
    public boolean isIdAllowed() {
        return true;
    }

    @Override
    public boolean handleAttribute(DDParser parser, String nsURI, String localName, int index) throws DDParser.ParseException {
        if (nsURI == null) {
            if ("name".equals(localName)) {
                this.name = parser.parseStringAttributeValue(index);
                return true;
            }
            if ("value".equals(localName)) {
                this.value = parser.parseStringAttributeValue(index);
                return true;
            }
            if ("binding-name".equals(localName)) {
                this.binding_name = parser.parseStringAttributeValue(index);
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
        diag.describeIfSet("value", value);
        diag.describeIfSet("binding-name", binding_name);
    }
}
