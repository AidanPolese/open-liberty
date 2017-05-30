// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.jsp.tsx.tag;

import java.util.Hashtable;
import java.util.Stack;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.ibm.ws.jsp.tsx.db.ConnectionProperties;

public class PasswdTag extends BodyTagSupport {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3256446923367003186L;

	public PasswdTag() {}

    public int doEndTag()
        throws JspException {
            
        Hashtable connectionLookup = (Hashtable)pageContext.getAttribute("TSXConnectionLookup", PageContext.PAGE_SCOPE);
        if (connectionLookup == null) {
            throw new JspException("No dbconnect tag found in jsp");
        }
        Stack connectionStack = (Stack)pageContext.getAttribute("TSXConnectionStack", PageContext.PAGE_SCOPE);
        if (connectionStack == null) {
            throw new JspException("No dbconnect tag found in jsp");
        }
        
        String connectionId = (String)connectionStack.peek();
        ConnectionProperties connection = (ConnectionProperties)connectionLookup.get(connectionId);

        connection.setLoginPasswd(getBodyContent().getString());
        return (EVAL_PAGE);
    }
}
