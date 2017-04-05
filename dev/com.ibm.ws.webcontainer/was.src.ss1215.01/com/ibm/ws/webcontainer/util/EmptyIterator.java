// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.ws.webcontainer.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

@SuppressWarnings("unchecked")
public class EmptyIterator implements Iterator 
{
   private static Iterator _instance = new EmptyIterator ();
   
   /**
    * Constructor for EmptyIterator.
    */
   public EmptyIterator() 
   {
		super();    
   }
   
   /**
    * @return boolean
    * @see java.util.Iterator#hasNext()
    */
   public boolean hasNext() 
   {
		return false;    
   }
   
   /**
    * @return Object
    * @see java.util.Iterator#next()
    */
   public Object next() 
   {
		throw new NoSuchElementException();    
   }
   
   /**
    * @see java.util.Iterator#remove()
    */
   public void remove() 
   {
		throw new RuntimeException("Not supported");    
   }
   
   /**
    * @return java.util.Iterator
    */
   public static Iterator getInstance() 
   {
		return _instance;    
   }
}
