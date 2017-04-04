/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejb.portable;

import java.io.OutputStream;

import javax.ejb.spi.HandleDelegate;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.omg.CORBA.ORB;

class HandleHelper {
    public static HandleDelegate lookupHandleDelegate() {
        try {
            return (HandleDelegate) new InitialContext().lookup("java:comp/HandleDelegate");
        } catch (NamingException e) {
            return HandleDelegateImpl.getInstance();
        }
    }

    /**
     * Returns an initialized client ORB
     *
     * @return initialized client ORB
     * @throws Exception
     */
    public static ORB getORB() throws Exception {
        throw new UnsupportedOperationException("Operation unavailable in Liberty profile");
    }

    /**
     * Determines whether the provided OutputStream is an IIOP output stream
     *
     * @return true if the OutputStream is an IIOP output stream, false if it is
     *         not, and null if unknown
     */
    public static Boolean isORBOutputStream(OutputStream os) {
        return null;
    }
}
