package com.ibm.websphere.command;

// IBM Confidential OCO Source Material
// 5639-D57 (C) COPYRIGHT International Business Machines Corp. 2000
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//  FILENAME: TargetPolicy.java
//
//  DESCRIPTION:
//      
//
// Change Log:
//  Date       Pgmr    Defect   Description
//  --------   ------- ------   ---------------------------------
// 03/09/00   v2iicrp  75515    javadoc changes
//
//**********************************************************************




/**
 * The TargetPolicy interface declares one method, getCommandTarget(), which
 * implements the routine used to associate commands and targets. The
 * TargetPolicyDefault class provides an implementation of this interface,
 * but application programmers can override it to suit their needs, for
 * example, to allow mapping commands to targets through the use of
 * properties files or administrative applications.
 * 
 * @ibm-api
 */
public interface TargetPolicy 
{
	/**
	 * Retrieves the target associated with the command, as
         * determined by the target policy.
	 * 
	 * @param command The command whose target is requested.
	 * @return The target for the command. 
	 */
	public CommandTarget
	getCommandTarget(TargetableCommand command);
}
