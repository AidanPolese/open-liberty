// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//	 296368		08/05/05	todkap		Nested exceptions lost for problems during application startup


package com.ibm.ws.webcontainer.exception;

import com.ibm.websphere.servlet.response.ResponseUtils;

public class WebContainerException extends java.lang.Exception
{
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3257002172494199088L;

	public WebContainerException()
    {
        super();
    }
    
    public WebContainerException(Throwable th)
    {
    	super (th);
    }

    public WebContainerException(String s)
    {
        // d147832 - run the string through the encoder to eliminate security hole
        super(ResponseUtils.encodeDataString(s));
    }

    public WebContainerException(String s, Throwable t)
    {
        // d147832 - run the string through the encoder to eliminate security hole
        super(ResponseUtils.encodeDataString(s), t);
    }

    
}
