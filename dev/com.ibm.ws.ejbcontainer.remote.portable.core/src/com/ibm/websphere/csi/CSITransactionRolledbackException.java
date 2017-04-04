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
package com.ibm.websphere.csi;

public class CSITransactionRolledbackException
                extends CSIException
{
    private static final long serialVersionUID = 6095094648647166480L;

    /**
     * Create a new CSITransactionRolledbackException with an empty
     * description string. <p>
     */
    public CSITransactionRolledbackException() {

    } // CSITransactionRolledbackException

    /**
     * Create a new CSITransactionRolledbackException with the
     * associated string description. <p.
     * 
     * @param s the <code>String</code> describing the exception <p>
     */
    public CSITransactionRolledbackException(String s) {

        super(s);

    } // CSITransactionRolledbackException

    /**
     * Create a new CSITransactionRolledbackException with the associated
     * string description and nested exception. <p>
     * 
     * @param s the <code>String</code> describing the exception <p>
     * 
     * @param ex the nested <code>Throwable</code>
     */
    public CSITransactionRolledbackException(String s, Throwable ex) {

        super(s, ex);

    } // CSITransactionRolledbackException

    /**
     * Create a new CSITransactionRolledbackException with an empty
     * description string and a minor code. <p>
     * 
     * @param minorCode the <code>int</code> minor code
     */
    public CSITransactionRolledbackException(int minorCode) {
        super(minorCode);

    } // CSITransactionRolledbackException

    /**
     * Create a new CSITransactionRolledbackException with the
     * associated string description and a minor code. <p.
     * 
     * @param s the <code>String</code> describing the exception <p>
     * @param minorCode the <code>int</code> minor code
     */
    public CSITransactionRolledbackException(String s, int minorCode) {

        super(s, minorCode);

    } // CSITransactionRolledbackException

    /**
     * Create a new CSITransactionRolledbackException with the associated
     * string description and nested exception and a minor code. <p>
     * 
     * @param s the <code>String</code> describing the exception <p>
     * 
     * @param ex the nested <code>Throwable</code>
     * @param minorCode the <code>int</code> minor code
     */
    public CSITransactionRolledbackException(String s, Throwable ex, int minorCode) {

        super(s, ex, minorCode);

    } // CSITransactionRolledbackException

} // CSITransactionRolledbackException
