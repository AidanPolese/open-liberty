// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.webcontainer.jsp.runtime;

import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.jsp.tagext.Tag;

import com.ibm.ws.jsp.runtime.UnsynchronizedStack;

public class TagHandlerPool extends ThreadLocal
{
	protected Object initialValue()
	{
		return new HashMap()
		{
			/**
			 * Comment for <code>serialVersionUID</code>
			 */
			private static final long serialVersionUID = 3545240228030787633L;

			protected void finalize() throws Throwable
			{
				Iterator i = values().iterator();
				while (i.hasNext())
				{
					UnsynchronizedStack stack = (UnsynchronizedStack) i.next();
					Iterator j = stack.iterator();
					while (j.hasNext())
					{
						Tag tag = (Tag) j.next();
						tag.release();
						tag = null;
					}
					stack.clear();
					stack = null;
				}
				clear();
				super.finalize();
			}
		};
	}
	public HashMap getPool()
	{
		return ((HashMap) get());
	}
}