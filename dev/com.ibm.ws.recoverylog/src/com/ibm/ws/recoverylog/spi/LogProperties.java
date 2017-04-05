/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2002, 2003    */
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                              */
/*                                                                            */
/*  DESCRIPTION:                                                              */
/*                                                                            */
/*  Change History:                                                           */
/*                                                                            */
/*  Date      Programmer    Defect      Description                           */
/*  --------  ----------    ------      -----------                           */
/* 06/06/03   beavenj       LIDB2472.2  Create                                */
/* 10/08/04   mezarin       LIDB1578-22 z/OS HA Manager support               */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Interface: LogProperties
//------------------------------------------------------------------------------
/**
* <p>
* An abstract representation of the properties associated with a recovery log.
* Different types of recovery log will require different implementations of  
* this interface to define their physical characteristics. 
* </p>
*
* <p>
* Instances of these implementations are created by the client service in order
* to define these characteristics.
* </p>
*/                                                                          
public interface LogProperties extends java.io.Serializable    /* @LIDB1578-22C*/
{
  //------------------------------------------------------------------------------
  // Method: LogProperties.logIdentifier
  //------------------------------------------------------------------------------
  /**
  * Returns the unique (within service) "Recovery Log Identifier" (RLI) value.
  *
  * @return int The unique RLI value.
  */
  public int logIdentifier();

  //------------------------------------------------------------------------------
  // Method: LogProperties.logName
  //------------------------------------------------------------------------------
  /**
  * Returns the unique (within service) "Recovery Log Name" (RLN). 
  *
  * @return String The unique RLN value.
  */
  public String logName();
}
