// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

//CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
//PK31377       4/30/07		mmolden             SERVLET FILTER IS NOT CALLED FOR
//PK64290         05/20/08      mmolden             SESSION LOSS WHEN USING ONLY URLREWRITING
package com.ibm.wsspi.webcontainer.filter;

import java.io.IOException;
import java.util.EnumSet;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.wsspi.webcontainer.RequestProcessor;
import com.ibm.wsspi.webcontainer.WCCustomProperties;
import com.ibm.wsspi.webcontainer.collaborator.CollaboratorInvocationEnum;
import com.ibm.wsspi.webcontainer.extension.ExtensionProcessor;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;
import com.ibm.wsspi.webcontainer.servlet.IServletWrapper;
import com.ibm.wsspi.webcontainer.webapp.NamespaceInvoker;

public interface WebAppFilterManager {

	/**
	 * Invokes the filters defined for a webapp.
	 * 
	 * @param request
	 * @param response
	 * @param target
	 * @param context
	 * @param invoker
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
    public boolean invokeFilters(ServletRequest request, ServletResponse response, IServletContext context, RequestProcessor requestProcessor, EnumSet<CollaboratorInvocationEnum> colEnum) 
		throws ServletException,IOException;

}
