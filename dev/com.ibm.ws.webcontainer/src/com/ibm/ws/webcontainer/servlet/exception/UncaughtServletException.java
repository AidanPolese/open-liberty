// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.ws.webcontainer.servlet.exception;

import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.webcontainer.webapp.WebAppErrorReport;

public class UncaughtServletException extends WebAppErrorReport
{
   /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3834591006632260147L;
private String _targetServletName;
   public UncaughtServletException(String servletName, Throwable cause)
   {
      super("Server caught unhandled exception from servlet [" + servletName + "]: " + cause.getMessage(), cause);
      setErrorCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      setTargetServletName(servletName);
   }
}
