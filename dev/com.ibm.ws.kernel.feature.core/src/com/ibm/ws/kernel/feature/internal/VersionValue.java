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
package com.ibm.ws.kernel.feature.internal;

import java.util.ArrayList;
import java.util.List;

public class VersionValue {
    private static final VersionValue singleton = new VersionValue();
    private static final ThreadLocal<List<String>> values = new ThreadLocal<List<String>>() {
        @Override
        protected List<String> initialValue() {
            return new ArrayList<String>();
        }
    };

    public static VersionValue valueOf(String value) {
        values.get().add(value);
        return singleton;
    }

    private VersionValue() {

    }

    public static VersionValue getInstance() {
        return singleton;
    }

    public static List<String> getValues() {
        List<String> copy = new ArrayList<String>(values.get());
        values.get().clear();
        return copy;
    }
}
