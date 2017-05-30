/************** Begin Copyright - Do not add comments here **************
 *
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013, 2014
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 */
package com.ibm.wsspi.security.wim.exception;

public class InvalidPropertyValueException extends WIMApplicationException {

    private static final long serialVersionUID = -3995509493602371323L;

    /**
     *
     */
    public InvalidPropertyValueException() {
        super();
    }

    /**
     * @param message
     */
    public InvalidPropertyValueException(String key, String message) {
        super(key, message);
    }

    /**
     * @param cause
     */
    public InvalidPropertyValueException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidPropertyValueException(String key, String message, Throwable cause) {
        super(key, message, cause);
    }

}
