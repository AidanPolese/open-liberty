package com.ibm.ws.sib.msgstore.task;
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
 *                 26/06/03 drphill  Original
 * 180053          03/11/03 gareth   Remove deprecated methods/interfaces
 * 180763.5        21/11/03 pradine  Add support for new PersistenceManager Interface
 * 184259          01/12/03 pradine  Update XID with logical delete flag
 * 184391.1        21/01/04 pradine  DDL generation program
 * 180763.7        10/02/04 pradine  Add support for mutiple item tables 
 * 184390.1.2      18/02/04 schofiel Revised Reliability Qualities of Service - MS - Task changes
 * 188052          10/03/04 pradine  Changes to the garbage collector 
 * 225118          13/08/20 drphill  Move code from tasks to stub (simplification)
 * SIB0002.ms.1    28/07/05 schofiel Changes for remote MQ subordinate resources
 * 306998.20       09/01/06 gareth   Add new guard condition to trace statements
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * ============================================================================
 */

import com.ibm.ws.sib.msgstore.MessageStoreConstants;
import com.ibm.ws.sib.msgstore.SevereMessageStoreException;
import com.ibm.ws.sib.msgstore.cache.links.AbstractItemLink;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Perform a remove of an unlocked item
 */
public final class RemoveTask extends AbstractRemoveTask
{
    private static TraceComponent tc = SibTr.register(RemoveTask.class, 
                                                      MessageStoreConstants.MSG_GROUP, 
                                                      MessageStoreConstants.MSG_BUNDLE);

    public RemoveTask(final AbstractItemLink link) throws SevereMessageStoreException
    {
        super(link);
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
        {
            SibTr.entry(tc, "<init>", link);
            SibTr.exit(tc, "<init>", this);
        }
    }

    /*
     *  (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.task.Task#getTaskType()
     */
    public final Task.Type getTaskType()
    {
        return Type.REMOVE;
    }
}
