// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.ws.container;

import java.util.Iterator;

/**
 * Config interface used by all internal configs
 */
public interface Configuration 
{
   
   /**
    * Identifier for configuration
    * @return String
    */
   public String getId();
   
   /**
    * To get at attribute
    * @param key
    * @return Object
    */
   public Object getAttribute(Object key);
   
   /**
    * To add attribute
    * @param key
    * @param attribute
    */
   public void addAttribute(Object key, Object attribute);
   
   /**
    * Get at attribute names
    * @return java.util.Iterator
    */
   @SuppressWarnings("unchecked")
   public Iterator getAttributeNames();
   
   /**
    * Get at attribute values
    * @return java.util.Iterator
    */
   @SuppressWarnings("unchecked")
   public Iterator getAttributeValues();
   
   /**
    * Remove attribute
    * @param key
    * @return Object
    */
   public Object removeAttribute(Object key);
   
   /**
    * @param wccmObj
    */
   public void populateFrom(Object wccmObj);
}
