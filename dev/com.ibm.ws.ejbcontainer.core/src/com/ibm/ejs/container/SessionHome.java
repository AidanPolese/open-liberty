/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2003, 2010
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

import java.rmi.RemoteException;

/**
 * A special EJSHome used for session beans with no local or remote home defined.
 */
public final class SessionHome extends EJSHome
{
    private static final long serialVersionUID = -5599958674558718699L;

    public SessionHome() throws RemoteException
    {
        super();
    }
}
