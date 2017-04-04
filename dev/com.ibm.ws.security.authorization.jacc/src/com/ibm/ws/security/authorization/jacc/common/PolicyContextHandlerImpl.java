/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.authorization.jacc.common;

import java.util.HashMap;

import javax.security.jacc.PolicyContextException;
import javax.security.jacc.PolicyContextHandler;

public class PolicyContextHandlerImpl implements PolicyContextHandler {

    private static boolean initialized = false;
    private static final String[] keysArray = new String[] { "javax.security.auth.Subject.container", "javax.xml.soap.SOAPMessage", "javax.servlet.http.HttpServletRequest",
                                                            "javax.ejb.EnterpriseBean", "javax.ejb.arguments" };
    private static PolicyContextHandlerImpl pchi;

    private PolicyContextHandlerImpl() {}

    public static PolicyContextHandlerImpl getInstance() {
        if (!initialized) {
            pchi = new PolicyContextHandlerImpl();
            initialized = true;
        }
        return pchi;
    }

    @Override
    public boolean supports(String key) throws PolicyContextException {
        for (String value : keysArray) {
            if (key.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String[] getKeys() throws PolicyContextException {
        return keysArray.clone();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getContext(String key, Object object)
                    throws PolicyContextException
    {
        if (object == null) {
            return null;
        }
        return ((HashMap<String, Object>) object).get(key);
    }

}
