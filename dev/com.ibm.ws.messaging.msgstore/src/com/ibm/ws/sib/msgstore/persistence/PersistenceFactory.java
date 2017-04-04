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
 *                 09/08/03 kschloss Original
 * 180763.2        07/10/03 pradine  Refactor MS configuration code
 * 190379          12/04/04 pradine  Tighten up stopping behaviour
 * 206674          02/06/04 schofiel Enable pluggable persistence layer in Message Store
 * 205363          28/07/04 pradine  Redesign unique key generators
 * 251161          13/04/05 gareth   Add ObjectManager code to CMVC
 * SIB0003.ms.14   18/08/05 schofiel File store - admin integration
 * 321394          07/11/05 schofiel Remove unused imports in MS
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * ============================================================================
 */

import com.ibm.ws.sib.msgstore.Configuration;
import com.ibm.ws.sib.msgstore.MessageStoreConstants;
import com.ibm.ws.sib.msgstore.SevereMessageStoreException;
import com.ibm.ws.sib.msgstore.impl.MessageStoreImpl;
import com.ibm.ws.sib.msgstore.transactions.impl.XidManager;

public class PersistenceFactory 
{
    private PersistenceFactory() {}
    
    /**
     * Factory method for {@link PersistentMessageStore} objects.
     * 
     * @param msi the {@link MessageStoreImpl} object
     * @param xidManager the {@link XidManager} object
     * @param configuration the {@link Configuration} object
     * @return a {@link PersistentMessageStore} object
     * @throws SevereMessageStoreException 
     */
    public static PersistentMessageStore getPersistentMessageStore(MessageStoreImpl msi, XidManager xidManager, Configuration configuration) throws SevereMessageStoreException
    {
        // Override configuration from property if present
        String pmsImplClassName = msi.getProperty(MessageStoreConstants.PROP_PERSISTENT_MESSAGE_STORE_CLASS,
                                                  configuration.getPersistentMessageStoreClassname());

        // No value for the implementation class yet? Use the default
        if (pmsImplClassName == null)
        {
            pmsImplClassName = MessageStoreConstants.PROP_PERSISTENT_MESSAGE_STORE_CLASS_DEFAULT;
        }

        PersistentMessageStore persistentMessageStore = null;
        try
        {
            Class clazz = Class.forName(pmsImplClassName);
            persistentMessageStore = (PersistentMessageStore)clazz.newInstance();
            persistentMessageStore.initialize(msi, xidManager, configuration);
        }
        catch (ClassNotFoundException e)
        {
            com.ibm.ws.ffdc.FFDCFilter.processException(e, "com.ibm.ws.sib.msgstore.persistence.impl.PersistenceFactory.getPersistentMessageStore", "1:72:1.15.1.1");
            throw new SevereMessageStoreException(e);
        }
        catch (InstantiationException e)
        {
            com.ibm.ws.ffdc.FFDCFilter.processException(e, "com.ibm.ws.sib.msgstore.persistence.impl.PersistenceFactory.getPersistentMessageStore", "1:77:1.15.1.1");
            throw new SevereMessageStoreException(e);
        }
        catch (IllegalAccessException e)
        {
            com.ibm.ws.ffdc.FFDCFilter.processException(e, "com.ibm.ws.sib.msgstore.persistence.impl.PersistenceFactory.getPersistentMessageStore", "1:82:1.15.1.1");
            throw new SevereMessageStoreException(e);
        }
        
        return persistentMessageStore;
    }
}
