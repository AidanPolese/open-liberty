/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* ORIGINS: 27                                                                */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2005          */
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
/* 05-01-19  mdobbie       LI3603      Creation                               */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
//Class: RLSController
//------------------------------------------------------------------------------
/**
 * The RecoveryLogService delegates calls to suspend and resume to an implementation of RLSController. 
 */
interface RLSController {

	 /**
     * Suspend i/o to the recovery log files
     * 
     * @param timeout value in seconds after which this suspend operation will be cancelled.
     * A timeout value of zero indicates no timeout
     * 
     * @exception RLSTimeoutRangeException Thrown if timeout is not in the range 0 < timeout <= 1,000,000,000.   
     */
	RLSSuspendToken suspend(int timeout) throws RLSTimeoutRangeException;
	
	/**
     * Cancels the corresponding suspend operation, identified by the supplied token.
     * 
     * If there are no outstanding suspend operation, then resumes i/o to the recovery log files.
     * 
     * @param token identifies the corresponding suspend operation to cancel
     * 
     * @exception RLSInvalidSuspendTokenException Thrown if token is null, invalid or has expired
     * 
     */
	void resume(RLSSuspendToken token) throws RLSInvalidSuspendTokenException;
	
	 /**
     * Cancels the corresponding suspend operation, identified by the supplied token byte array.
     * 
     * If there are no outstanding suspend operation, then resumes i/o to the recovery log files.
     * 
     * @param tokenBytes a byte array representation of the RLSSuspendToken, identifying the 
     * corresponding suspend operation to cancel
     * 
     * @exception RLSInvalidSuspendTokenException Thrown if the token byte array is null, invalid or has expired
     * 
     */
	void resume(byte[] tokenBytes) throws RLSInvalidSuspendTokenException;
}
