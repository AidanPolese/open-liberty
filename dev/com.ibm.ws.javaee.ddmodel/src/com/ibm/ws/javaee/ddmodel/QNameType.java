/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel;

import javax.xml.namespace.QName;

import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;

public class QNameType extends AnySimpleType implements com.ibm.ws.javaee.dd.common.QName {

    @Override
    public String getNamespaceURI() {
        return value.getNamespaceURI();
    }

    @Override
    public String getLocalPart() {
        return value.getLocalPart();
    }

    public static QNameType wrap(DDParser parser, String wrapped) throws ParseException {
        return new QNameType(parser, wrapped);
    }

    // content
    private QName value;

    public QNameType() {
        super(Whitespace.collapse);
    }

    public QNameType(DDParser parser, String lexical) throws ParseException {
        super(Whitespace.collapse, parser, lexical);
    }

    @Override
    protected void setValueFromLexical(DDParser parser, String lexical) {
        // We may not be able to set the value without access to the in-scope namespaces,
        // so we defer attempting to do so until resolve is called
    }

    public void resolve(DDParser parser) {
        String lexical = getLexicalValue();
        int colonOffset = lexical.indexOf(':');
        if (colonOffset == -1) {
            value = new QName(lexical);
        } else {
            String localPart = lexical.substring(colonOffset + 1);
            String prefix = lexical.substring(0, colonOffset);
            String uri = parser.getNamespaceURI(prefix);
            value = new QName(uri, localPart, prefix);
        }
    }

    @Override
    public void describe(DDParser.Diagnostics diag) {
        if (value != null) {
            if (value.getNamespaceURI() != null) {
                diag.append("{" + value.getNamespaceURI() + "}");
            }
            diag.append("\"" + value.getLocalPart() + "\"");
        } else {
            diag.append("null");
        }
    }
}
