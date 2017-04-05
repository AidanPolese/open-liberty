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
/* %Z% %I% %W% %G% %U% [%H% %T%]                                              */
/*                                                                            */
/* DESCRIPTION:                                                               */
/*                                                                            */
/* Change History:                                                            */
/*                                                                            */
/* Date      Programmer    Defect      Description                            */
/* --------  ----------    ------      -----------                            */
/* 06/06/03  beavenj       LIDB2472.2  Create                                 */
/* 04-03-24  awilkins LIDB2775.53.5.1  Exception chaining                     */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Class: LogCorruptedException
//------------------------------------------------------------------------------
/**
* This exception is generated if the recovery log service detects that a corruption
* in a recovery log file as it reads it back from disk. This is often not as serious
* as it sounds since as long as the basic header information at the front of the 
* target log file is formatted correctly, the recovery log will tollerate a single
* corruption at any point through the reload process. If a corruption is detected
* then the read will stop and recovery will take place with the information read
* upto that point. The rational behind this is that the recovery log is designed
* to cope with corruption that occurs due to a system failure (eg power failure) 
* where the corrption will actually occur at the end of the file. In this case the
* code which attempted to force the data to disk will not have regained control,
* so it is safe to ignore the corrupted data. The recovery log is NOT designed to 
* be able to recovery if arbitary corrption (eg user has damaged the file manually) 
* has occured. In such cases the recovery log will thow the LogCorruptedException 
* exception.
*/
public class LogCorruptedException extends Exception
{
  protected LogCorruptedException(Throwable cause)
  {
    super(cause);
  }
}

