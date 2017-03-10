/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.feature.internal.cmdline;

/**
 *
 */
public class FeatureToolException extends RuntimeException {
    /**  */
    private static final long serialVersionUID = 6441973779054340565L;

    private ReturnCode returnCode = ReturnCode.RUNTIME_EXCEPTION;
    private final String translatedMsg;

    public FeatureToolException(String message, String translatedMsg) {
        super(message);
        this.translatedMsg = translatedMsg;
    }

    public FeatureToolException(String message, String translatedMsg, Throwable cause) {
        super(message, cause);
        this.translatedMsg = translatedMsg;
    }

    public FeatureToolException(String message, String translatedMsg, Throwable cause, ReturnCode rc) {
        super(message, cause);
        this.translatedMsg = translatedMsg;
        this.returnCode = rc;
    }

    public ReturnCode getReturnCode() {
        return returnCode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Throwable#getLocalizedMessage()
     */
    @Override
    public String getLocalizedMessage() {
        if (translatedMsg == null)
            return getMessage();

        return translatedMsg;
    }
}
