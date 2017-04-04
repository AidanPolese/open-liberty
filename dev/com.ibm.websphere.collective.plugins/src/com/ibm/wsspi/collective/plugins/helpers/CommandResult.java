/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.collective.plugins.helpers;

import java.util.Date;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.wsspi.collective.plugins.TaskStorage;

/**
 * Helper class that encapsulates the result of a single command.
 *
 * @ibm-spi
 */
public class CommandResult {

    private String description;
    private String status;
    private int returnCode;
    private String stdout;
    private String stderr;
    private long timestamp;

    /**
     * @param description
     * @param status
     * @param returnCode
     * @param stdout
     * @param stderr
     */
    @Trivial
    public CommandResult(String description, String status, int returnCode, String stdout, String stderr) {
        super();
        this.description = description;
        this.status = status;
        this.returnCode = returnCode;
        this.stdout = stdout;
        this.stderr = stderr;
        this.timestamp = new Date().getTime();
    }

    /**
     * Convenience constructor when we just want to add a description and status, and set the current time stamp.
     */
    public CommandResult(String description, String status) {
        this.description = description;
        this.status = status;
        this.returnCode = 0;
        this.stdout = null;
        this.stderr = null;
        this.timestamp = new Date().getTime();
    }

    /**
     * Convenience constructor for failed results
     */
    public CommandResult(String description, int returnCode, String stderr) {
        this(description, TaskStorage.STATUS_FAILED);
        this.returnCode = returnCode;
        this.stderr = stderr;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return PasswordUtils.maskPasswords(description);
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the returnCode
     */
    public int getReturnCode() {
        return returnCode;
    }

    /**
     * @param returnCode the returnCode to set
     */
    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    /**
     * @return the stdout
     */
    public String getStdout() {
        return PasswordUtils.maskPasswords(stdout);
    }

    /**
     * @param stdout the stdout to set
     */
    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    /**
     * @return the stderr
     */
    public String getStderr() {
        return PasswordUtils.maskPasswords(stderr);
    }

    /**
     * @param stderr the stderr to set
     */
    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
