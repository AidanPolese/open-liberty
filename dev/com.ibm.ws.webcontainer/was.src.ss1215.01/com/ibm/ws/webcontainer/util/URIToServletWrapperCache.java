// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.util;

import java.util.HashMap;

/**
 *
 * Simple Hashmap extension class with add()s and remove()s synchronized
 */
@SuppressWarnings("unchecked")
public class URIToServletWrapperCache extends HashMap
{
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3617015269156074804L;
	private int size = 0;
	private int maxCapacity = 0;

	/**
	 * @param maxCapacity
	 */
	public URIToServletWrapperCache(int maxCapacity)
	{
		super(maxCapacity, 1.0f);
		this.maxCapacity = maxCapacity;
	}
	
	
	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object key, Object value)
	{
		synchronized(this)
		{
				//253010, Do not check size since we do so in WebContainer
				Object obj = super.put(key, value);
				if (obj==null)		//Object was added or value previously added was null
					size = super.size(); 	
				return obj;				
		}
	}
	

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(Object key)
	{
		synchronized(this)
		{
			if (size > 0) // check current size before trying remove
			{
				Object obj =  super.remove(key);
				size = super.size();
				return obj;
			}
			return null;
		}
	}

}
