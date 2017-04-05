// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.


package com.ibm.ws.webcontainer.util;


import java.security.AccessController;
import java.security.PrivilegedAction;

@SuppressWarnings("unchecked")
public class WebContainerSystemProps {


    private static boolean _sendRedirectCompatibility = false;

    static{
	String doPrivSendRedirect = (String)AccessController.doPrivileged(new PrivilegedAction()
								 {
								     public Object run()
								     {
									 return (System.getProperty("com.ibm.websphere.sendredirect.compatibility"));
								     }
								 });

	if (doPrivSendRedirect!=null)
	{
	    _sendRedirectCompatibility = doPrivSendRedirect.equalsIgnoreCase("true");
	}
    }


    public static boolean getSendRedirectCompatibilty(){
    	return _sendRedirectCompatibility;
    }
}
