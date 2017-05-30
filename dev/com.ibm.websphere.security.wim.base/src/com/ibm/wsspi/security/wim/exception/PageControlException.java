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

public class PageControlException extends WIMApplicationException {

    private static final long serialVersionUID = 2164261437087845428L;

    /**
     *
     */
    public PageControlException() {
        super();
    }

    /**
     * @param message
     */
    public PageControlException(String key, String message) {
        super(key, message);
    }

    /**
     * @param cause
     */
    public PageControlException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public PageControlException(String key, String message, Throwable cause) {
        super(key, message, cause);
    }

}
