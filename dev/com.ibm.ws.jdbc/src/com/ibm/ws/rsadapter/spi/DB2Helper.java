/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2003,2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.rsadapter.spi;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLInvalidAuthorizationSpecException; 
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.ibm.ejs.cm.logger.TraceWriter;

import javax.resource.ResourceException;
import javax.transaction.Transaction;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.Transaction.UOWCoordinator;
import com.ibm.ws.Transaction.UOWCurrent;
import com.ibm.ws.resource.ResourceRefInfo;
import com.ibm.ws.rsadapter.AdapterUtil;
import com.ibm.ws.rsadapter.DSConfig;
import com.ibm.ws.tx.embeddable.EmbeddableWebSphereTransactionManager;

/**
 * Helper class for common DB2 behavior. Do not instantiate this class directly.
 * Always use one of the subclasses which are specific to the JDBC driver that connects to DB2.
 */
public class DB2Helper extends DatabaseHelper {
    private static TraceComponent tc = Tr.register(DB2Helper.class, AdapterUtil.TRACE_GROUP, AdapterUtil.NLS_FILE);
    private static final String ZOS_CURRENT_SQLID = "currentSQLID";

    @SuppressWarnings("deprecation")
    protected static final TraceComponent db2Tc = com.ibm.ejs.ras.Tr.register("com.ibm.ws.db2.logwriter", "WAS.database", null);

    HashMap<Object, Class<?>> db2ErrorMap = new HashMap<Object, Class<?>>(37); // this number should change when adding new values

    private String currentSQLid = null;
    String osType;
    boolean isRRSTransaction = false;
    String threadIdentitySupport = THREAD_IDENTITY_SUPPORT_NOTALLOWED;
    boolean threadSecurity = false;
    boolean localZOS = false;
    private static Field mTransactionStateField = null; 
    String productName = null;
    // : the connection type. 1 = jdbc driver; 2 = jcc driver
    static int JDBC = 1; 
    static int SQLJ = 2; 
    int connType = 0; 
    private transient PrintWriter db2Pw; 

    /**
     * Construct a helper class for common DB2 behavior. Do not instantiate this class directly.
     * Always use one of the subclasses which are specific to the JDBC driver that connects to DB2.
     * 
     * @param mcf managed connection factory
     */
    DB2Helper(WSManagedConnectionFactoryImpl mcf) throws Exception {
        super(mcf);

        mcf.supportsGetTypeMap = false;

        Properties props = mcf.dsConfig.get().vendorProps;
        currentSQLid = (String) props.get(ZOS_CURRENT_SQLID);
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(this, tc, "ZOS_CURRENT_SQLID is", currentSQLid);

        if (AdapterUtil.isZOS()) 
            localZOS = true;
        // flag. therefore, it is saved to use this flag to indicate that this is a zOS database.
        if (localZOS) {
            isRRSTransaction = true;
            threadIdentitySupport = THREAD_IDENTITY_SUPPORT_ALLOWED;
            threadSecurity = true;
        }

        Collections.addAll(staleErrorCodes,
                           -30108,
                           -30081
                           -30080,
                           -6036,
                           -1229,
                           -1224,
                           -1035,
                           -1034,
                           -1015,
                           -924,
                           -923,
                           -906);

        staleSQLStates.add("58004");
        staleSQLStates.remove("S1000"); // DB2 sometimes uses this SQL state for non-stale. In those cases, rely on error code to detect.
    }

