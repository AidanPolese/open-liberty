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
package com.ibm.ws.security.utility;

/**
 *
 */
public enum SecurityUtilityReturnCodes {
    OK(0),
    ERR_GENERIC(1),

    ERR_SERVER_NOT_FOUND(2),
    ERR_CLIENT_NOT_FOUND(3),
    ERR_PATH_CANNOT_BE_CREATED(4),
    ERR_FILE_EXISTS(5);

    final int rc;

    private SecurityUtilityReturnCodes(int val) {
        rc = val;
    }

    int getReturnCode() {
        return rc;
    }

}
