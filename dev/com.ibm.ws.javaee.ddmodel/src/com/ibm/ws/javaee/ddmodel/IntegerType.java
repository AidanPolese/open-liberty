/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel;

import java.math.BigInteger;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;

public class IntegerType extends AnySimpleType {

    public BigInteger getIntegerValue() {
        return value;
    }

    public int getIntValue() {
        return value.intValue();
    }

    public long getLongValue() {
        return value.longValue();
    }

    public static IntegerType wrap(DDParser parser, String wrapped) throws ParseException {
        return new IntegerType(parser, wrapped);
    }

    private BigInteger value;

    public IntegerType() {
        super(Whitespace.collapse);
    }

    protected IntegerType(DDParser parser, String lexical) throws ParseException {
        super(Whitespace.collapse, parser, lexical);
    }

    @Override
    @FFDCIgnore(NumberFormatException.class)
    protected void setValueFromLexical(DDParser parser, String lexical) throws ParseException {
        try {
            value = new BigInteger(lexical);
        } catch (NumberFormatException e) {
            throw new ParseException(parser.invalidIntValue(lexical));
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
