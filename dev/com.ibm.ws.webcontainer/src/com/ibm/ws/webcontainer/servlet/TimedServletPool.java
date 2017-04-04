// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

//  CHANGE HISTORY
//  Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//          314011         11/10/05     mmolden             61FVT:Advanced Servlet Caching Test Failures

package com.ibm.ws.webcontainer.servlet;

import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

@SuppressWarnings("unchecked")
public class TimedServletPool {
	
	private long _maxIdleTime;
	protected Vector _allElements = new Vector();
	private Vector _idleElements = new Vector();
	protected ServletConfig _config;
	protected Class _servletClass;
	
	/**
	 * @param initialPoolSize
	 * @param maxIdleTime
	 * @param class1
	 * @param config
	 */
	public TimedServletPool(int initialPoolSize, long maxIdleTime, Class servletClass, ServletConfig config) throws InstantiationException, IllegalAccessException, ServletException {
		_maxIdleTime = maxIdleTime;
		_servletClass = servletClass;
		_config = config;

		// one second is the minimum time an idle instance can remain in the pool before being destroyed.
		maxIdleTime = maxIdleTime < 1000 ? 1000 : maxIdleTime;

		if (initialPoolSize < 1) initialPoolSize = 1;

		for (int i = 0; i < initialPoolSize; i++)
		{
			addElementToIdleList(createNewElement());
		}
	}

	public synchronized TimedServletPoolElement getNextElement()
	throws InstantiationException, IllegalAccessException, ServletException
	{
		int idleCount = _idleElements.size();
		TimedServletPoolElement e = null;
		if (idleCount > 0)
		{
			//int lastIndex = idleCount - 1;
			int lastIndex = 0;
			e = (TimedServletPoolElement)_idleElements.elementAt(lastIndex);
			_idleElements.removeElementAt(lastIndex);
		}
		else
		{
			e = createNewElement();
		}
		return e;
	}

	synchronized void removeExpiredElements()
	{
		//remove any servlets that have been idle for more than the max idle time.
		TimedServletPoolElement e = null;
		int idleCount = _idleElements.size();

		for (int i = 0; i < idleCount; i++)
		{
			e = (TimedServletPoolElement)_idleElements.elementAt(0);

			if (e.getIdleTime() > _maxIdleTime)
			{
				_allElements.removeElement(e);
				_idleElements.removeElementAt(0);
				e.getServlet().destroy();
			}
			else
			{
				return;
			}
		}
	}

	synchronized void removeAllElements()
	{
		//remove everything
		Enumeration elements = _allElements.elements();
		while (elements.hasMoreElements())
		{
			TimedServletPoolElement e = (TimedServletPoolElement)elements.nextElement();
			_allElements.removeElement(e);
			_idleElements.removeElement(e);
			e.getServlet().destroy();
		}
	}

	public synchronized void returnElement(TimedServletPoolElement e)
	{
		addElementToIdleList(e);
	}

	public void setMaxIdleTime(long millisecs)
	{
		_maxIdleTime = millisecs;
	}

	public long getMaxIdleTime()
	{
		return _maxIdleTime;
	}

	public int getSize()
	{
		return _allElements.size();
	}

	protected TimedServletPoolElement createNewElement()
	throws InstantiationException, IllegalAccessException, ServletException
	{
		Servlet s = (Servlet)_servletClass.newInstance();

		s.init(_config);
		TimedServletPoolElement e = new TimedServletPoolElement(s);
		_allElements.addElement(e);
		return e;
	}

	protected synchronized void addElementToIdleList(TimedServletPoolElement e)
	{
		e.access();
		_idleElements.addElement(e);
	}
}
