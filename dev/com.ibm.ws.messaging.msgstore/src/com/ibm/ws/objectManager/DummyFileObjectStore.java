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
 *
 * Change activity:
 *
 *  Reason           Date    Origin     Description
 * --------------- -------- -------- ------------------------------------------
 *  607710         21/08/09 gareth    Add isAnyTracingEnabled() check around trace
 *  733546         26/04/12 slaterpa  Make tokenSet transient
 * ============================================================================
 */

import com.ibm.ws.objectManager.utils.Trace;
import com.ibm.ws.objectManager.utils.Tracing;

/**
 * <p>
 * Behaves like a persistent object store but does not write anything to disk. Used my ObjectManagerState
 * to hold its persistent form once it has been restored from the checkpoint ara of the log.
 * Recovery is not possible when this is used.
 * 
 * @author IBM Corporation
 */
public final class DummyFileObjectStore
                extends AbstractObjectStore
{
    private static final Class cclass = DummyFileObjectStore.class;
    private static Trace trace = ObjectManager.traceFactory.getTrace(cclass,
                                                                     ObjectManagerConstants.MSG_GROUP_STORE);
    private static final long serialVersionUID = 4029554983273984385L;

    /**
     * Constructor
     * 
     * @param storeName Identifies the ObjecStore and the file directory.
     * @param objectManager The ObjectManager that manages this store.
     * @throws ObjectManagerException
     */

    public DummyFileObjectStore(String storeName,
                                ObjectManager objectManager)
        throws ObjectManagerException
    {
        super(storeName,
              objectManager,
              STRATEGY_KEEP_ALWAYS);

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()) {
            trace.entry(this,
                        cclass,
                        "<init>",
                        "StoreName=" + storeName + ", ObjectManager=" + objectManager);
            trace.exit(this,
                       cclass,
                       "<init>");
        } // if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()).
    } // End of constructor.

    /**
     * Constructor, used during ObjectManagerState construction.
     * 
     * @param objectManagerState The ObjectManager that manages this store.
     * @throws ObjectManagerException
     */
    protected DummyFileObjectStore(ObjectManagerState objectManagerState)
        throws ObjectManagerException
    {
        super(objectManagerState);

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()) {
            trace.entry(this,
                        cclass,
                        "<init>",
                        objectManagerState);
            trace.exit(this,
                       cclass,
                       "<init>");
        } // if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()).
    } // End of constructor.

    // --------------------------------------------------------------------------
    // extends AbstractObjectStore.
    // --------------------------------------------------------------------------

    /**
     * Always throws InMemoryObjectNotAvailableException.
     * 
     * @throws InMemoryObjectNotAvailableException because all ManagedObjects are unavailable.
     * 
     * @see com.ibm.ws.objectManager.ObjectStore#get(com.ibm.ws.objectManager.Token)
     */
    public ManagedObject get(Token token)
                    throws ObjectManagerException
    {
        final String methodName = "get";
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()) {
            trace.entry(this,
                        cclass,
                        methodName,
                        token);
            trace.exit(this,
                       cclass,
                       methodName);
        } // if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()).
        throw new InMemoryObjectNotAvailableException(this,
                                                      token);
    } // get().

    /**
     * For DummyObjectstore there is nothing to do. Blocks until this has completed.
     * 
     * @see com.ibm.ws.objectManager.ObjectStore#flush()
     */
    public synchronized void flush()
                    throws ObjectManagerException
    {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()) {
            trace.entry(this,
                        cclass,
                        "flush");
            trace.exit(this,
                       cclass,
                       "flush");
        }
    } // flush().

    private transient Set tokenSet; // Initialised if used.

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.objectManager.ObjectStore#tokens()
     */
    public Set tokens() {
        if (tokenSet == null) {
            tokenSet = new AbstractSetView() {
                public long size() {
                    return 0;
                }

                public Iterator iterator() {
                    return new Iterator() {

                        public boolean hasNext()
                        {
                            return false;
                        } // hasNext().

                        public Object next()
                        {
                            throw new java.util.NoSuchElementException();
                        } // next().

                        public boolean hasNext(Transaction transaction)
                                        throws ObjectManagerException
                        {
                            throw new UnsupportedOperationException();
                        } // hasNext().

                        public Object next(Transaction transaction)
                                        throws ObjectManagerException
                        {
                            throw new UnsupportedOperationException();
                        } // next().

                        public Object remove(Transaction transaction)
                                        throws ObjectManagerException
                        {
                            throw new UnsupportedOperationException();
                        } // remove().

                    }; // new Iterator().
                } // iterator().  
            }; // new AbstractSetView().
        } // if (tokenSet == null). 
        return tokenSet;
    } // tokens().

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.objectManager.ObjectStore#captureStatistics()
     */
    public java.util.Map captureStatistics()
                    throws ObjectManagerException
    {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this,
                        cclass,
                        "captureStatistics");

        java.util.Map statistics = super.captureStatistics();

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this,
                       cclass,
                       "captureStatistics",
                       "return statistics=" + statistics);
        return statistics;
    } // captureStatistics().
} // class DummyFileObjectStore.
