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

public class CSIActivityRequiredException
                extends CSIException
{
    private static final long serialVersionUID = -6336387163843485291L;

    /**
     * Create a new CSIActivityRequiredException with an empty
     * description string. <p>
     */

    public CSIActivityRequiredException() {

    }

    /**
     * Create a new CSIActivityRequiredException with the
     * associated string description. <p.
     * 
     * @param s the <code>String</code> describing the exception <p>
     */

    public CSIActivityRequiredException(String s) {

        super(s);
    }

    /**
     * Create a new CSIActivityRequiredException with the associated
     * string description and nested exception. <p>
     * 
     * @param s the <code>String</code> describing the exception <p>
     * 
     * @param ex the nested <code>Throwable</code>
     */

    public CSIActivityRequiredException(String s, Throwable ex) {

        super(s, ex);
    }

    /**
     * Create a new CSIActivityRequiredException with an empty
     * description string and a minor code. <p>
     * 
     * @param minorCode the <code>int</code> minor code
     */

    public CSIActivityRequiredException(int minorCode) {
        super(minorCode);

    }

    /**
     * Create a new CSIActivityRequiredException with the
     * associated string description and a minor code. <p.
     * 
     * @param s the <code>String</code> describing the exception <p>
     * 
     * @param minorCode the <code>int</code> minor code
     */

    public CSIActivityRequiredException(String s, int minorCode) {

        super(s, minorCode);
    }

    /**
     * Create a new CSIActivityRequiredException with the associated
     * string description and nested exception and a minor code. <p>
     * 
     * @param s the <code>String</code> describing the exception <p>
     * 
     * @param ex the nested <code>Throwable</code>
     * 
     * @param minorCode the <code>int</code> minor code
     */

    public CSIActivityRequiredException(String s, Throwable ex, int minorCode) {

        super(s, ex, minorCode);
    }

} // CSIActivityRequiredException
