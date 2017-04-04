// 1.4, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.persistent.filemgr;

import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;

//
// Interface for FileManager.
//
public interface FileManager
    extends PhysicalFileInterface,
        Instrumentation,
        Constants
{

    public  boolean isReadOnly();

    /* Cache free storage information for fast restart */
    public  void cache_free_storage_info()
        throws IOException;


    public  void dump_disk_memory(Writer out)
        throws IOException;

    public  void dump_stats(Writer out, boolean labels)
        throws IOException;

    public  void dump_stats_header(Writer out)
        throws IOException;

    public  void reset_stats();
    
    public  long length()
        throws IOException;

    public  void dump_memory(Writer out)
        throws IOException;

    public  long start();

    public  int grain_size();

    public  long fetchUserData(long serialnumber)
        throws IOException,
               FileManagerException;

    public  boolean storeUserData(long serialnumber, long value)
        throws IOException,
               FileManagerException;

    public  long allocateAndClear(int size)
        throws FileManagerException, 
            IOException, 
            EOFException;

    /* Main allocation routine exposed in public interface */
    public  long allocate(int request_size)
        throws IOException;

     /* Main deallocation routine exposed in public interface.  This routine
        performs a disk read to read the size and then a disk write to mark
        the size tag allocated.  An optimization to avoid doing the read would
        be to store the size of all allocated blocks in a hash table. */
    public  void deallocate(long block)
        throws IOException;

    /* Main coalescing routine exposed in public interface. */
    public  void coalesce()
        throws IOException;

}






