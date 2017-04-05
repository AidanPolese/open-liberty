/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.tx.embeddable;

public class NativeJDBCDriverHelper {

    public static void threadSwitch() {
        com.ibm.ws.Transaction.NativeJDBCDriverHelper.threadSwitch();
    }
}
