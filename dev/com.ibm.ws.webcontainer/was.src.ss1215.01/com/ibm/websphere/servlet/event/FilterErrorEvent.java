// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.websphere.servlet.event;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

/**
 * Event that reports a filter error.
 * 
 * @ibm-api
 */

public class FilterErrorEvent extends FilterEvent {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	private Throwable _error;

	/**
	 * @param source
	 * @param filterConfig
	 */
	public FilterErrorEvent(Object source, FilterConfig filterConfig, Throwable error) {
		super(source, filterConfig);
		_error = error;
	}
	
    /**
     * Returns the top-level error.
     */
    public Throwable getError() {
        return _error;
    }

    /**
     * Get the original cause of the error.
     * Use of ServletExceptions by the engine to rethrow errors
     * can cause the original error to be buried within one or more
     * exceptions.  This method will sift through the wrapped ServletExceptions
     * to return the original error.
     */
    public Throwable getRootCause() {
        Throwable root = getError();
        while(true) {
            if(root instanceof ServletException) {
                ServletException se = (ServletException)_error;
                Throwable seRoot = se.getRootCause();
                if(seRoot == null) {
                    return root;
                }
                else if(seRoot.equals(root)) {//prevent possible recursion
                    return root;
                }
                else {
                    root = seRoot;
                }
            }
            else {
                return root;
            }
        }
    }


}
