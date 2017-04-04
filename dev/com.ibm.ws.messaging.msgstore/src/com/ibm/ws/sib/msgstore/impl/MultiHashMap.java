package com.ibm.ws.sib.msgstore.impl;
/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 *                 21/03/03 drphill  Original
 * 205363          28/07/04 pradine  Redesign unique key generators
 * 223743          13/08/04 drphill  Implement generic Map interface
 * LIDB3706-5.239  19/01/05 gareth   Add Serialization support
 * 288073          13/07/05 schofiel Dump consolidation
 * 454303          26/07/07 gareth   Various FINDBUGS changes
 * ============================================================================
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.ibm.ws.sib.msgstore.XmlConstants;
import com.ibm.ws.sib.msgstore.cache.links.AbstractItemLink;
import com.ibm.ws.sib.utils.ras.FormattedWriter;

/** A hash map with greater parallelism
 */
public class MultiHashMap implements Map, XmlConstants
{
    private static final class SubMap extends HashMap 
    {
        private static final long serialVersionUID = -4648138397952456765L;

        private final synchronized Object get(long key)
        {
            return get(Long.valueOf(key));
        }
        private final synchronized Object put(long key, Object value)
        {
            return put(Long.valueOf(key), value);
        }
        private final synchronized Object remove(long key)
        {
            return remove(Long.valueOf(key));
        }
    }

    private final int _subMapCount;
    private final SubMap[] _subMaps;

    /**
     * 
     */
    public MultiHashMap(int subMapCount)
    {
        super();
        _subMapCount = subMapCount;
        _subMaps = new SubMap[_subMapCount];
        for (int i = 0; i < _subMaps.length; i++)
        {
            _subMaps[i] = new SubMap();
        }
    }

    private final SubMap _subMap(final long key)
    {
        return _subMaps[(int) Math.abs(key) % _subMapCount];
    }

    /**
     * used in MessageStoreImpl.findById
     * @param key
     */
    public final AbstractItemLink get(final long key)
    {
        return(AbstractItemLink)_subMap(key).get(key);
    }

    /**
     * used in MessageStoreImpl.register
     * @param key
     * @param value
     */
    public final void put(final long key, final AbstractItemLink value)
    {
        _subMap(key).put(key, value);
    }

    /**
     * used in MessageStoreImpl.unregister
     * @param key
     */
    public final AbstractItemLink remove(final long key)
    {
        return(AbstractItemLink)_subMap(key).remove(key);
    }

    public final void clear()
    {
        for (int i = 0; i < _subMaps.length; i++)
        {
            _subMaps[i].clear();
        }
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.impl.Map#xmlWriteOn(com.ibm.ws.sib.utils.ras.FormattedWriter)
     */
    public void xmlWriteOn(FormattedWriter writer) throws IOException 
    {
        writer.newLine();
        writer.startTag(XML_ITEM_MAP);
        writer.indent();
        for (int i = 0; i < _subMaps.length; i++)
        {
            Iterator iterator = _subMaps[i].values().iterator();
            while (iterator.hasNext())
            {
                writer.newLine();
                AbstractItemLink ail = (AbstractItemLink) iterator.next();
                ail.xmlShortWriteOn(writer);
            }
        }
        writer.outdent();
        writer.newLine();
        writer.endTag(XML_ITEM_MAP);
    }
}
