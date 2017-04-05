// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.ws.webcontainer.core;



/**
 * Sequence of Commands to be executed in the specified sequence
 */
public interface CommandSequence 
{
   
   /**
    * @param command
    */
   public void addCommand(Command command);
   
   /**
    * @param command
    */
   public void removeCommand(Command command);
   
   /**
    * @param req
    * @param res
    */
   public void execute(Request req, Response res);
}
