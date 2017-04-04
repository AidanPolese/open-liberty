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
package com.ibm.ws.javaee.ddmodel.common;

import com.ibm.ws.javaee.dd.common.JNDIEnvironmentRef;
import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;

/**
 *
 */
public class JNDIEnvironmentRefType extends DDParser.ElementContentParsable implements JNDIEnvironmentRef {

    @Override
    public String getName() {
        return jndi_env_name.getValue();
    }

    JNDINameType jndi_env_name = new JNDINameType();

    private final String element_local_name;

    protected JNDIEnvironmentRefType(String element_local_name) {
        this.element_local_name = element_local_name;
    }

    @Override
    public boolean handleChild(DDParser parser, String localName) throws ParseException {
        if (element_local_name.equals(localName)) {
            parser.parse(jndi_env_name);
            return true;
        }
        return false;
    }

    @Override
    public void describe(DDParser.Diagnostics diag) {
        diag.describe(element_local_name, jndi_env_name);
    }
}
