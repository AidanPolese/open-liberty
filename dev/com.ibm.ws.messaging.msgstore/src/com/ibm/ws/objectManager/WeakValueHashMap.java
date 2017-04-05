package com.ibm.ws.objectManager;

/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2013
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 *
 */

/**
 * Maps values as long as they are referenced elswhere in the JVM.
 * 
 * A variant of java.util.WeakHashMap that removes mappings when the value
 * becomes unreferenced rather than the key. Note that the Value returned by
 * a Map.Entry will be the containing WeakEntry so to get the referenced Value
 * you need to use Map.Entry.getValue().get();
 * 
 * @See java.util.WeakHashMap.
 */
public class WeakValueHashMap
                extends java.util.HashMap
{
    private static final long serialVersionUID = -1200413598564584640L;
    // Reference queue for cleared WeakEntries
    private final java.lang.ref.ReferenceQueue queue = new java.lang.ref.ReferenceQueue();

    /**
     * Remove the unreferenced Entries from the map.
     */
    private final void clearUnreferencedEntries()
    {
        for (WeakEntry entry = (WeakEntry) queue.poll(); entry != null; entry = (WeakEntry) queue.poll()) {
            WeakEntry removedEntry = (WeakEntry) super.remove(entry.key);
            // The entry for this key may have been replaced with another one after it was dereferenced.
            // Make sure we removed the correct entry.
            if (removedEntry != entry) {
                super.put(entry.key, removedEntry);
            }
        } // for...
    } // clearUnreferencedEntries().

    public final int size() {
        clearUnreferencedEntries();
        return super.size();
    } // size().

    public final boolean isEmpty() {
        return size() == 0;
    } // isEmpty().

    public final Object get(Object key) {
        clearUnreferencedEntries();
        WeakEntry weakEntry = (WeakEntry) super.get(key);
        if (weakEntry == null)
            return null;
        else
            return weakEntry.get();
    } // get().

    public final Object put(Object key, Object value) {
        clearUnreferencedEntries();
        WeakEntry weakEntry = (WeakEntry) super.put(key, new WeakEntry(key, value));
        Object existingValue = null;
        if (weakEntry != null) {
            existingValue = weakEntry.get();
            weakEntry.clear();
        }
        return existingValue;
    } // put().

    public final Object remove(Object key)
    {
        WeakEntry weakEntry = (WeakEntry) super.remove(key);
        Object value = null;
        if (weakEntry != null) {
            value = weakEntry.get();
            weakEntry.clear();
        } // if (weakEntry != null). 
        clearUnreferencedEntries();
        return value;
    }

    public final void clear() {
        // Dump the existing reference queue.
        while (queue.poll() != null);
        super.clear();
    } // clear().

    /**
     * Weak reference for the value, but also holds the key.
     */
    private final class WeakEntry
                    extends java.lang.ref.WeakReference
    {
        private Object key;

        /**
         * Create new entry.
         * 
         * @param key matching the value stored.
         * @param value being stored.
         */
        WeakEntry(Object key,
                  Object value)
        {
            super(value,
                  queue);
            this.key = key;

        } // WeakEntry.
    } // class WeakEntry. 
} // Of WeakValueHashMap.
