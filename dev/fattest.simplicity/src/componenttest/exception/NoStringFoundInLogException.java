/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package componenttest.exception;

/**
 * This exception is thrown to indicate that the expected string could not be
 * found in the logs
 */
public class NoStringFoundInLogException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoStringFoundInLogException() {
        super();
    }

    public NoStringFoundInLogException(String message) {
        super(message);
    }

    public NoStringFoundInLogException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoStringFoundInLogException(Throwable cause) {
        super(cause);
    }

}
