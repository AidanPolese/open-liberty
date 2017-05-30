/************** Begin Copyright - Do not add comments here **************
*
 *
 * IBM Confidential OCO Source Material
 * Virtual Member Manager (C) COPYRIGHT International Business Machines Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 */
package com.ibm.wsspi.security.wim.exception;

public class AttributeNotSupportedException extends WIMApplicationException {

    private static final long serialVersionUID = -6031660467113138568L;

    /**
     *
     */
    public AttributeNotSupportedException() {
        super();

    }

    /**
     * @param message
     */
    public AttributeNotSupportedException(String key, String message) {
        super(key, message);

    }

    /**
     * @param cause
     */
    public AttributeNotSupportedException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public AttributeNotSupportedException(String key, String message, Throwable cause) {
        super(key, message, cause);
    }

}
