package com.ibm.ws.sib.msgstore;
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
 * Reason          Date     Origin     Description
 * --------------- -------- ---------- ----------------------------------------
 *                 13/04/03 kschloss   Original
 * 169421.1.1      30/09/03 pradine    Add support for new configuration model
 * 180763.2        07/10/03 pradine    Refactor MS configuration code
 * 191800          24/02/04 pradine    Add NLS support to the persistence layer
 * 190379          12/04/04 pradine    Tighten up stopping behaviour
 * 230745          10/09/04 pradine    Should not throw an exception during start
 * SIB0003.ms.14   18/08/05 schofiel   File store - admin integration
 * 306998.19       09/01/06 gareth     Add new guard condition to trace statements
 * ============================================================================
 */

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.msgstore.MessageStoreConstants;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Holds the configuration information needed by the Message Store.
 * 
 * @author kschloss
 * @author pradine
 */
public class WASConfiguration extends Configuration {
    private static TraceComponent tc = SibTr.register(WASConfiguration.class, MessageStoreConstants.MSG_GROUP, MessageStoreConstants.MSG_BUNDLE);
    
    protected String datasourceJndiName = "jdbc/DefaultDataSource";
    protected String authAlias;

    /**
     * Constructor
     *
     */
    protected WASConfiguration() {
        super();
        
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            SibTr.entry(tc, "<ctor>()");
            SibTr.exit(tc, "<ctor>()", this);
        }
    }

    /**
     * Create a new WASConfiguration object
     * 
     * @return a new WASConfiguration object
     */
    public static WASConfiguration getDefaultWasConfiguration() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "getDefaultWasConfiguration()");
            
        WASConfiguration config = new WASConfiguration();
        
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "getDefaultWasConfiguration()", config);

        return(config);
    }

    /**
     * Returns the jndi name of the data source that the message store will use.
     * 
     * @return the jndi name of the data source
     */
    public String getDatasourceJndiName() {
        //No trace required
        return datasourceJndiName;
    }

    /**
     * Sets the data source jndi name.
     * 
     * @param datasourceJndiName the data source jndi name
     */
    public void setDatasourceJndiName(String datasourceJndiName) {
        //No trace required
        this.datasourceJndiName = datasourceJndiName;
    }

    /**
     * Sets the J2C authentication alias that will be used to connect to the
     * data source.
     * 
     * @param authAlias the J2C authentication alias
     */
    public void setAuthenticationAlias(String authAlias) {
        //No trace required
        this.authAlias = authAlias; 
    }

    /**
     * Returns the J2C authentication alias.
     * 
     * @return the J2C authentication alias
     */
    public String getAuthenticationAlias() {
        //No trace required
        return authAlias;
    }
    
    public String toString() {
        return super.toString()
               + ", JNDI name: " + datasourceJndiName
               + ", Auth alias: " + authAlias;
    }
}
