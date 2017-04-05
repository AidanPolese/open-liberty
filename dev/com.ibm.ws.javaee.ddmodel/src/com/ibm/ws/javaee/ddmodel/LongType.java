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

import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;

public class LongType extends AnySimpleType {

    public long getLongValue() {
        return value.longValue();
    }

    public static LongType wrap(DDParser parser, String wrapped) throws ParseException {
        return new LongType(parser, wrapped);
    }

    private Long value;

    public LongType() {
        super(Whitespace.collapse);
    }

    protected LongType(DDParser parser, String lexical) throws ParseException {
        super(Whitespace.collapse, parser, lexical);
    }

    @Override
    @FFDCIgnore(NumberFormatException.class)
    protected void setValueFromLexical(DDParser parser, String lexical) throws ParseException {
        try {
            value = Long.parseLong(lexical);
        } catch (NumberFormatException e) {
            throw new ParseException(parser.invalidLongValue(lexical));
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
