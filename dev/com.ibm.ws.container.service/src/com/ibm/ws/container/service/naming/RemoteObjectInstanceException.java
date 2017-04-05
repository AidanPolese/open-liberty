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
package com.ibm.ws.container.service.naming;

/**
 * This exception indicates that something went wrong while attempting
 * to acquire an object that is represented on a remote server.
 * The cause of this exception should include the information necessary
 * for understanding what went wrong. For example, it could be that
 * the client failed to communicate with the server which could result
 * in a RemoteException. Possibly the object does not exist on the
 * remote server, resulting in a NamingException. Or possibly the
 * object could not be re-constructed locally due to a
 * ClassNotFoundException, etc.
 */
public class RemoteObjectInstanceException extends Exception {
    private static final long serialVersionUID = 7057215313281670551L;

    public RemoteObjectInstanceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
