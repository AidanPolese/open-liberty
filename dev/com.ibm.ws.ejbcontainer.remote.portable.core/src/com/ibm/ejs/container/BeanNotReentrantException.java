/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2010
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

public class BeanNotReentrantException extends java.rmi.RemoteException
{
    private static final long serialVersionUID = -4139033422889883913L;

    private transient boolean ivTimeout; // d653777.1

    public BeanNotReentrantException() {
        super();
    }

    public BeanNotReentrantException(String message) {
        super(message);
    }

    public BeanNotReentrantException(String message, boolean timeout) { // d653777.1
        this(message);
        ivTimeout = timeout;
    }

    public boolean isTimeout() { // d653777.1
        return ivTimeout;
    }
}
