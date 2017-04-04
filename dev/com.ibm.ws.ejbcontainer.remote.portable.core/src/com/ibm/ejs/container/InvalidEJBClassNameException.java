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
 * InvalidEJBClassNameException
 * This exception is thrown by the container when an
 * attempt is made to install an EJB by specifiying its
 * attribute list in the form of a Properties object.
 * It indicates that the EJB's class name as specified in
 * the attr list could not be found in the jar file
 * that contains the code for the EJB.
 **/

public class InvalidEJBClassNameException extends ContainerException
{
    private static final long serialVersionUID = 3765545543954619613L;

    public InvalidEJBClassNameException(String s, Throwable ex) {
        super(s, ex);
    }
}
