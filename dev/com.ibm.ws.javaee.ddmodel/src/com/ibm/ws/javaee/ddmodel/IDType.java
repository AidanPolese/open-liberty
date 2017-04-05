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

public class IDType extends TokenType {

    public static IDType wrap(DDParser parser, String wrapped) throws ParseException {
        return new IDType(parser, wrapped);
    }

    public IDType() {
        super();
    }

    protected IDType(DDParser parser, String lexical) throws ParseException {
        super(parser, lexical);
    }
}
