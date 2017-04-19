/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.mock;

import javax.ejb.EJBContext;

import com.ibm.ws.ejbcontainer.osgi.internal.naming.ContextJavaColonNamingHelper;

public class TestContextJavaColonNamingHelper extends ContextJavaColonNamingHelper {

    private boolean ejbContextActive = false;
    private final EJBContext ejbContext;

    public TestContextJavaColonNamingHelper(EJBContext context) {
        ejbContext = context;
    }

    public void setEjbContextActive(boolean ejbContextActive) {
        this.ejbContextActive = ejbContextActive;
    }

    @Override
    protected boolean isEJBContextActive() {
        return ejbContextActive;
    }

    @Override
    protected EJBContext getEJBContext() {
        return ejbContext;
    }
}
