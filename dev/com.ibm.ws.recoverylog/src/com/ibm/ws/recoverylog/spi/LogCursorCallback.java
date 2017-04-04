/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* ORIGINS: 27                                                                */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2003, 2010*/
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                              */
/*                                                                            */
/* DESCRIPTION:                                                               */
/*                                                                            */
/* Change History:                                                            */
/*                                                                            */
/* Date      Programmer    Defect      Description                            */
/* --------  ----------    ------      -----------                            */
/* 06/06/03  beavenj       LIDB2472.2  Create                                 */
/* 08/03/10  mallam        642260      Custom logs                            */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Interface: LogCursorCallback
//------------------------------------------------------------------------------
/**
* <p>
* This interface defines callback methods that can be driven on an object when
* key events occur in the lifecycle of a LogCursor. 
* </p>
*
* <p>
* When a LogCursor is created, a callback object (implementing this interface)
* may be supplied on the constructor. The LogCursor object will then invoke
* methods on the callback when various events occur.
* </p>
*
* <p>
* At present, only the remove event will result in a callback.
* </p>
*/                                                                          
public interface LogCursorCallback
{
  //------------------------------------------------------------------------------
  // Interface: LogCursorCallback.removing
  //------------------------------------------------------------------------------
  /**
  * The associated LogCursors 'remove' method has been invoked. The remove operation
  * is about to be performed on the 'target' object.
  *
  * @param target The object that is being removed.
  *
  * @exception InternalLogException Thrown if an unexpected error has occured.
  */                                                          
  public void removing(Object target) throws InternalLogException;
}

