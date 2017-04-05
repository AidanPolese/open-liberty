/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2002, 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

public class CSIActivitySessionResetException
                extends CSIException
{
    private static final long serialVersionUID = 2661494188830836173L;

    /**
     * Create a new CSIActivitySessionResetException with an empty
     * description string. <p>
     */

    public CSIActivitySessionResetException() {

    }

    /**
     * Create a new CSIActivitySessionResetException with the
     * associated string description. <p.
     * 
     * @param s the <code>String</code> describing the exception <p>
     */

    public CSIActivitySessionResetException(String s) {

        super(s);
    }

    /**
     * Create a new CSIActivitySessionResetException with the associated
     * string description and nested exception. <p>
     * 
     * @param s the <code>String</code> describing the exception <p>
     * 
     * @param ex the nested <code>Throwable</code>
     */

    public CSIActivitySessionResetException(String s, Throwable ex) {

        super(s, ex);
    }

    /**
     * Create a new CSIActivitySessionResetException with an empty
     * description string and a minor code. <p>
     * 
     * @param minorCode the <code>int</code> minor code
     */

    public CSIActivitySessionResetException(int minorCode) {
        super(minorCode);

    }

    /**
     * Create a new CSIActivitySessionResetException with the
     * associated string description and a minor code. <p.
     * 
     * @param s the <code>String</code> describing the exception <p>
     * 
     * @param minorCode the <code>int</code> minor code
     */

    public CSIActivitySessionResetException(String s, int minorCode) {

        super(s, minorCode);
    }

    /**
     * Create a new CSIActivitySessionResetException with the associated
     * string description and nested exception and a minor code. <p>
     * 
     * @param s the <code>String</code> describing the exception <p>
     * 
     * @param ex the nested <code>Throwable</code>
     * 
     * @param minorCode the <code>int</code> minor code
     */

    public CSIActivitySessionResetException(String s, Throwable ex, int minorCode) {

        super(s, ex, minorCode);
    }

} // CSIActivitySessionResetException
