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
package com.ibm.wsspi.stackManager;

/**
 * Exception indicated an operation was attempted on a stack name that can not be found.
 */
public class NoSuchStackNameException extends Exception {

    private String stackName;

    /**
     * Constructor.
     * 
     * @param stackName for the exception
     * @param cause
     */
    public NoSuchStackNameException(String stackName) {

        setStackName(stackName);
    }

    /**
     * Get the stack name that caused this exception.
     * 
     * @return the stack name
     */
    public String getStackName() {
        return stackName;
    }

    /**
     * Set the stack name that caused this exception.
     */
    public void setStackName(String stackName) {
        this.stackName = stackName;
    }
}
