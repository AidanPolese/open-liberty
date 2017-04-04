// 1.4, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.persistent.filemgr;

interface Instrumentation
{
    // ------------------------------------------------------------------------
    // Instrumentation
    // ------------------------------------------------------------------------
    void update_read_time(long msec);
    void update_write_time(long msec);
    void increment_read_count();
    void increment_write_count();
    // ------------------------------------------------------------------------
    
}
