// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.wsspi.webcontainer;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


/**
 * 
 * Interface that indicates that the implementation class is capable of processing
 * ServletRequests.
 * @ibm-private-in-use
 */
public interface RequestProcessor 
{
   
   /**
    * @param Request req
    * @param Response res@param req
    * @param res
    */
   public void handleRequest(ServletRequest req, ServletResponse res) throws Exception;
   
   /**
    * 
    * @return boolean Returns true if this request processor is for internal use only
    */
   public boolean isInternal();

   public String getName();
}
