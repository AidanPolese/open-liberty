// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.wsspi.webcontainer.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

/**
*
* 
* WrappingEnumeration wraps a collection of strings to handle getHeaderNames and getHeaders
* return type mismatch.
* 
* @ibm-private-in-use
* 
* @since   WAS8.0
* 
*/
public class WrappingEnumeration implements Enumeration {
	private Collection<String> targetCollection=null;
	
	private Iterator it=null;
	
	public WrappingEnumeration(Collection<String>  targetCollection) {
		this.targetCollection = targetCollection;
	}
	
	public Collection<String> getTargetCollection() {
		return targetCollection;
	}

	
	@Override
	public boolean hasMoreElements() {
		if (this.targetCollection==null)
		{
			return false;
		}
		else
		{
			if (it==null){
				it = targetCollection.iterator();
			}
			return (it.hasNext());
		}
	}

	@Override
	public Object nextElement() {
		if (this.targetCollection==null)
		{
			return null;
		}
		else
		{
			if (it==null){
				it = targetCollection.iterator();
			}
			return (it.next());
		}
	}
	
}