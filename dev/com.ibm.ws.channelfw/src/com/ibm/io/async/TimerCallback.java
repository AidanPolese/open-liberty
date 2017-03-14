//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2005, 2006
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change History:
//Date      UserId      Defect          Description
//--------------------------------------------------------------------------------
// 11/21/05 wigger      325773         add re-use count for race condition detection

package com.ibm.io.async;

/**
 * Callback used for AIO timeouts.
 */
public interface TimerCallback {

    /**
     * Called when timeout has triggered.
     * 
     * @param twi attachment object passed in when timeout work item was created
     */
    void timerTriggered(TimerWorkItem twi);

}
