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
 * Reason          Date   Origin     Description
 * --------------- ------ --------   --------------------------------------------
 *                 131103 pradine    Original
 * 185331.1        080104 pradine    Continued work to deprecate the Recoverable Interface
 * 191800          240204 pradine    Add NLS support to the persistence layer
 * 188052          100304 pradine    Changes to the garbage collector
 * 213328          300604 pradine    Perform synchronous delete during 2PC processing
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore.persistence;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.ws.sib.msgstore.MessageStoreConstants;

/**
 * Enumerated type for {@link com.ibm.ws.sib.msgstore.persistence.impl.Tuple}
 * objects.
 *
 * @author pradine
 */
public class TupleTypeEnum {
    private static TraceNLS nls = TraceNLS.getTraceNLS(MessageStoreConstants.MSG_BUNDLE);
        
    /**
     * Item tuple. Usually represents the thing being stored.
     */
    public static final TupleTypeEnum ITEM = new TupleTypeEnum("II");
    
    /**
     * Item Reference tuple. Used as a pointer to {@link #ITEM} tuples
     */
    public static final TupleTypeEnum ITEM_REFERENCE = new TupleTypeEnum("RI");
    
    /**
     * Item Stream tuple. Contains other tuples such as {@link #ITEM},
     * and {@link #REFERENCE_STREAM}.
     */
    public static final TupleTypeEnum ITEM_STREAM = new TupleTypeEnum("IS");
    
    /**
     * Reference Stream tuple. Contains {@link #ITEM_REFERENCE} tuples.
     */
    public static final TupleTypeEnum REFERENCE_STREAM = new TupleTypeEnum("RS");
    
    /**
     * Root tuple. Represents the top of the hierarchy.
     */
    public static final TupleTypeEnum ROOT = new TupleTypeEnum("RT");
    
    private String type;
    
    /*
     * Private constructor in order to prevent instantiation
     */
    private TupleTypeEnum(String type) {
        this.type = type;
    }
    
    /**
     * Factory method for <code>TupleTypeEnum</code> objects.
     * 
     * @param type the string corresponding to the type of <code>TupleTypeEnum</code> that you want.
     * @return a <code>TupleTypeEnum</code> object.
     */
    public static TupleTypeEnum getInstance(String type) {
        if (type.equals(ITEM.type))
            return ITEM;
        else if (type.equals(ITEM_REFERENCE.type))
            return ITEM_REFERENCE;
        else if (type.equals(ITEM_STREAM.type))
            return ITEM_STREAM;
        else if (type.equals(REFERENCE_STREAM.type))
            return REFERENCE_STREAM;
        else if (type.equals(ROOT.type))
            return ROOT;
        else
            throw new IllegalArgumentException(nls.getFormattedMessage("INVALID_TUPLE_TYPE_SIMS1502",
                                                                       new Object[] {type},
                                                                       null));
    }
    
    /*
     *  (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return type;
    }
}
