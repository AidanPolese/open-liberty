package com.ibm.ws.sib.msgstore.persistence.impl;

/*
 * 
 * 
 * ============================================================================
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2012,2014
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
 * 190181          10/02/04 schofiel Creation
 * 191575          20/02/04 pradine  Add support for MAXDEPTH column in the Item Table
 * 188051          29/03/04 pradine  Add support for temporary tables
 * 206970          03/06/04 schofiel Sort out to-dos in SpillDispatcher
 * 215986          13/07/04 pradine  Split the Persistable interface
 * 223636.2        26/08/04 corrigk  Consolidate dump
 * 223996.1        01/09/04 pradine  Remove tick count and max depth
 * 229746          07/09/04 pradine  Display state during trace
 * 229942          08/09/04 pradine  Remove obsolete code
 * 247513          14/01/05 schofiel Improve spilling performance
 * 272110          10/05/05 schofiel 602:SVT: Malformed messages bring down the AppServer
 * SIB0002.ms.1    28/07/05 schofiel Changes for remote MQ subordinate resources
 * SIB0112b.ms.1   07/08/06 gareth   Large message support.
 * SIB0112d.ms.2   28/06/07 gareth   MemMgmt: SpillDispatcher improvements - datastore
 * 463642          04/09/07 gareth   Revert to using spill limits
 * 496154          22/04/07 gareth   Improve spilling performance
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * 538096          23/07/08 susana   Use getInMemorySize
 * F1332-51592     28/09/11 vmadhuka Persist redelivery count to FILESTORE
 * ============================================================================
 */

import java.io.IOException;
import java.util.List;

import com.ibm.ws.sib.msgstore.PersistentDataEncodingException;
import com.ibm.ws.sib.msgstore.SevereMessageStoreException;
import com.ibm.ws.sib.msgstore.cache.links.AbstractItemLink;
import com.ibm.ws.sib.msgstore.persistence.Persistable;
import com.ibm.ws.sib.msgstore.persistence.TupleTypeEnum;
import com.ibm.ws.sib.transactions.PersistentTranId;
import com.ibm.ws.sib.utils.DataSlice;
import com.ibm.ws.sib.utils.ras.FormattedWriter;

/**
 * The abstract base class for the cached data used in the Tasks.
 */
public abstract class CachedPersistableImpl implements Tuple
{
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private final Tuple _masterPersistable;

    public CachedPersistableImpl(Persistable masterPersistable)
    {
        _masterPersistable = (Tuple) masterPersistable;
    }

