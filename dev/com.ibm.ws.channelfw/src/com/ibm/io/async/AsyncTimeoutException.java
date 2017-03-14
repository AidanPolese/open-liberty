// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2005, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------
// 08/31/05 gilgen      LIDB3618-2      M2/M3 drops 
// 09/01/05 gilgen      302453          M3 code updates
// 12/01/05 gilgen      328131          add SUID
// 12/07/05 gilgen      329710          don't let Internal Error go in timeout exceptions

package com.ibm.io.async;

/**
 * Checked exception thrown when an asynchronous operation has been prematurely completed because it
 * timed out.
 */
public class AsyncTimeoutException extends AsyncException {
    // required SUID since this is serializable
    private static final long serialVersionUID = -5699872437960867150L;

    AsyncTimeoutException() {
        super(AsyncProperties.aio_operation_timedout, "Timeout", 0);
    }
}
