/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 2004       */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/*                                                                                   */
/* DESCRIPTION:                                                                      */
/*                                                                                   */
/* Change History:                                                                   */
/*                                                                                   */
/* Date      Programmer  Defect         Description                                  */
/* --------  ----------  ------         -----------                                  */
/* 20/10/04  kaczyns     LIDB1578-22    Create                                       */
/* ********************************************************************************* */

package com.ibm.ws.recoverylog.spi;

public interface ScalableFailureScope extends FailureScope
{
    /**
     * Constant representing the UUID in the group properties object.
     */
    public static final String UUID = "SFS_UUID";

    /**
     * Constant representing the server short name in the group properties
     * object.
     */
    public static final String SERVER_SHORT = "SVR_SHORT";

    /**
     * Returns the server specific UUID for the FailureScope.
     */
    public String uuid();

    /**
     * Returns the stoken for the servent of this FailureScope, or
     * returns null if this FailureScope represents all servants.
     */
    public byte[] stoken();

    /**
     * Returns the server short name.
     */
    public String serverShortName();
}
