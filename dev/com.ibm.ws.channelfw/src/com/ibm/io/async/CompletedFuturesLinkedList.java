//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2005, 2006
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change History:
//Date     UserId      Defect          Description
//--------------------------------------------------------------------------------
// 01/17/06 gilgen     336062          created file
// 04/21/06 wigger     364091          add SUID

package com.ibm.io.async;

import java.util.LinkedList;

/**
 * List of work items for the handler threads to process.
 * This class is only used as an aid to help in performance analysis so that
 * this use of LinkedList can be differentiated from other instances of LinkedList
 */
public class CompletedFuturesLinkedList extends LinkedList<CompletedFutureWorkItem> {
    // required SUID since this is serializable
    private static final long serialVersionUID = 4648399594223090738L;

}
