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

public class CSITransactionRequiredException
                extends CSIException
{
    private static final long serialVersionUID = 6233453544778238991L;

    /**
     * Create a new CSITransactionRequiredException with an empty
     * description string. <p>
     */
    public CSITransactionRequiredException() {

    } // CSITransactionRequiredException

    /**
     * Create a new CSITransactionRequiredException with the
     * associated string description. <p.
     * 
     * @param s the <code>String</code> describing the exception <p>
     */
    public CSITransactionRequiredException(String s) {

        super(s);

    } // CSITransactionRequiredException

    /**
     * Create a new CSITransactionRequiredException with the associated
     * string description and nested exception. <p>
     * 
     * @param s the <code>String</code> describing the exception <p>
     * 
     * @param ex the nested <code>Throwable</code>
     */
    public CSITransactionRequiredException(String s, Throwable ex) {

        super(s, ex);

    } // CSITransactionRequiredException

    /**
     * Create a new CSITransactionRequiredException with an empty
     * description string and a minor code. <p>
     * 
     * @param minorCode the <code>int</code> minor code
     */
    public CSITransactionRequiredException(int minorCode) {
        super(minorCode);

    } // CSITransactionRequiredException

    /**
     * Create a new CSITransactionRequiredException with the
     * associated string description and minor code. <p.
     * 
     * @param s the <code>String</code> describing the exception <p>
     * @param minorCode the <code>int</code> minor code
     */
    public CSITransactionRequiredException(String s, int minorCode) {

        super(s, minorCode);

    } // CSITransactionRequiredException

    /**
     * Create a new CSITransactionRequiredException with the associated
     * string description and nested exception and minor code. <p>
     * 
     * @param s the <code>String</code> describing the exception <p>
     * 
     * @param ex the nested <code>Throwable</code>
     * @param minorCode the <code>int</code> minor code
     */
    public CSITransactionRequiredException(String s, Throwable ex, int minorCode) {

        super(s, ex, minorCode);

    } // CSITransactionRequiredException

} // CSITransactionRequiredException
