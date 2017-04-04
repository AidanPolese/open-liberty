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

public class StringType extends AnySimpleType {

    public static class ListType extends ParsableListImplements<StringType, String> {
        @Override
        public StringType newInstance(DDParser parser) {
            return new StringType();
        }

        @Override
        public List<String> getList() {
            List<String> stringList = new ArrayList<String>();
            for (StringType st : list) {
                stringList.add(st.getValue());
            }
            return stringList;
        }
    }

    public String getValue() {
        return value;
    }

    public <T extends Enum<T>> T parseEnumValue(DDParser parser, Class<T> valueClass) throws ParseException {
        return parser.parseEnum(getValue(), valueClass);
    }

    public static StringType wrap(DDParser parser, String wrapped) throws ParseException {
        return new StringType(parser, wrapped);
    }

    // content
    private String value;

    public StringType() {
        super(Whitespace.preserve);
    }

    public StringType(boolean untrimmed) {
        super(Whitespace.preserve, untrimmed);
    }

    protected StringType(Whitespace wsfacet) {
        super(wsfacet);
    }

    protected StringType(DDParser parser, String lexical) throws ParseException {
        super(Whitespace.preserve, parser, lexical);
    }

    protected StringType(Whitespace wsfacet, DDParser parser, String lexical) throws ParseException {
        super(wsfacet, parser, lexical);
    }

    @Override
    protected void setValueFromLexical(DDParser parser, String lexical) {
        value = getLexicalValue();
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
