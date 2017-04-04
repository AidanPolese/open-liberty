/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.ejbcontainer;

/**
 * The ApplicationNotStartedException is thrown by the Enterprise Java Bean (EJB)
 * container when an attempt is made to access either an EJB instance
 * or EJB home before the application is fully started and on
 * a thread different from the one being used to start
 * the application. The only EJB work that is allowed during
 * application start processing is work being performed for startup
 * Singleton beans or legacy IBM startup beans.<p>
 * 
 * Note: An ApplicationNotStartedException would not be returned directly to an
 * application, but would be nested within the appropriate exception
 * required by the EJB Specification for the type of EJB access being
 * attempted. For example, an attempt to use a reference to an EJB local
 * business interface would result in a javax.ejb.NoSuchEJBException. <p>
 **/
public class ApplicationNotStartedException extends RuntimeException
{

    private static final long serialVersionUID = 8422722919386789100L;

    /**
     * Constructs a new ApplicationNotStartedException; no message.
     **/
    public ApplicationNotStartedException()
    {
        super();
    }

    /**
     * Constructs a new ApplicationNotStartedException with the specified detail
     * message.
     **/
    public ApplicationNotStartedException(String detailMessage)
    {
        super(detailMessage);
    }
}
