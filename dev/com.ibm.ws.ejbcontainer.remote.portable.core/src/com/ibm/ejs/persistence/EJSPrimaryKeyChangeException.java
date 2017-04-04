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
package com.ibm.ejs.persistence;

public class EJSPrimaryKeyChangeException extends EJSPersistenceException
{
    private static final long serialVersionUID = 1560537528374641555L;

    public EJSPrimaryKeyChangeException() {} // EJSPrimaryKeyChangeException

    public EJSPrimaryKeyChangeException(String s) {
        super(s);
    } // EJSPrimaryKeyChangeException

    public EJSPrimaryKeyChangeException(String s, Throwable ex) {
        super(s, ex);
    }

} // EJSPrimaryKeyChangeException
