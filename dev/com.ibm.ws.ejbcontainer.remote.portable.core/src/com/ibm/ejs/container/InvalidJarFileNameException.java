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

/**
 * InvalidJarFileNameException
 * This exception is thrown by the container when an attempt is
 * made to install an EJB by specifying its attributes in the
 * form of a Properties object.
 * It indicates that the name of the jar file that contains the
 * EJB code is incorrect or could not be accessed.
 **/
public class InvalidJarFileNameException extends ContainerException
{
    private static final long serialVersionUID = -8291283892884860366L;

    public InvalidJarFileNameException(String s) {
        super(s);
    }
}
