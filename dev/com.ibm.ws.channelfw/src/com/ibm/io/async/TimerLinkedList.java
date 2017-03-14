//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2005, 2006
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change History:
//Date     UserId      Defect          Description
//--------------------------------------------------------------------------------
// 12/01/05 gilgen      328131          add SUID
package com.ibm.io.async;

import java.util.LinkedList;

/**
 * List of work items for the timer thread to process.
 * This class is only used as an aid to help in performance analysis so that
 * this use of LinkedList can be differentiated from other instances of LinkedList
 */
public class TimerLinkedList extends LinkedList<TimerWorkItem> {
    // required SUID since this is serializable
    private static final long serialVersionUID = -1590870373192807194L;

    /**
     * a unique ID for each work item that will be in the timer slots that are
     * in this list. It is put here since the code will be synchronizing
     * on this list when creating uniqueID's.
     */
    // public long uniqueID = 0;

}
