// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.ws.webcontainer.core;

import java.util.Iterator;

import com.ibm.wsspi.webcontainer.*;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;

/**
 * A RequestMapper is an optimized data structure that serves up the target 
 * or intermediate target in the request processing delegation chain.
 */
public interface RequestMapper 
{
   
   /**
    * @param reqURI
    * @return RequestProcessor
    */
   public RequestProcessor map(String reqURI);
   
   /**
    * @param req
    * @return RequestProcessor
    */
   public RequestProcessor map(IExtendedRequest req);
   
   /**
    * @param path
    * @param target
    */
   public void addMapping(String path, Object target) throws Exception;
   
   /**
    * @param path
    */
   public void removeMapping(String path);
   
   /**
    * Returns an Iterator of all the target mappings added
    * to this mapper
    */
   @SuppressWarnings("unchecked")
   public Iterator targetMappings();
   
   public Object replaceMapping(String path, Object target) throws Exception;
   
   public boolean exists(String path);
   
   
}
