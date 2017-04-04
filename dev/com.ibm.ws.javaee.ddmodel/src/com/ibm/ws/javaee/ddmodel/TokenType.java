/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel;

import java.util.ArrayList;
import java.util.List;

import com.ibm.ws.javaee.ddmodel.DDParser.ParsableListImplements;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;

public class TokenType extends StringType {

    public static class ListType extends ParsableListImplements<TokenType, String> {
        @Override
        public TokenType newInstance(DDParser parser) {
            return new TokenType();
        }

        @Override
        public List<String> getList() {
            List<String> stringList = new ArrayList<String>();
            for (TokenType token : list) {
                stringList.add(token.getValue());
            }
            return stringList;
        }
    }

    public static TokenType wrap(DDParser parser, String wrapped) throws ParseException {
        return new TokenType(parser, wrapped);
    }

    public TokenType() {
        super(Whitespace.collapse);
    }

    protected TokenType(DDParser parser, String lexical) throws ParseException {
        super(Whitespace.collapse, parser, lexical);
    }

    public ListType split(DDParser parser, String expr) throws ParseException {
        ListType list = new ListType();
        String[] tokens = getValue().split(expr);
        for (String token : tokens) {
            list.add(parser.parseToken(token));
        }
        return list;
    }
}
