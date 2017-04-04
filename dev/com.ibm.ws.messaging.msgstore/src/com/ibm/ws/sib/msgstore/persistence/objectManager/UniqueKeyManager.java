package com.ibm.ws.sib.msgstore.persistence.objectManager;
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
 *  Reason        Date     Origin    Description
 * ------------- -------- --------- ----------------------------------------
 * 251161        14/04/05  gareth    Add ObjectManager code to CMVC
 * 286523        21/07/05  gareth    Make sure we are started before create
 * 306998.20     09/01/06  gareth    Add new guard condition to trace statements
 * ============================================================================
 */

import com.ibm.ws.objectManager.*;

import com.ibm.ws.sib.msgstore.persistence.*;
import com.ibm.ws.sib.msgstore.PersistenceException;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.msgstore.MessageStoreConstants;
import com.ibm.ws.sib.utils.ras.SibTr;

public class UniqueKeyManager
{
    private static TraceComponent tc = SibTr.register(UniqueKeyManager.class, 
                                                      MessageStoreConstants.MSG_GROUP, 
                                                      MessageStoreConstants.MSG_BUNDLE);

    private Token _uniqueKeyRootToken;

    private Thread                _rangeManagerThread;
    private UniqueKeyRangeManager _rangeManager;

    public void start(Anchor anchor, ObjectManager objectManager, ObjectStore objectStore) throws PersistenceException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "start", "");
        
        // Get root token
        _uniqueKeyRootToken = anchor.getUniqueKeyTableToken();

        // Does our root token exist?
        if (_uniqueKeyRootToken == null)
        {
            try
            {
                // If so we need to create it and add it 
                // to the existing message store structure.
                Transaction tran = objectManager.getTransaction();

                // Create the list that represents our colection of 
                // unique key generators.
                LinkedList uniqueKeyRootList = new LinkedList(tran, objectStore);
                _uniqueKeyRootToken = uniqueKeyRootList.getToken();

                // Add to transaction and include update to 
                // the root anchor object.
                tran.lock(anchor);
                anchor.setUniqueKeyTableToken(_uniqueKeyRootToken);
                tran.replace(anchor);
                tran.commit(false);

                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "UniqueKeyGenerator root list created.");
            }
            catch (ObjectManagerException ome)
            {
                com.ibm.ws.ffdc.FFDCFilter.processException(ome, "com.ibm.ws.sib.msgstore.persistence.objectManager.UniqueKeyManager.start", "1:78:1.5", this);
                if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.event(tc, "Exception caught initializing root of unique key generator list!", ome);
                if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "start");
                throw new PersistenceException("Exception caught initializing root of unique key generator list!", ome);
            }
        }

        // Create range manager
        _rangeManager = new UniqueKeyRangeManager(_uniqueKeyRootToken, objectManager, objectStore);
        _rangeManager.start();

        // Start up async part of range manager
        _rangeManagerThread = new Thread(_rangeManager);
        _rangeManagerThread.setName("UniqueKeyRangeManager");
        _rangeManagerThread.setDaemon(true);
        _rangeManagerThread.start();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "start");
    }

    public void stop()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "stop");

        // Call the rangeManager to stop. This should
        // trigger the death of the rangeManagerThread
        // aswell.
        if (_rangeManager != null)
        {
            _rangeManager.stop();

            _rangeManager = null;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "stop");
    }

    public UniqueKeyGenerator createUniqueKeyGenerator(String name, long range) throws PersistenceException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "createUniqueKeyGenerator", "Name="+name+", Range="+range);

        if (_rangeManager == null)
        {
            PersistenceException pe = new PersistenceException("Request to create new UniqueKeyGenerator failed. UniqueKeyManager not started!");
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.event(tc, "Request to create new UniqueKeyGenerator failed. UniqueKeyManager not started!");
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "createUniqueKeyGenerator");
            throw pe;
        }
        
        UniqueKeyGeneratorImpl generator = new UniqueKeyGeneratorImpl(_rangeManager, name, range);

        try
        {
            generator.initialize();
        }
        catch (PersistenceException pe)
        {
            com.ibm.ws.ffdc.FFDCFilter.processException(pe, "com.ibm.ws.sib.msgstore.persistence.objectManager.UniqueKeyManager.createUniqueKeyGenerator", "1:135:1.5", this);
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.event(tc, "Exception caught initializing unique key generator!", pe);
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "createUniqueKeyGenerator");
            throw pe;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "createUniqueKeyGenerator", "return="+generator);
        return generator;
    }
}
