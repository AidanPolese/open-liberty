// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.websphere.servlet.response;


/**
 * @ibm-api
 * 
 */
public class ResponseErrorReport extends com.ibm.websphere.servlet.error.ServletErrorReport {
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3978709506010395441L;

	public ResponseErrorReport() {
        super();
    }
    /**
     * Constructs a new ResponseErrorReport with the specified message.
     *
     * @param message Message of exception
     */

    public ResponseErrorReport(String message) {
        super(message);
    }

    /**
     * Constructs a new ResponseErrorReport with the specified message
     * and root cause.
     *
     * @param message Message of exception
     * @param rootCause Exception that caused this exception to be raised
     */

    public ResponseErrorReport(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    /**
     * Constructs a new WebAppErrorReport with the specified message
     * and root cause.
     *
     * @param rootCause Exception that caused this exception to be raised
     */

    public ResponseErrorReport(Throwable rootCause) {
        super(rootCause);
    }

    /**
     * Set the error code of the response.
     */
    public void setErrorCode(int sc) {
        super.setErrorCode(sc);
    }

    /**
     * Set the name of the target Servlet.
     */
    public void setTargetServletName(String servletName) {
        super.setTargetServletName(servletName);
    }
}
