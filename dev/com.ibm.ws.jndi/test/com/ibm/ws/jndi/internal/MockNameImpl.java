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
package com.ibm.ws.jndi.internal;

import java.util.Properties;
import java.util.Vector;

import javax.naming.CompoundName;
import javax.naming.InvalidNameException;

@SuppressWarnings("serial")
public class MockNameImpl extends CompoundName {
    private static Properties PROPS = new Properties() {
        {
            put("jndi.syntax.direction", "left_to_right");
            put("jndi.syntax.separator", "/");
        }
    };

    MockNameImpl() {
        super(new Vector<String>().elements(), PROPS);
    }

    MockNameImpl(String name) throws InvalidNameException {
        super(name, PROPS);
    }
}
