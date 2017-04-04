/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

public class ContainerException extends java.rmi.RemoteException
{
    private static final long serialVersionUID = -3000641845739978815L;

    public ContainerException(String s) {
        super(s);
    }

    public ContainerException(String s, java.lang.Throwable ex) {
        super(s, ex);
    }

    public ContainerException(java.lang.Throwable ex) {
        super("", ex); //150727
    }

    public ContainerException() {
        super();
    }

}
