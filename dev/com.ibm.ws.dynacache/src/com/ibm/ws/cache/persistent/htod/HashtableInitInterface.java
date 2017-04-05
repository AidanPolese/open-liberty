// 1.4, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.persistent.htod;

/*************************************************************************
 * HashtableInterface.  This interface is used during initialization of the
 *      hashtable if the hashtable is being initialized from disk and the
 *      hashtable was not properly closed.  Applications that extend the 
 *      HashtableOnDisk or logically extend it via embedding may need to
 *      do their own cleanup also.   Part of recovery is to iterate the
 *      hashtable, passing each key and object to this interface.
 *
 * Note.  This is really old.  I wonder if we should be using the 
 * HashtableAction instead.
 *      
 *************************************************************************/
public interface HashtableInitInterface
{
    void initialize(Object key, Object value);
}
