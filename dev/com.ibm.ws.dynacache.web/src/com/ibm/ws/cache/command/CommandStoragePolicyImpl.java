// 1.9, 10/8/07
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.command;

import java.io.*;
import com.ibm.websphere.command.*;
import com.ibm.ws.cache.EntryInfo;
import com.ibm.ws.cache.intf.DCache;

/**
 * This class provides the default implementation of the CommandStoragePolicy
 * interface.
 * It caches the command in serialized form, and
 * makes a copy of the command when putting it in the cache and
 * when giving it out during a cache hit.
 */
public class CommandStoragePolicyImpl implements CommandStoragePolicy
{
    private static final long serialVersionUID = 1275064778046836019L;
    
    /**
     * This implements the method in the CommandStoragePolicy interface.
     *
     * @param cacheableCommand The command to put in the cache.
     * @return The cached representation of the command.
     */
    public Serializable prepareForCache(CacheableCommand command)
    {
       return command;
    }

    /**
     * This implements the method in the CommandStoragePolicy interface.
     *
     * @param object The cached representation of the command.
     * @return The command that is given out during a cache hit.
     */
    public CacheableCommand prepareForCacheAccess(Serializable inputObject, DCache cache, EntryInfo ei)
    {
        return (CacheableCommand) inputObject;
    }
}
