/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel;

import com.ibm.websphere.ras.ProtectedString;
import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;

/**
 * Provides protection against tracing sensitive data.
 */
public class ProtectedStringType extends StringType {

    private ProtectedString value;

    protected ProtectedStringType(@Sensitive String lexical) throws ParseException {
        super(Whitespace.preserve);
        value = new ProtectedString(lexical.toCharArray());
    }

    @Sensitive
    @Override
    public String getValue() {
        return String.valueOf(value.getChars());
    }

    public static ProtectedStringType wrap(@Sensitive String wrapped) throws ParseException {
        return new ProtectedStringType(wrapped);
    }

    @Override
    protected void setValueFromLexical(DDParser parser, String lexical) {
        String lexicalValue = getLexicalValue();
        value = new ProtectedString(lexicalValue.toCharArray());
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
