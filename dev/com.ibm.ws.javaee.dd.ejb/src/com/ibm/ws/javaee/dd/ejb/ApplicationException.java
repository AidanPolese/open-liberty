/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.ejb;

/**
 * Represents &lt;application-exception>.
 */
public interface ApplicationException
{
    /**
     * @return &lt;exception-class>
     */
    String getExceptionClassName();

    /**
     * @return true if &lt;rollback> is specified
     * @see #isRollback
     */
    boolean isSetRollback();

    /**
     * @return &lt;rollback> if specified
     * @see #isSetRollback
     */
    boolean isRollback();

    /**
     * @return true if &lt;inherited> is specified
     * @see #isInherited
     */
    boolean isSetInherited();

    /**
     * @return &lt;inherited> if specified
     * @see #isSetInherited
     */
    boolean isInherited();
}