    public Persistable getPersistable()
    {
        return _masterPersistable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Persistable#persistableOperationBegun()
     */
    @Override
    public void persistableOperationBegun() throws SevereMessageStoreException
    {
        _masterPersistable.persistableOperationBegun();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Persistable#persistableOperationCompleted()
     */
    @Override
    public void persistableOperationCompleted() throws SevereMessageStoreException
    {
        _masterPersistable.persistableOperationCompleted();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Persistable#persistableOperationCancelled()
     */
    @Override
    public void persistableOperationCancelled() throws SevereMessageStoreException
    {
        _masterPersistable.persistableOperationCancelled();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Persistable#persistableRepresentationWasCreated()
     */
    @Override
    public boolean persistableRepresentationWasCreated()
    {
        return _masterPersistable.persistableRepresentationWasCreated();
    }

    // Defect 496154
    @Override
    public int persistableOperationsOutstanding()
    {
        return _masterPersistable.persistableOperationsOutstanding();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Persistable#requiresPersistence()
     */
    @Override
    public boolean requiresPersistence()
    {
        return _masterPersistable.requiresPersistence();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Persistable#setLogicallyDeleted(boolean)
     */
    @Override
    public void setLogicallyDeleted(boolean logicallyDeleted)
    {
        _masterPersistable.setLogicallyDeleted(logicallyDeleted);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Persistable#setPersistentTranId(com.ibm.ws.sib.msgstore.transactions.PersistentTranId)
     */
    @Override
    public void setPersistentTranId(PersistentTranId xid)
    {
        _masterPersistable.setPersistentTranId(xid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Persistable#setPermanentTableId(int)
     */
    @Override
    public void setPermanentTableId(int permanentTableId)
    {
        _masterPersistable.setPermanentTableId(permanentTableId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Persistable#getPermanentTableId()
     */
    @Override
    public int getPermanentTableId()
    {
        return _masterPersistable.getPermanentTableId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Persistable#setTemporaryTableId(int)
     */
    @Override
    public void setTemporaryTableId(int temporaryTableId)
    {
        _masterPersistable.setTemporaryTableId(temporaryTableId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Persistable#getTemporaryTableId()
     */
    @Override
    public int getTemporaryTableId()
    {
        return _masterPersistable.getTemporaryTableId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Persistable#setItemClassId(int)
     */
    @Override
    public void setItemClassId(int itemClassId)
    {
        _masterPersistable.setItemClassId(itemClassId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Persistable#getItemClassId()
     */
    @Override
    public int getItemClassId()
    {
        return _masterPersistable.getItemClassId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#setCanExpireSilently(boolean)
     */
    @Override
    public void setCanExpireSilently(boolean canExpireSilently)
    {
        _masterPersistable.setCanExpireSilently(canExpireSilently);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#getCanExpireSilently()
     */
    @Override
    public boolean getCanExpireSilently()
    {
        return _masterPersistable.getCanExpireSilently();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#setLockID(long)
     */
    @Override
    public void setLockID(long lockID)
    {
        _masterPersistable.setLockID(lockID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#getLockID()
     */
    @Override
    public long getLockID()
    {
        return _masterPersistable.getLockID();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#setItemClassName(java.lang.String)
     */
    @Override
    public void setItemClassName(String classname)
    {
        _masterPersistable.setItemClassName(classname);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#getItemClassName()
     */
    @Override
    public String getItemClassName()
    {
        return _masterPersistable.getItemClassName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#setAbstractItemLink(com.ibm.ws.sib.msgstore.cache.links.AbstractItemLink)
     */
    @Override
    public void setAbstractItemLink(AbstractItemLink link)
    {
        _masterPersistable.setAbstractItemLink(link);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#getData()
     */
    // Feature SIB0112b.ms.1
    @Override
    public List<DataSlice> getData() throws PersistentDataEncodingException, SevereMessageStoreException
    {
        return _masterPersistable.getData();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#setPersistentSize(int)
     */
    @Override
    public void setPersistentSize(int persistentDataSize)
    {
        _masterPersistable.setPersistentSize(persistentDataSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#getPersistentSize()
     */
    @Override
    public int getPersistentSize()
    {
        return _masterPersistable.getPersistentSize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Persistable#setInMemoryByteSize(int)
     */
    @Override
    public void setInMemoryByteSize(int byteSize)
    {
        _masterPersistable.setInMemoryByteSize(byteSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Persistable#getInMemoryByteSize()
     */
    @Override
    public int getInMemoryByteSize()
    {
        return _masterPersistable.getInMemoryByteSize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#getUniqueId()
     */
    @Override
    public long getUniqueId()
    {
        return _masterPersistable.getUniqueId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#getContainingStreamId()
     */
    @Override
    public long getContainingStreamId()
    {
        return _masterPersistable.getContainingStreamId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#setPriority(int)
     */
    @Override
    public void setPriority(int priority)
    {
        _masterPersistable.setPriority(priority);
    }

    @Override
    public void setRedeliveredCount(int redeliveredCount)
    {
        _masterPersistable.setRedeliveredCount(redeliveredCount);
    }

    @Override
    public int getRedeliveredCount()
    {
        return _masterPersistable.getRedeliveredCount();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#getPriority()
     */
    @Override
    public int getPriority()
    {
        return _masterPersistable.getPriority();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#setSequence(long)
     */
    @Override
    public void setSequence(long sequence)
    {
        _masterPersistable.setSequence(sequence);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#getSequence()
     */
    @Override
    public long getSequence()
    {
        return _masterPersistable.getSequence();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#setReferredID(long)
     */
    @Override
    public void setReferredID(long referredID)
    {
        _masterPersistable.setReferredID(referredID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#getReferredID()
     */
    @Override
    public long getReferredID()
    {
        return _masterPersistable.getReferredID();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#setStorageStrategy(int)
     */
    @Override
    public void setStorageStrategy(int storageStrategy)
    {
        _masterPersistable.setStorageStrategy(storageStrategy);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#getStorageStrategy()
     */
    @Override
    public int getStorageStrategy()
    {
        return _masterPersistable.getStorageStrategy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#getTupleType()
     */
    @Override
    public TupleTypeEnum getTupleType()
    {
        return _masterPersistable.getTupleType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#setExpiryTime(long)
     */
    @Override
    public void setExpiryTime(long expiryTime)
    {
        _masterPersistable.setExpiryTime(expiryTime);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#getExpiryTime()
     */
    @Override
    public long getExpiryTime()
    {
        return _masterPersistable.getExpiryTime();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Persistable#setDeliveryDelayTime(long)
     */
    @Override
    public void setDeliveryDelayTime(long deliveryDelayTime) {
        _masterPersistable.setDeliveryDelayTime(deliveryDelayTime);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Persistable#getDeliveryDelayTime()
     */
    @Override
    public long getDeliveryDelayTime() {
        return _masterPersistable.getDeliveryDelayTime();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#getPersistentTranId()
     */
    @Override
    public PersistentTranId getPersistentTranId()
    {
        return _masterPersistable.getPersistentTranId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#isLogicallyDeleted()
     */
    @Override
    public boolean isLogicallyDeleted()
    {
        return _masterPersistable.isLogicallyDeleted();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#createPersistable(long, com.ibm.ws.sib.msgstore.persistence.TupleTypeEnum)
     */
    @Override
    public Persistable createPersistable(long uniqueID, TupleTypeEnum tupleType)
    {
        return _masterPersistable.createPersistable(uniqueID, tupleType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#setContainingStream(com.ibm.ws.sib.msgstore.persistence.Persistable)
     */
    @Override
    public void setContainingStream(Persistable containingStream)
    {
        _masterPersistable.setContainingStream(containingStream);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#getContainingStream()
     */
    @Override
    public Persistable getContainingStream()
    {
        return _masterPersistable.getContainingStream();
    }

    // Defect 463642 
    // Revert to using spill limits previously removed in SIB0112d.ms.2
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#setWasSpillingAtAddition(boolean)
     */
    @Override
    public void setWasSpillingAtAddition(boolean wasSpilling)
    {
        _masterPersistable.setWasSpillingAtAddition(wasSpilling);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#xmlWrite(com.ibm.ws.sib.msgstore.FormattedWriter)
     */
    @Override
    public void xmlWrite(FormattedWriter writer) throws IOException
    {
        _masterPersistable.xmlWrite(writer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        //No trace neccessary
        return "uniqueId: " + getUniqueId()
               + ", containingStreamId: " + getContainingStreamId()
               + ", className: " + getItemClassName()
               + ", classId: " + getItemClassId()
               + ", dataSize: " + getPersistentSize()
               + ", permanentTableId: " + getPermanentTableId()
               + ", temporaryTableId: " + getTemporaryTableId()
               + ", storageStrategy: " + getStorageStrategy()
               + ", tupleType: " + getTupleType()
               + ", priority: " + getPriority()
               + ", sequence: " + getSequence()
               + ", canExpireSilently: " + getCanExpireSilently()
               + ", lockId: " + getLockID()
               + ", referredId: " + getReferredID()
               + ", expiryTime: " + getExpiryTime()
               + ", logicallyDeleted: " + isLogicallyDeleted()
               + ", xid: " + getPersistentTranId()
               + ", deliveryDelayTime: " + getDeliveryDelayTime()
               + LINE_SEPARATOR;
    }
}
