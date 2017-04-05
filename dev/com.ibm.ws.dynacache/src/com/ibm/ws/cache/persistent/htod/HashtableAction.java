// 1.4, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.persistent.htod;

/**************************************************************************
 * HashtableAction.  This defines a callback interface for use in iterating
 *       the hashtable.  To use, implement the interface and pass it to
 *       HashtableOnDisk.iterateKeys or HashtableOnDisk.iterateObjects.  The
 *       is iterated and this callback is invoked for each object found.
 *
 * @param entry This is the HashtableEntry for the object found in the current
 *       iteration.
 *************************************************************************/
public interface HashtableAction
{
    public boolean execute(HashtableEntry entry) throws Exception;
}

