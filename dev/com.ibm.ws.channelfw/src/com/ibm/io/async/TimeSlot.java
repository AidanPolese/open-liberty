//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2005, 2006
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change History:
//Date     UserId      Defect          Description
//--------------------------------------------------------------------------------

package com.ibm.io.async;

/**
 * A time slot, or bucket, which represent a particular timeout time. This object
 * can also be a node in a list of time slots.
 * get/set methods are not used for performance gains, also no synchronization
 * is done within this class.
 */
public class TimeSlot {

    protected final static int TIMESLOT_SIZE = 300;
    protected final static int TIMESLOT_LAST_ENTRY = TIMESLOT_SIZE - 1;

    /**
     * The timeout value that this slot represents.
     */
    long timeoutTime = 0;

    /**
     * that last valid entry that is in the work item array
     */
    int lastEntryIndex = -1;

    /**
     * work item array which is filled once from top (0 -index) to bottom.
     */
    TimerWorkItem[] entries = new TimerWorkItem[TIMESLOT_SIZE];

    /**
     * the last time a new entry was added to the work item array
     */
    long mostRecentlyAccessedTime = 0;

    /**
     * a reference to the next slot which follows this one
     */
    TimeSlot nextEntry = null;

    /**
     * a reference to the previous slot which follows this one
     */
    TimeSlot prevEntry = null;

    /**
     * Constructor.
     * 
     * @param _timeoutTime
     */
    public TimeSlot(long _timeoutTime) {
        this.timeoutTime = _timeoutTime;
    }

    /**
     * Add a timer item.
     * 
     * @param addItem
     * @param curTime
     */
    public void addEntry(TimerWorkItem addItem, long curTime) {
        // this routine assumes the slot is not full

        this.mostRecentlyAccessedTime = curTime;
        this.lastEntryIndex++;
        this.entries[lastEntryIndex] = addItem;
    }

}
