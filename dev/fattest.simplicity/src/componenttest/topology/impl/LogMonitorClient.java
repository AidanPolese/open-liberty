/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package componenttest.topology.impl;

import com.ibm.websphere.simplicity.RemoteFile;

/**
 * This interface may be used to provides services to the LogMonitor class for the purpose of
 * abstracting the server type that is using the LogMonitor services for the purpose of log wait/search.
 */
public interface LogMonitorClient {
    /**
     * This method returns the default log file for the server being monitored.
     * Typically, in a LibertyServer instance, this would be the 'messages.log' file.
     *
     * @return
     * @throws Exception
     */
    public RemoteFile lmcGetDefaultLogFile() throws Exception;

    /**
     * This method clears the log offset for the server instance. Log offset style
     * tracking has been deprecated. This method has been provided to enable backward
     * compatibility with logic that was moved into the LogMonitor class. For new implementations,
     * this method can be a 'no op'.
     */
    public void lmcClearLogOffsets();

    /**
     * This method updates the log offset for the server instance. Log offset style
     * tracking has been deprecated. This method has been provided to enable backward
     * compatibility with logic that was moved into the LogMonitor class. For new implementations,
     * this method can be a 'no op'.
     */
    public void lmcUpdateLogOffset(String logFile, Long newLogOffset);
}
