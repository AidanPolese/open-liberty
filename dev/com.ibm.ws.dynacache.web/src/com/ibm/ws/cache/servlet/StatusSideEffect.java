// 1.6, 2/10/05
//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.ws.cache.servlet;

import javax.servlet.http.HttpServletResponse;

/**
 * This class is used by the FragmentComposer to remember the 
 * status code with comment
 * as part of the state that is remembered just
 * prior to the execution of a JSP so that it can be executed 
 * again without executing its parent JSP.
 */
public class StatusSideEffect implements ResponseSideEffect
{
    private static final long serialVersionUID = 6532739036952813391L;
    private int statusCode = 0;
    private String comment = null;

    public String toString() {
       StringBuffer sb = new StringBuffer("Status side effect: \n\t");
       sb.append("Status code: ").append(statusCode).append("\n");
       if (comment != null) {
          sb.append("\tComment: ").append(comment).append("\n");
       }
       return sb.toString();
    }


    /**
     * Constructor with parameter.
     * 
     * @param statusCode The status code.
     * @param comment The comment.
     */
    public
    StatusSideEffect(int statusCode,
                     String comment)
    {
        this.statusCode = statusCode;
        this.comment = comment;
    }

	/**
	 * This resets the state of an HTTP response object to be just 
	 * as it was prior to executing a JSP.
	 *
	 * @param response The response object.
	 *    
	 *  */
	public void performSideEffect(HttpServletResponse response) {
		response.setStatus(statusCode, comment);
	}
}
