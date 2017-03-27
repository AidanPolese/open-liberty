/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jsonp.lib.provider;

import java.math.BigDecimal;

import javax.json.stream.JsonLocation;

/**
 *
 */
public class JsonParserImpl implements javax.json.stream.JsonParser {

    @Override
    public void close() {}

    @Override
    public BigDecimal getBigDecimal() {
        return null;
    }

    @Override
    public int getInt() {
        return 0;
    }

    @Override
    public JsonLocation getLocation() {
        return null;
    }

    @Override
    public long getLong() {
        return 0;
    }

    @Override
    public String getString() {
        return "Custom JSONP implementation loaded from a shared library";
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public boolean isIntegralNumber() {
        return false;
    }

    @Override
    public Event next() {
        return null;
    }
}
