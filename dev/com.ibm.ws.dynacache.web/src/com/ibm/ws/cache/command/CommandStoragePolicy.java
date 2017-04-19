// 1.7, 10/8/07
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
 * This interface supports multiple storage policies for commands.
 * The default policy caches the command in serialized form, and
 * makes a copy of the command when putting it in the cache and
 * when giving it out during a cache hit.
 * A name of a class implementing this interface can be provided in the
 * dynacache.xml configuration file.
 */
public interface CommandStoragePolicy extends Serializable {

    /**
     * This converts the executed command into the cached representation.
     *
     * @param cacheableCommand The command to put in the cache.
     * @return The cached representation of the command.
     */
    public Serializable prepareForCache(CacheableCommand cacheableCommand);

    /**
     * This converts the cached representation of the command into something
     * that can be given out during a cache hit.
     *
     * @param object The cached representation of the command.
     * @return The command that is given out during a cache hit.
     */
    public CacheableCommand prepareForCacheAccess(Serializable object, DCache cache, EntryInfo ei);

}
