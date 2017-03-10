//  @(#) 1.3 SERV1/ws/code/security.crypto/src/com/ibm/ISecurityUtilityImpl/UnsupportedCryptoAlgorithmException.java, WAS.security.crypto, WASX.SERV1, pp0919.25 8/21/05 11:15:34 [5/15/09 17:40:36]
//
//  COMPONENT_NAME: security
//
//  MODULE_NAME: UnsupportedCryptoAlgorithmException.java
//
//  ORIGINS: 27
//
//  IBM Confidential OCO Source Material
//  5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//
//  The source code for this program is not published or otherwise divested
//  of its trade secrets, irrespective of what has been deposited with the
//  U.S. Copyright Office.
//
//  THIS PRODUCT CONTAINS RESTRICTED MATERIALS OF IBM
//  All Rights Reserved *  Licensed Materials - Property of IBM
//
//  DESCRIPTION:
//
//    This module contains the JAVA implementation of
//    com.ibm.ISecurityUtilityImpl.UnsupportedCryptoAlgorithmException.java.
//
//    Note: This file is not generated from an IDL file.
//
//  CHANGE HISTORY:
//
//    Date      Programmer     Defect        Description
//    --------  -------------  ------------  -----------------------------------
//    01/11/01  dbscheer       d90016.2      duplicate PasswordUtil function in com.ibm.ISecurityUtilityImpl
//    03/02/05  BChiu          fLIDB3706-5.120  SerialUID fix

package com.ibm.websphere.crypto;

/**
 * Exception thrown when a requested crypto algorithm (ie. "xor") is unknown
 * by the runtime conversion code.
 */
public class UnsupportedCryptoAlgorithmException extends Exception {
    private static final long serialVersionUID = -1038233301900299114L;

    /**
     * Constructor.
     */
    public UnsupportedCryptoAlgorithmException() {
        super();
    }

}
