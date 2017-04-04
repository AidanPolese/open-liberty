/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* ORIGINS: 27                                                                */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2002, 2004    */
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/*                                                                            */
/* DESCRIPTION:                                                               */
/*                                                                            */
/* Change History:                                                            */
/*                                                                            */
/* Date      Programmer    Defect      Description                            */
/* --------  ----------    ------      -----------                            */
/* 08/03/05  mezarin       pk01151     Create                                 */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Class: ServantFailureDuringRecoveryException
//------------------------------------------------------------------------------
/**
* A requested operation is not available or cannot be issued in the present state
*/
public class ServantFailureDuringRecoveryException extends RecoveryFailedException
{
  public ServantFailureDuringRecoveryException()
  {
      super();
  }
  
  public ServantFailureDuringRecoveryException(Throwable cause)
  {
      super(cause);
  }
}

