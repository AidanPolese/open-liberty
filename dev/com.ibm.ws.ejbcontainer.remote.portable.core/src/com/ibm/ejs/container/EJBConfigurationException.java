/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2006, 2007
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

/**
 * This exception is thrown when EJB container detects that the user
 * configuration of an EJB is incorrect or invalid. The configuration error
 * can be either incorrect use of either annotation and/or xml.
 */
public class EJBConfigurationException extends Exception
{
    private static final long serialVersionUID = 3204992112732695704L;

    public EJBConfigurationException()
    {
        // intentionally left blank.
    }

    public EJBConfigurationException(String detailMessage)
    {
        super(detailMessage);
    }

    public EJBConfigurationException(Throwable throwable)
    {
        super(throwable);
    }

    public EJBConfigurationException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }

}
