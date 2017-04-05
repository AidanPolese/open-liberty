// 1.2, 10/10/05
// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2005
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.ws.cache;


import java.util.ArrayList;

public interface EvictAction {

public ArrayList walkDiskCache(int evictPolicy, int deleteEntries, long deleteSize);

public void deleteFromDisk(ArrayList delEntries);

}

