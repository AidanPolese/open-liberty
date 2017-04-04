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

/**
 *  The base exception type for all exceptions thrown by the
 *  container-server interface. <p>
 */

package com.ibm.websphere.csi;

public class CSIAccessException
                extends CSIException
{
    private static final long serialVersionUID = 4785451820797479654L;

    /**
     * Create a new CSIAccessException with an empty
     * description string. <p>
     */
    public CSIAccessException() {

    } // CSIAccessException

    /**
     * Create a new CSIAccessException with the
     * associated string description. <p.
     * 
     * @param s the <code>String</code> describing the exception <p>
     */
    public CSIAccessException(String s) {

        super(s);

    } // CSIAccessException

    /**
     * Create a new CSIAccessException with the associated
     * string description and nested exception. <p>
     * 
     * @param s the <code>String</code> describing the exception <p>
     * 
     * @param ex the nested <code>Throwable</code>
     */
    public CSIAccessException(String s, Throwable ex) {

        super(s, ex);

    } // CSIAccessException

    /**
     * Create a new CSIAccessException with a
     * minor code. <p>
     */
    public CSIAccessException(int minorCode) {
        super(minorCode);

    } // CSIAccessException

    /**
     * Create a new CSIAccessException with the
     * associated string description and minor code. <p>
     * 
     * @param s the <code>String</code> describing the exception <p>
     * @param minorCode the <code>int</code> describing the minor code <p>
     */
    public CSIAccessException(String s, int minorCode) {

        super(s, minorCode);

    } // CSIAccessException

    /**
     * Create a new CSIAccessException with the associated
     * string description and nested exception. <p>
     * 
     * @param s the <code>String</code> describing the exception <p>
     * @param ex the nested <code>Throwable</code>
     * @param minorCode the <code>int</code> describing the minor code <p>
     */
    public CSIAccessException(String s, Throwable ex, int minorCode) {

        super(s, ex, minorCode);

    } // CSIAccessException

} // CSIAccessException