    /**
     * <p>This method configures a connection before first use. This method is invoked only when a new
     * connection to the database is created. It is not invoked when connections are reused
     * from the connection pool.</p>
     * <p> This class will set a variable db2ZOS to <code>FALSE<code> as default value. This method
     * sets to <code>TRUE<code> if the backend system is zOS.
     * 
     * @param conn the connection to set up.
     * @exception SQLException if connection setup cannot be completed successfully.
     */
    @Override
    public void doConnectionSetup(Connection conn) throws SQLException {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();

        if (isTraceOn && tc.isEntryEnabled()) 
            Tr.entry(this, tc, "doConnectionSetup");

        //set the currrentSQLid on the connection if its not null
        Statement stmt = null;
        Transaction suspendedTx = null; 
        EmbeddableWebSphereTransactionManager tm = mcf.connectorSvc.getTransactionManager();

        try {
            if (currentSQLid != null && !currentSQLid.equals("")) 
            {
                // If the work below is happening under a global transaction scope, we must suspend the 
                // global transaction before we perform the action. That is because if we are to perform an
                // action that implies that a local transaction should on the thread, we need to make sure that
                // the action will take place under a local transaction scope. 
                // If we do not do this, others that are aware of the transaction type currently on
                // the thread (i.e. DB2 T2 jdbc driver) may react in a way that is inconsistent with
                // the actions and expectations below.
                UOWCurrent uow = (UOWCurrent) tm;
                UOWCoordinator coord = uow == null ? null : uow.getUOWCoord();
                boolean inGlobalTransaction = coord != null && coord.isGlobal();
                if (inGlobalTransaction) { 
                    try {
                        suspendedTx = tm.suspend();
                    } catch (Throwable t) {
                        throw new SQLException(t);
                    }
                }

                if (isTraceOn && tc.isDebugEnabled()) 
                {
                    Tr.debug(this, tc, "Setting currentSQLID : " 
                                       + currentSQLid);
                }
                stmt = conn.createStatement();
                String sql = "set current sqlid = '" + currentSQLid + "'";
                stmt.executeUpdate(sql);
            }

        } finally {
            // close the statement
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) { 
                com.ibm.ws.ffdc.FFDCFilter.processException(e, getClass().getName(), "231", this);
                if (isTraceOn && tc.isDebugEnabled()) 
                    Tr.debug(this, tc, "SQLException occured in closing the statement ", e);
            } finally { 
                // If there is a suspended transaction, resume it.
                if (suspendedTx != null) {
                    try {
                        tm.resume(suspendedTx);
                    } catch (Throwable t) {
                        throw new SQLException(t);
                    }
                }
            }
        }

