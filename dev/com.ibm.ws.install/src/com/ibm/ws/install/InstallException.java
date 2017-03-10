/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.install;

import java.util.ArrayList;
import java.util.List;

/**
 * This exception indicates that there is an installation exception.
 */
public class InstallException extends Exception {

    private static final long serialVersionUID = -2397755301510300348L;
    public static final int BAD_ARGUMENT = 20; // same as ReturnCode.BAD_ARGUMENT
    public static final int RUNTIME_EXCEPTION = 21; // same as ReturnCode.RUNTIME_EXCEPTION
    public static final int ALREADY_EXISTS = 22; // same as ReturnCode.ALREADY_EXISTS
    public static final int BAD_FEATURE_DEFINITION = 23; // same as ReturnCode.BAD_FEATURE_DEFINITION
    public static final int MISSING_CONTENT = 24; // same as ReturnCode.MISSING_CONTENT
    public static final int IO_FAILURE = 25; // same as ReturnCode.IO_FAILURE
    public static final int NOT_VALID_FOR_CURRENT_PRODUCT = 29; // same as ReturnCode.NOT_VALID_FOR_CURRENT_PRODUCT
    public static final int CONNECTION_FAILED = 33; // same as ReturnCode.CONNECTION_FAILED for installUtility

    int rc = RUNTIME_EXCEPTION;
    List<Object> data = new ArrayList<Object>();

    /**
     * @param message
     * @param cause
     * @parm rc
     */
    public InstallException(String message, Throwable cause, int rc) {
        super(message, cause);
        this.rc = rc;
    }

    /**
     * @param message
     */
    public InstallException(String message) {
        super(message);
    }

    /**
     * @param message
     * @parm rc
     */
    public InstallException(String message, int rc) {
        super(message);
        this.rc = rc;
    }

    /**
     * @return the rc
     */
    public int getRc() {
        return rc;
    }

    /**
     * @return the Data
     */
    public List<Object> getData() {
        return data;
    }

    /**
     * @param Data the Data to set
     */
    public void setData(Object... objects) {
        for (Object s : objects) {
            this.data.add(s);
        }
    }

}
