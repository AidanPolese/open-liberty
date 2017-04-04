/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jndi.url.contexts.javacolon.internal;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * This class is used during SSFB passivation/activation as a replacement object
 * (to be used instead of the javax.naming.Context during serialization) since
 * Liberty's implementation of javax.naming.Context is not serializable. This works
 * by storing the env map and the JNDI name, so that the context can be looked up
 * again on deserialization (activation).
 */
public class JavaURLContextReplacement implements Serializable {
    private static final long serialVersionUID = 4440131108499555728L;

    private String base;
    private Hashtable<?, ?> env;

    JavaURLContextReplacement() {}

    void setBase(String base) {
        this.base = base;
    }

    String getBase() {
        return base;
    }

    void setEnv(Hashtable<?, ?> env) {
        this.env = env;
    }

    Hashtable<?, ?> getEnv() {
        return env;
    }
}
