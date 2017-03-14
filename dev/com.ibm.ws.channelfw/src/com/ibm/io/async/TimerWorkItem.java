//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2005, 2006
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change History:
//Date     UserId      Defect          Description
//--------------------------------------------------------------------------------
// 11/16/05 gilgen      324433         Eliminate uniqueID 
// 11/21/05 wigger      325773         add re-use count for race condition detection

package com.ibm.io.async;

/**
 * The work item that will be queued from the requesting thread to the timer
 * thread. get/set methods are not used for accessing variables in order to help
 * performance, also no synchronization is done within this class.
 */
public class TimerWorkItem {

    // public final static long START_TIMER_REQUEST = 0;
    // public final static long CANCEL_TIMER_REQUEST = 1;

    /** State indicating this timer item is currently active */
    public final static long ENTRY_ACTIVE = 1L;
    /** State indicating this timer item is currently cancelled */
    public final static long ENTRY_CANCELLED = 2L;

    /** the time at which the timeout should trigger */
    long timeoutTime = 0L;

    /** Current state of this work item */
    public long state = ENTRY_ACTIVE;

    /** Callback used if/when this times out */
    TimerCallback callback = null;

    /** Attachment used during callback usage */
    Object attachment = null;

    /** ID used to protect against rapid timeout conflicts */
    int futureCount = 0;

    /**
     * Empty Constructor
     */
    public TimerWorkItem() {
        // nothing to do
    }

    /**
     * Constructor.
     * 
     * @param _timeoutTime
     * @param _callback
     * @param _attachment
     * @param _futureCount
     */
    public TimerWorkItem(long _timeoutTime,
                         TimerCallback _callback,
                         Object _attachment,
                         int _futureCount) {

        this.timeoutTime = _timeoutTime;
        this.callback = _callback;
        this.attachment = _attachment;
        this.futureCount = _futureCount;
    }

}
