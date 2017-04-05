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

import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;

public class BooleanType extends AnySimpleType {

    public boolean getBooleanValue() {
        return value.booleanValue();
    }

    public static BooleanType wrap(DDParser parser, String wrapped) throws ParseException {
        return new BooleanType(parser, wrapped);
    }

    protected Boolean value;

    public BooleanType() {
        super(Whitespace.collapse);
    }

    protected BooleanType(DDParser parser, String lexical) throws ParseException {
        super(Whitespace.collapse, parser, lexical);
    }

    @Override
    protected void setValueFromLexical(DDParser parser, String lexical) throws ParseException {
        if ("true".equals(lexical) || "1".equals(lexical)) {
            value = Boolean.TRUE;
        } else if ("false".equals(lexical) || "0".equals(lexical)) {
            value = Boolean.FALSE;
        } else {
            throw new ParseException(parser.invalidEnumValue(lexical, "true", "1", "false", "0"));
        }
    }

    @Override
    public void describe(DDParser.Diagnostics diag) {
        if (value != null) {
            diag.append("\"" + value + "\"");
        } else {
            diag.append("null");
        }
    }
}
