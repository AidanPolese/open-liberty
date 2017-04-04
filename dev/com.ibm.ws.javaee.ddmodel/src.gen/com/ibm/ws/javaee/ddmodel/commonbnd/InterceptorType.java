// NOTE: This is a generated file. Do not edit it directly.
package com.ibm.ws.javaee.ddmodel.commonbnd;

import com.ibm.ws.javaee.ddmodel.DDParser;

public class InterceptorType extends com.ibm.ws.javaee.ddmodel.commonbnd.RefBindingsGroupType implements com.ibm.ws.javaee.dd.commonbnd.Interceptor {
    public InterceptorType() {
        this(false);
    }

    public InterceptorType(boolean xmi) {
        super(xmi);
    }

    com.ibm.ws.javaee.ddmodel.StringType class_;

    @Override
    public java.lang.String getClassName() {
        return class_ != null ? class_.getValue() : null;
    }

    @Override
    public boolean isIdAllowed() {
        return true;
    }

    @Override
    public boolean handleAttribute(DDParser parser, String nsURI, String localName, int index) throws DDParser.ParseException {
        if (nsURI == null) {
            if (!xmi && "class".equals(localName)) {
                this.class_ = parser.parseStringAttributeValue(index);
                return true;
            }
        }
        return super.handleAttribute(parser, nsURI, localName, index);
    }

    @Override
    public boolean handleChild(DDParser parser, String localName) throws DDParser.ParseException {
        return super.handleChild(parser, localName);
    }

    @Override
    public void describe(com.ibm.ws.javaee.ddmodel.DDParser.Diagnostics diag) {
        diag.describeIfSet("class", class_);
    }
}
