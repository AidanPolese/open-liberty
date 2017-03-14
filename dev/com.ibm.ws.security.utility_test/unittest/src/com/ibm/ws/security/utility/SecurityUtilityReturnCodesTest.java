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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * These tests exist to make sure we do not inadventently change the return code values
 */
public class SecurityUtilityReturnCodesTest {

    /**
     * Test method for {@link com.ibm.ws.security.utility.SecurityUtilityReturnCodes#getReturnCode()}.
     */
    @Test
    public void getReturnCode_OK() {
        assertEquals("FAIL: The return code value for 'OK' was changed",
                     0, SecurityUtilityReturnCodes.OK.getReturnCode());
    }

    /**
     * Test method for {@link com.ibm.ws.security.utility.SecurityUtilityReturnCodes#getReturnCode()}.
     */
    @Test
    public void getReturnCode_ERR_GENERIC() {
        assertEquals("FAIL: The return code value for 'ERR_GENERIC' was changed",
                     1, SecurityUtilityReturnCodes.ERR_GENERIC.getReturnCode());
    }

    /**
     * Test method for {@link com.ibm.ws.security.utility.SecurityUtilityReturnCodes#getReturnCode()}.
     */
    @Test
    public void getReturnCode_ERR_SERVER_NOT_FOUND() {
        assertEquals("FAIL: The return code value for 'ERR_SERVER_NOT_FOUND' was changed",
                     2, SecurityUtilityReturnCodes.ERR_SERVER_NOT_FOUND.getReturnCode());
    }

    /**
     * Test method for {@link com.ibm.ws.security.utility.SecurityUtilityReturnCodes#getReturnCode()}.
     */
    @Test
    public void getReturnCode_ERR_CLIENT_NOT_FOUND() {
        assertEquals("FAIL: The return code value for 'ERR_CLIENT_NOT_FOUND' was changed",
                     3, SecurityUtilityReturnCodes.ERR_CLIENT_NOT_FOUND.getReturnCode());
    }

    /**
     * Test method for {@link com.ibm.ws.security.utility.SecurityUtilityReturnCodes#getReturnCode()}.
     */
    @Test
    public void getReturnCode_ERR_PATH_CANNOT_BE_CREATED() {
        assertEquals("FAIL: The return code value for 'ERR_PATH_CANNOT_BE_CREATED' was changed",
                     4, SecurityUtilityReturnCodes.ERR_PATH_CANNOT_BE_CREATED.getReturnCode());
    }

    /**
     * Test method for {@link com.ibm.ws.security.utility.SecurityUtilityReturnCodes#getReturnCode()}.
     */
    @Test
    public void getReturnCode_ERR_FILE_EXISTS() {
        assertEquals("FAIL: The return code value for 'ERR_FILE_EXISTS' was changed",
                     5, SecurityUtilityReturnCodes.ERR_FILE_EXISTS.getReturnCode());
    }

}
