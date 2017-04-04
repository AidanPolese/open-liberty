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
package com.ibm.ws.jndi;

import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

public class WSNameParser implements NameParser {

    private final Object root;

    public WSNameParser(Object symbolicRoot) {
        this.root = symbolicRoot; // extra de-reference to force an early NPE
    }

    @Override
    public Name parse(String s) throws NamingException {
        return new WSName(s);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WSNameParser) {
            WSNameParser that = (WSNameParser) o;
            return this.root == that.root;
            // TODO do we need to do any other kind of comparison?
            // e.g. if this is a CosNaming context, we could check _is_equivalent
        }
        return false;
    }

    @Override
    public int hashCode() {
        return root.hashCode();
    }
}