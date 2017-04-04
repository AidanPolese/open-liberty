package com.ibm.ws.sib.msgstore.persistence;
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
 *                 07/04/03 kschloss Original
 *                 12/09/03 pradine  Added updateTickValueOnly() and updateLockIDOnly()
 * 180053          03/11/03 gareth   Remove deprecated methods/interfaces
 * 181803          04/11/03 pradine  Eliminate nonpersistent item table
 * 180763.3        13/11/03 pradine  Add support for new tables
 * 183088          17/11/03 pradine  Refactor read interfaces
 * 180763.5        21/11/03 pradine  Add support for new PersistenceManager Interface
 * 185331          10/12/03 pradine  Deprecate the Recoverable Interface
 * 185443          12/12/03 pradine  Support returning stream ids for in-doubts
 * 185331.1        08/01/04 pradine  Continued work to deprecate the Recoverable Interface
 * 188010          22/01/04 pradine  Clean up of persistence layer interfaces
 * 180763.7        10/02/04 pradine  Add support for mutiple item tables
 * 187223.4        05/03/04 pradine  Non-cache expirer support
 * 188052.2        16/03/04 pradine  Changes to the garbage collector (continued)
 * 190379          12/04/04 pradine  Tighten up stopping behaviour
 * 183180          16/04/04 pradine  Check database tables on ME startup
 * 206674          02/06/04 schofiel Enable pluggable persistence layer in Message Store
 * 201684.1        25/06/04 pradine  Misleading method names
 * 214830.2        12/07/04 pradine  MessageStore CacheLoader Implementation
 * 205363          28/07/04 pradine  Redesign unique key generators
 * 251161          13/04/05 gareth   Add ObjectManager code to CMVC
 * 288073          13/07/05 schofiel Dump consolidation
 * SIB0112b.ms.1   07/08/06 gareth   Large message support.
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * ============================================================================
 */

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.ibm.ws.sib.msgstore.Configuration;
import com.ibm.ws.sib.msgstore.PersistenceException;
import com.ibm.ws.sib.msgstore.SevereMessageStoreException;
import com.ibm.ws.sib.msgstore.impl.MessageStoreImpl;
import com.ibm.ws.sib.msgstore.persistence.UniqueKeyGenerator;
import com.ibm.ws.sib.msgstore.transactions.impl.XidManager;
import com.ibm.ws.sib.utils.DataSlice;
import com.ibm.ws.sib.utils.ras.FormattedWriter;

/** 
 * This interface is used to retrieve items from the persistent store.
 * Writing to the persistent store is done via the
 * {@link com.ibm.ws.sib.msgstore.transactions.PersistenceManager} interface.
 *
 * @see com.ibm.ws.sib.msgstore.transactions.PersistenceManager
 */
public interface PersistentMessageStore 
{
    /**
     * Returns the serialized binary data associated with a {@link Persistable}
     * item.
     * 
     * @param item the {@link Persistable}
     * @return a byte array containing the binary data for the {@link Persistable}
     * @throws PersistenceException
     */
    // FSIB0112b.ms.1
    public List<DataSlice> readDataOnly(Persistable item) throws PersistenceException;

    /**
     * Returns the items and item references contain in a stream.
     * 
     * @param containingStream the stream to search.
     * @return a {@link java.util.List} containing {@link Persistable} objects
     * @throws PersistenceException
     */
    public List readNonStreamItems(Persistable containingStream) throws PersistenceException;

    /**
     * Read all item streams and reference streams.
     * 
     * @return a {@link java.util.List} containing {@link Persistable} objects
     * @throws PersistenceException
     */
    public List readAllStreams() throws PersistenceException;

    /**
     * Identify all streams that contain items that can expire
     *
     * @return a {@link java.util.Set} containing {@link Long} objects
     * @throws PersistenceException
     */
    public Set identifyStreamsWithExpirableItems() throws PersistenceException;

    /**
     * Read all in-doubt transaction ids.
     * 
     * @return a {@link java.util.List} containing
     * {@link com.ibm.ws.sib.msgstore.transactions.PersistentTranId} objects
     * @throws PersistenceException
     */
    public List readIndoubtXIDs() throws PersistenceException;

    /**
     * Identify all streams that contain in-doubt items
     * 
     * @return a {@link java.util.Set} containing {@link Long} objects
     * @throws PersistenceException
     */
    public Set identifyStreamsWithIndoubtItems() throws PersistenceException;

    /**
     * Return the root persistable.
     * 
     * @return a {@link Persistable}
     * @throws PersistenceException
     */
    public Persistable readRootPersistable() throws PersistenceException, SevereMessageStoreException;

    /**
     * Initialises the Persistent Message Store.
     * 
     * @param msi Reference to the owning MessageStoreImpl which offers a variety of utility methods
     * @param xidManager The transaction layer's XidManager
     * @param configuration The configuration for the persistence layer 
     */
    public void initialize(MessageStoreImpl msi, XidManager xidManager, Configuration configuration);

    /**
     * Starts the Persistent Message Store.
     */
    public void start() throws PersistenceException;

    /**
     * Stops the Persistent Message Store.
     * 
     * @param mode specifies the type of stop operation which is to be performed.
     */
    public void stop(int mode);

    /**
     * Creates a UniqueKeyGenerator object with a persistent range counter.
     * 
     * @param name   The unique name of the generator
     * @param range  The batch size for allocation of unique id's
     * 
     * @return A new UniqueKeyGenerator
     */
    public UniqueKeyGenerator getUniqueKeyGenerator(String name, int range);

    /** Request that the receiver prints its xml representation
     * (recursively) onto writer.
     * @param writer
     * @throws IOException
     */
    public abstract void xmlWriteOn(FormattedWriter writer) throws IOException;
}
