// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.ws.webcontainer.util;

import java.util.Enumeration;
import java.util.Iterator;

@SuppressWarnings("unchecked")
public class IteratorEnumerator implements Enumeration 
{
   private Iterator _iterator = null;
   
   /**
    * Constructor for IteratorEnumerator.
    * @param iter
    */
   public IteratorEnumerator(Iterator iter) 
   {
		this._iterator = iter;    
   }
   
   /**
    * @return boolean
    * @see java.util.Enumeration#hasMoreElements()
    */
   public boolean hasMoreElements() 
   {
		return this._iterator.hasNext();    
   }
   
   /**
    * @return Object
    * @see java.util.Enumeration#nextElement()
    */
   public Object nextElement() 
   {
		return this._iterator.next();    
   }
}
