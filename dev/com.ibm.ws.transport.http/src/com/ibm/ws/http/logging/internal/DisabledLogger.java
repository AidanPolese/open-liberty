// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.http.logging.internal;

import com.ibm.wsspi.http.channel.HttpRequestMessage;
import com.ibm.wsspi.http.channel.HttpResponseMessage;
import com.ibm.wsspi.http.channel.HttpServiceContext;
import com.ibm.wsspi.http.logging.AccessLog;
import com.ibm.wsspi.http.logging.DebugLog;
import com.ibm.wsspi.http.logging.LogFile;

/**
 * Disabled no-op logger for simple runtime usage.
 * 
 */
@SuppressWarnings("unused")
public class DisabledLogger implements DebugLog, AccessLog {

    /** Singleton instance of this logger class */
    private static final DisabledLogger singleton = new DisabledLogger();

    /**
     * Constructor.
     */
    public DisabledLogger() {
        // nothing
    }

    /**
     * Access the singleton instance of this logger.
     * 
     * @return DisabledLogger
     */
    public static DisabledLogger getRef() {
        return singleton;
    }

    /**
     * @see DebugLog#getCurrentLevel()
     */
    public Level getCurrentLevel() {
        return DebugLog.Level.NONE;
    }

    /**
     * @see DebugLog#isEnabled(DebugLog.Level)
     */
    public boolean isEnabled(Level logLevel) {
        return false;
    }

    /**
     * @see DebugLog#log(DebugLog.Level, byte[], HttpServiceContext)
     */
    public void log(Level logLevel, byte[] message, HttpServiceContext hsc) {
        // nothing
    }

    /**
     * @see DebugLog#log(DebugLog.Level, String, HttpServiceContext)
     */
    public void log(Level logLevel, String message, HttpServiceContext hsc) {
        // nothing
    }

    /**
     * @see DebugLog#log(DebugLog.Level, byte[], String, String, String, String)
     */
    public void log(Level logLevel, byte[] message, String remoteIP, String remotePort, String localIP, String localPort) {
        // nothing
    }

    /**
     * @see DebugLog#log(DebugLog.Level, String, String, String, String, String)
     */
    public void log(Level logLevel, String message, String remoteIP, String remotePort, String localIP, String localPort) {
        // nothing
    }

    /**
     * @see DebugLog#setCurrentLevel(DebugLog.Level)
     */
    public void setCurrentLevel(Level logLevel) {
        // nothing
    }

    /**
     * @see LogFile#disable()
     */
    public boolean disable() {
        return true;
    }

    /**
     * @see LogFile#getFileName()
     */
    public String getFileName() {
        return "";
    }

    /**
     * @see LogFile#getMaximumBackupFiles()
     */
    public int getMaximumBackupFiles() {
        return 0;
    }

    /**
     * @see LogFile#getMaximumSize()
     */
    public long getMaximumSize() {
        return LogFile.UNLIMITED;
    }

    /**
     * @see LogFile#isStarted()
     */
    public boolean isStarted() {
        return false;
    }

    /**
     * @see LogFile#setMaximumBackupFiles(int)
     */
    public boolean setMaximumBackupFiles(int number) {
        return true;
    }

    /**
     * @see LogFile#setMaximumSize(long)
     */
    public boolean setMaximumSize(long size) {
        return true;
    }

    /**
     * @see LogFile#start()
     */
    public boolean start() {
        return true;
    }

    /**
     * @see LogFile#stop()
     */
    public boolean stop() {
        return true;
    }

    /**
     * @see AccessLog#getFormat()
     */
    public Format getFormat() {
        return AccessLog.Format.COMMON;
    }

    /**
     * @see AccessLog#log(HttpRequestMessage, HttpResponseMessage, String, String, String, long)
     */
    public void log(HttpRequestMessage request, HttpResponseMessage response, String version, String userId, String remoteAddr, long numBytes) {
        // nothing
    }

    /**
     * @see AccessLog#log(byte[])
     */
    public void log(byte[] message) {
        // nothing
    }

    /**
     * @see AccessLog#log(String)
     */
    public void log(String message) {
        // nothing
    }

    /**
     * @see AccessLog#setFormat(AccessLog.Format)
     */
    public void setFormat(Format format) {
        // nothing
    }

}
