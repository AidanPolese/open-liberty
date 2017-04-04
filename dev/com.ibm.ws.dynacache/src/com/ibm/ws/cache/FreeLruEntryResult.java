// 1.2, 6/23/08
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

/**
 * This is the return structure for FreeLruEntry method in Cache.java.
 */
public class FreeLruEntryResult {
	
	public boolean success;      // entry evicted successful in FreeLruEntry
	public int  entriesRemoved;  // number of entries removed;
	public long bytesRemoved;    // number of bytes removed

	public FreeLruEntryResult() {
		success = false;
		entriesRemoved = 0;
		bytesRemoved = -1;
	}
}
