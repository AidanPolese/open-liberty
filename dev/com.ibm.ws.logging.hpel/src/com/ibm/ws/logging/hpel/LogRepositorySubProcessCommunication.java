//%Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009,2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change History:
 *
 * Reason            Version    Date        User id     Description
 * ----------------------------------------------------------------------------
 * F017049-22453      8.0      04-07-2010    mcasile   Interface for visibility of RMI implementer in WebSphere
 */
package com.ibm.ws.logging.hpel;

/**
 * <code>LogRepositorySubProcessCommunication</code> is an API to be implemented by some agent that can perform IPC starting at
 * a child process and serviced by the parent process.  It is intended to allow any form of IPC desired
 */
public interface LogRepositorySubProcessCommunication {
	/**
	 * notify the controlling process that this process is creating a file.  The implementation will return a string
	 * that represents the name of the file that should be created.  It will also notify the controlling process so
	 * that any retention and caching can be handled.
	 * @param spTimeStamp subProcess timestamp used as part of name generation.  This is passable here in case there are
	 * scenarios where records may already be somehow queued and the timeStamp for file creation should be different
	 * than the current timestamp
	 * @param spPid subProcess PID used as part of name generation
	 * @param spLabel subProcess label identifying info that should help a user identify the context of the subProcess
	 * @return String that is the name of the file which should be created
	 */
	public String notifyOfFileCreation(String destinationType, long spTimeStamp, String spPid, String spLabel) ;

	/**
	 * request that the controlling process remove log files from the repository to free up space.  One common cause of
	 * this call is an IOException where, for example, a repository was specified to allow a max size of 50Mb, but the
	 * file system had 40Mb of space (so even though we are < 50Mb, we can write no more).  Any calls to this method are
	 * an exception basis.  Normal processing should not involve calling this method.
	 * @return true if some files were removed ... false if no files were able to be removed
	 */
	public boolean removeFiles(String destinationType) ;

	/**
	 * request that the controlling process mark this subProcess as no longer active.  This makes all files associated with
	 * this process eligible for removal by retention processing (based on the retention criteria)
	 * @param spPid subProcess PID for which no logging type files are to be considered active
	 */
	public void inactivateSubProcess(String spPid) ;
}
