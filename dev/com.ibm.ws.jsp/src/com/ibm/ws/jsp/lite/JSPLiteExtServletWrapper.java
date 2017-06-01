//IBM Confidential OCO Source Material
//  5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997-2007 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.lite;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.ibm.ws.jsp.Constants;
import com.ibm.wsspi.webcontainer.servlet.GenericServletWrapper;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

public class JSPLiteExtServletWrapper extends GenericServletWrapper {
    public JSPLiteExtServletWrapper(IServletContext parent) throws Exception
    {
        super(parent);
    }
    
    public void handleRequest(ServletRequest req, ServletResponse res) throws Exception {
        if (req instanceof HttpServletRequest) {
            HttpServletRequest hreq = (HttpServletRequest)req;
            if (preCompile(hreq))
                return;
            super.handleRequest(req, res);
        }
    }

    /* A request to a JSP page that has a request parameter with name jsp_precompile
     * is a precompilation request. This method determines if it is this type of request.*/
    boolean preCompile(HttpServletRequest request) throws ServletException 
    {
        String queryString = request.getQueryString();
        if (queryString == null)
            return (false);
        int start = queryString.indexOf(Constants.PRECOMPILE);
        if (start < 0)
            return (false);
        queryString = queryString.substring(start + Constants.PRECOMPILE.length());
        if (queryString.length() == 0)
            return (true); // ?jsp_precompile
        if (queryString.startsWith("&"))
            return (true); // ?jsp_precompile&foo=bar...
        if (!queryString.startsWith("="))
            return (false); // part of some other name or value
        int limit = queryString.length();
        int ampersand = queryString.indexOf("&");
        if (ampersand > 0)
            limit = ampersand;
        String value = queryString.substring(1, limit);
        if (value.equals("true"))
            return (true); // ?jsp_precompile=true
        else if (value.equals("false"))
            //The spec makes it clear that even if the value is set to false, we should behave as if it is set.
            return (true); // ?jsp_precompile=false
        else
            throw new ServletException("Cannot have request parameter " + Constants.PRECOMPILE + " set to " + value);
                    
    }
    
}
