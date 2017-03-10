/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot;

/**
 * The ClientRunnerException is used when an unknown exception occurs while running
 * ClientRunner.run() that executes the main() method of the main class in a client module.
 * The exception message will contain information describing the condition.
 */
@SuppressWarnings("serial")
public class ClientRunnerException extends LaunchException {
    private final ReturnCode returnCode = ReturnCode.CLIENT_RUNNER_EXCEPTION;

    public ClientRunnerException(String message, String translatedMsg) {
        super(message, translatedMsg);
        setReturnCode(returnCode);
    }

    public ClientRunnerException(String message, String translatedMsg, Throwable cause) {
        super(message, translatedMsg, cause);
        setReturnCode(returnCode);
    }

    public ClientRunnerException(String message, String translatedMsg, Throwable cause, ReturnCode rc) {
        super(message, translatedMsg, cause);
        setReturnCode(rc);
    }

}
