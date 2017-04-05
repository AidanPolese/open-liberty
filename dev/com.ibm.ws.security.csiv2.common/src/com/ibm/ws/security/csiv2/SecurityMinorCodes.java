/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.csiv2;

/**
 * CSIv2 Minor Codes. Uses a subset of the minor codes from tWAS to maintain compatibility.
 * When adding new minor codes, please ensure compatibility with minor codes in tWAS.
 */
public class SecurityMinorCodes {

    public static final int SECURITY_FAMILY_BASE = 0x49424300;
    public static final int AUTHENTICATION_FAILED = SECURITY_FAMILY_BASE + 0x0;
    public static final int INVALID_IDENTITY_TOKEN = SECURITY_FAMILY_BASE + 0xC;
    public static final int IDENTITY_SERVER_NOT_TRUSTED = SECURITY_FAMILY_BASE + 0xD;

    public static final int SECURITY_DISTRIBUTED_BASE = 0x49421090;
    public static final int CREDENTIAL_NOT_AVAILABLE = SECURITY_DISTRIBUTED_BASE + 0x2;
    public static final int SECURITY_MECHANISM_NOT_SUPPORTED = SECURITY_DISTRIBUTED_BASE + 0x3;
    public static final int GSS_FORMAT_ERROR = SECURITY_DISTRIBUTED_BASE + 0x35;

}
