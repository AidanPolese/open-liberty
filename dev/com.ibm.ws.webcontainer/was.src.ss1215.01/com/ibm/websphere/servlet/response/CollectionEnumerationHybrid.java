//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2010
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.websphere.servlet.response;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

public class CollectionEnumerationHybrid<E> extends ArrayList<E> implements Enumeration<E> {
	private static final long serialVersionUID = -7103072034780794758L;
	private Iterator<E> iterator;
	
	@Override
	public boolean hasMoreElements() {
		if (iterator==null)
			iterator = this.iterator();
		return iterator.hasNext();
	}

	@Override
	public E nextElement() {
		if (iterator==null)
			iterator = this.iterator();
		return iterator.next();
	}

}
