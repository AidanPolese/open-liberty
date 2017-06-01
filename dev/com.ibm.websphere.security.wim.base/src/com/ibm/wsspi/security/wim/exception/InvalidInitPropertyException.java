/************** Begin Copyright - Do not add comments here **************
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 *
 */
package com.ibm.wsspi.security.wim.exception;

public class InvalidInitPropertyException extends WIMApplicationException {

    private static final long serialVersionUID = 7520481823714145286L;

    /**
     *
     */
    public InvalidInitPropertyException() {
        super();
    }

    /**
     * @param message
     */
    public InvalidInitPropertyException(String key, String message) {
        super(key, message);
    }

    /**
     * @param cause
     */
    public InvalidInitPropertyException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidInitPropertyException(String key, String message, Throwable cause) {
        super(key, message, cause);
    }
}
