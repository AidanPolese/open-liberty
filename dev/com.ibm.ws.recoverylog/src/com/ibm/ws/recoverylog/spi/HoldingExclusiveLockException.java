/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* ORIGINS: 27                                                                */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003,2004     */
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
/* Date      Programmer  Defect         Description                           */
/* --------  ----------  ------         -----------                           */
/* 06/06/03  beavenj     LIDB2472.2     Create                                */
/* 03-09-29  awilkins                                                         */
/*           beavenj     175817         Changes for CScope logging (NIO)      */
/* 04-01-09  awilkins    LIDB2775-53.5  z/OS code merge                       */
/*                                                                            */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;

//------------------------------------------------------------------------------
// Class: HoldingExclusiveLockException
//------------------------------------------------------------------------------
/**
* This exception indicates that an operation has failed as the caller currently
* holds an exclusive lock (see Lock.java)
*/
public class HoldingExclusiveLockException extends Exception
{
  /** 
  * WebSphere RAS TraceComponent registration
  */
  private static final TraceComponent tc = Tr.register(HoldingExclusiveLockException.class,
                                           TraceConstants.TRACE_GROUP, null);

  //------------------------------------------------------------------------------
  // Method: HoldingExclusiveLockException.HoldingExclusiveLockException
  //------------------------------------------------------------------------------
  /**
  * Exception constructor.
  */
  public HoldingExclusiveLockException()
  {
    if (tc.isEntryEnabled()) Tr.entry(tc, "HoldingExclusiveLockException");
    if (tc.isEntryEnabled()) Tr.exit(tc, "HoldingExclusiveLockException", this);
  }
}

