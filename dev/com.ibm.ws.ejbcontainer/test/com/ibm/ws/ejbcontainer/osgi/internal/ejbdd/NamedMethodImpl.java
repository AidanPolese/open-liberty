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
package com.ibm.ws.ejbcontainer.osgi.internal.ejbdd;

import java.util.Arrays;
import java.util.List;

import com.ibm.ws.javaee.dd.ejb.NamedMethod;

class NamedMethodImpl implements NamedMethod {
    private final String name;
    private final List<String> params;

    NamedMethodImpl(String name, String... params) {
        this.name = name;
        this.params = Arrays.asList(params);
    }

    @Override
    public String getMethodName() {
        return name;
    }

    @Override
    public List<String> getMethodParamList() {
        return params;
    }
}