        if (isTraceOn && tc.isEntryEnabled())
            Tr.exit(this, tc, "doConnectionSetup");
    }

    @Override
    public final boolean doesStatementCacheIsoLevel() {
        return true;
    }

    Object getDB2Object(Connection con) {
        throw new UnsupportedOperationException(); // should only be invoked if using DB2JCCHelper, which properly implements
    }

    @Override
    public int getDefaultIsolationLevel() {
        return Connection.TRANSACTION_REPEATABLE_READ;
    }

    /**
     * Feature WS14621 
     * 
     * @return an indicator of the DataSource is RRS-enabled.
     */
    @Override
    public Boolean getRRSTransactional() {
        return isRRSTransaction; 
    }

    /**
     * Feature WS14621 
     * 
     * @return string to indicate whether the DataSource allows Thread Identity Support.
     */
    @Override
    public String getThreadIdentitySupport() {
        //return THREAD_IDENTITY_SUPPORT_ALLOWED;
        return threadIdentitySupport; 
    }

    /**
     * Feature WS14621 
     * 
     * @return an boolean to indicate whether the DataSource supports "synch to thread"
     *         for the allocateConnection, i.e., push an ACEE corresponding to the current
     *         java Subject on the native OS thread.
     */
    @Override
    public Boolean getThreadSecurity() {
        return threadSecurity; 
    }

    /**
     * @return NULL because DB2 provides sufficient trace of its own.
     */
    @Override
    public com.ibm.ejs.ras.TraceComponent getTracer() {
        return null;
    }

    @Override
    public PrintWriter getPrintWriter() throws ResourceException 
    {
        final boolean trace = TraceComponent.isAnyTracingEnabled();
        //not synchronizing here since there will be one helper
        // and most likely the setting will be serially, even if its not,
        // it shouldn't matter here (tracing).

        if (db2Pw == null) {
            db2Pw = new PrintWriter(new TraceWriter(db2Tc), true);
        }
        if (trace && tc.isDebugEnabled())
            Tr.debug(this, tc, "returning", db2Pw);
        return db2Pw;
    }

    /**
     * Determine if the top level exception is an authorization exception.
     * Chained exceptions are not checked.
     * 
     * Look for the JDBC 4.0 exception subclass
     * or the 28000 SQLState (Invalid authorization specification)
     * or a DB2 error code in (-1403, -4214, -30082)
     * or an exception message containing the text [2013]
     * 
     * @param x the exception to check.
     * @return true or false to indicate if the exception is an authorization error.
     * @throws NullPointerException if a NULL exception parameter is supplied.
     */
    boolean isAuthException(SQLException x) {
        int ec = x.getErrorCode();
        return x instanceof SQLInvalidAuthorizationSpecException
               || "28000".equals(x.getSQLState()) // Authorization name is invalid
               || -1403 == ec // The username and/or password supplied is incorrect.
               || -4214 == ec
               || -30082 == ec // CONNECTION FAILED FOR SECURITY REASON
               // [ibm][db2][jcc][t4][2013][11249] Connection authorization failure occurred.  Reason: User ID or Password invalid.
               || x.getMessage() != null && x.getMessage().indexOf("[2013]") > 0;
    }

    /**
     * @return true if the exception or a cause exception in the chain is known to indicate a stale statement. Otherwise false.
     */
    @Override
    public boolean isStaleStatement(SQLException x) {
        // check for cycles
        Set<Throwable> chain = new HashSet<Throwable>();

        for (Throwable t = x; t != null && chain.add(t); t = t.getCause())
            if (t instanceof SQLException) {
                SQLException sqlX = (SQLException) t;
                int ec = sqlX.getErrorCode();
                if (-514 == ec ||
                    -518 == ec)
                    return true;
            }
        return super.isStaleStatement(x);
    }

    @Override
    public void processLastHandleClosed(Connection conn, boolean autoCommit, boolean inGlobal) throws SQLException 
    { 
      // although not intuitive, it is possible for the DB2 for z/OS Legacy JDBC Driver to leave
      // its internal Connection on a Unit of Work boundary even when auto commit is true;
      // most application developers won't know this, so to save an application from experiencing an
      // LTC rollback when auto commit is true, we can detect here if the Connection is not on a
      // UOW boundary and reset it
        if (localZOS && autoCommit && !inGlobal) 
        { 
            resetUOWBoundary(conn); 
        } 
    } 

    @Override
    public void doConnectionCleanupOnWrapper(WSRdbManagedConnectionImpl mc) throws SQLException 
    { 
      // because DB2 for z/OS is RRS transactional, it is extremely important to
      // ensure that Connections being returned to the Free Pool are on a Unit of
      // Work boundary; otherwise, errors such as SQLException -925, or server
      // abends may occur when the Connection is later reused
        if (localZOS) 
        { 
            resetUOWBoundary(mc.sqlConn); 
        } 
    } 

    /**
     * This method uses proprietary logic to detect if the conn parameter is on a Unit of Work boundary,
     * and if not, the connection is rolled back in order to set it on a Unit of Work boundary.
     * 
     * @param conn the Connection that will be reset on a UOW boundary
     * 
     * @throws SQLException
     */

    private void resetUOWBoundary(Connection conn) throws SQLException 
    { 
        if (tc.isEntryEnabled()) 
        { 
            Tr.entry(this, tc, "resetUOWBoundary"); 
        } 
        // by default, always rollback to fulfull the implied condition
        // of the method name that we will reset this connection on a
        // unit of work boundary
        //
        // however, if there is driver specific logic available to
        // predetermine if we are on a Unit of Work boundary, then we
        // can disable the rollback for a performance improvement
        boolean rollback = true;
        if (localZOS && mcf.supportsUOWDetection) 
        { 
            try 
            { 
                Object db2Obj = getDB2Object(conn);
                if (mTransactionStateField == null) 
                { 
                    Class<?> db2ObjClass = db2Obj.getClass(); 
                    mTransactionStateField = db2ObjClass.getField("mTransactionState"); 
                } 
                int mTransactionStateValue = mTransactionStateField.getInt(db2Obj); 
                if (mTransactionStateValue == 0) 
                { 
                    rollback = false; 
                } 
            } 
            catch (Exception e) 
            { 
                // don't throw exception further; we can still function, but for
                // whatever reason the driver isn't supporting detection of the
                // unit of work boundary
                if (tc.isEventEnabled())
                    Tr.event(this, tc, "JDBC Driver does not support UOW detection because of: ", e); 
            } 
        } 

        if (rollback) 
        { 
            if (tc.isDebugEnabled()) 
            { 
                Tr.debug(this, tc, "Issuing rollback to reset UOW boundary"); 
            } 
            conn.rollback(); 
        } 
        else if (tc.isDebugEnabled()) 
        { 
            Tr.debug(this, tc, "Connection already on UOW boundary; skip rollback"); 
        } 
        if (tc.isEntryEnabled()) 
        { 
            Tr.exit(this, tc, "resetUOWBoundary"); 
        } 
    }

    @Override
    public boolean shouldTraceBeEnabled(WSManagedConnectionFactoryImpl mcf) {
        // here will base this on the mcf since, the value is enabled for the system
        // as a whole
        if (db2Tc.isDebugEnabled() && !mcf.loggingEnabled) {
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldTraceBeEnabled(WSRdbManagedConnectionImpl mc) {
        //the mc.mcf will be passed to the shouldTraceBeEnabled method that handles WSManagedConnectionFactoryImpl and 
        //get the boolean returned
        return (this.shouldTraceBeEnabled(mc.mcf));
    } 

    @Override
    public boolean shouldTraceBeDisabled(WSRdbManagedConnectionImpl mc) {
        if (!db2Tc.isDebugEnabled() && mc.mcf.loggingEnabled)
            return true;

        return false;
    }

    @Override
    public void gatherAndDisplayMetaDataInfo(Connection conn, WSManagedConnectionFactoryImpl mcf) throws SQLException 
    {
        super.gatherAndDisplayMetaDataInfo(conn, mcf);

        if (getDriverName().equalsIgnoreCase("DSNAJDBC")) //means DB2 legacy RRS is being used.
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) 
            {
                Tr.debug(this, tc, "application using <DB2 for zOS Local JDBC Provider (RRS)> which is not supported anymore in WAS6.1");
            }
            Tr.warning(tc, "PROVIDER_NOT_SUPPORTED",
                       "DB2 for zOS Local JDBC Provider (RRS)", "DB2 Universal JDBC Driver Provider Type 2");
            throw new SQLException(AdapterUtil.getNLSMessage("PROVIDER_NOT_SUPPORTED",
                                                             "DB2 for zOS Local JDBC Provider (RRS)", "DB2 Universal JDBC Driver Provider Type 2"));
        }

    }

    public int branchCouplingSupported(int couplingType) 
    {
        // Default is Loose for DB2 in general
        // Tight is not supported by the DB or JDBC driver 
        if (couplingType == ResourceRefInfo.BRANCH_COUPLING_TIGHT) {
            DSConfig config = super.mcf.dsConfig.get();
            Tr.warning(tc, "TBC_NOT_SUPPORTED", config.jndiName == null ? config.id : config.jndiName);
            return -1;
        }

        return javax.transaction.xa.XAResource.TMNOFLAGS;
    }

}