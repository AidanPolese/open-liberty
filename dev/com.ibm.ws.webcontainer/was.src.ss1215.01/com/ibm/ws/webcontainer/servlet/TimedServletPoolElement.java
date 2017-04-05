// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.servlet;

import javax.servlet.Servlet;


public class TimedServletPoolElement {
	private long _lastAccessedTime;
	private Servlet _servlet;

	public TimedServletPoolElement(Servlet s)
	{
		_servlet = s;
		access();
	}

	long getLastAccessedTime()
	{
		return _lastAccessedTime;
	}

	Servlet getServlet()
	{
		return _servlet;
	}

	void access()
	{
		_lastAccessedTime = System.currentTimeMillis();
	}

	long getIdleTime()
	{
		long idleTime = System.currentTimeMillis() - getLastAccessedTime();
		return idleTime;
	}
}
