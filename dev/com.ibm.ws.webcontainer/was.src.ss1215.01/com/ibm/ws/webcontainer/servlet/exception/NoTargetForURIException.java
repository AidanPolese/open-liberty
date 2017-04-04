// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.servlet.exception;

import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.webcontainer.webapp.WebAppErrorReport;

public class NoTargetForURIException extends WebAppErrorReport
{
   /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3257565122414392883L;

public NoTargetForURIException(String uri)
   {
      super("No target servlet configured for uri: " + uri);
      setErrorCode(HttpServletResponse.SC_NOT_FOUND);
   }
}
