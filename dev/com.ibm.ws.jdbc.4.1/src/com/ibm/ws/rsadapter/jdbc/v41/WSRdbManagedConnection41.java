/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.rsadapter.jdbc.v41;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.Executor;

import javax.resource.ResourceException;
import javax.security.auth.Subject;
import javax.sql.PooledConnection;
import javax.transaction.Transaction;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.rsadapter.AdapterUtil;
import com.ibm.ws.rsadapter.spi.WSConnectionRequestInfoImpl;
import com.ibm.ws.rsadapter.spi.WSManagedConnectionFactoryImpl;
import com.ibm.ws.rsadapter.spi.WSRdbManagedConnectionImpl;

public class WSRdbManagedConnection41 extends WSRdbManagedConnectionImpl {

    private static final TraceComponent tc = Tr.register(WSRdbManagedConnection41.class, AdapterUtil.TRACE_GROUP, AdapterUtil.NLS_FILE);

    private boolean aborted;

    public WSRdbManagedConnection41(WSManagedConnectionFactoryImpl mcf1,
                                    PooledConnection poolConn1,
                                    Connection conn,
                                    Subject sub,
                                    WSConnectionRequestInfoImpl cxRequestInfo) throws ResourceException {
        super(mcf1, poolConn1, conn, sub, cxRequestInfo);
    }

    @Override
    public String getSchema() throws SQLException {
        Transaction suspendTx = null;
        // Global trans must be suspended for jdbc-4.1 getters and setters on zOS
        if (AdapterUtil.isZOS() && isGlobalTransactionActive())
            suspendTx = suspendGlobalTran();

        String schema;
        try {
            schema = sqlConn.getSchema();
        } catch (IncompatibleClassChangeError e) {
            // If the JDBC driver was compiled with java 6
            throw new SQLFeatureNotSupportedException();
        } catch (SQLException sqle) {
            throw sqle;
        } finally {
            if (suspendTx != null)
                resumeGlobalTran(suspendTx);
        }
        return schema;
    }

    /**
     * Set a schema for this managed connection.
     *
     * @param schema The schema to set on the connection.
     */
    @Override
    public void setSchema(String schema) throws SQLException {
        Transaction suspendTx = null;

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(this, tc, "Set Schema to " + schema);
        // Global trans must be suspended for jdbc-4.1 getters and setters on zOS
        if (AdapterUtil.isZOS() && isGlobalTransactionActive())
            suspendTx = suspendGlobalTran();

        try {
            sqlConn.setSchema(schema);
            currentSchema = schema;
            connectionPropertyChanged = true;
        } catch (IncompatibleClassChangeError e) {
            // If the JDBC driver was compiled with java 6
            throw new SQLFeatureNotSupportedException();
        } catch (SQLException sqle) {
            throw sqle;
        } finally {
            if (suspendTx != null)
                resumeGlobalTran(suspendTx);
        }
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        int timeOut;
        Transaction suspendTx = null;
        // Global trans must be suspended for jdbc-4.1 getters and setters on zOS
        if (AdapterUtil.isZOS() && isGlobalTransactionActive())
            suspendTx = suspendGlobalTran();
        try {
            timeOut = sqlConn.getNetworkTimeout();
        } catch (IncompatibleClassChangeError e) {
            // If the JDBC driver was compiled with java 6
            throw new SQLFeatureNotSupportedException();
        } catch (SQLException sqle) {
            throw sqle;
        } finally {
            if (suspendTx != null)
                resumeGlobalTran(suspendTx);
        }
        return timeOut;
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(this, tc, "Set NetworkTimeout to " + milliseconds);

        Transaction suspendTx = null;
        // Global trans must be suspended for jdbc-4.1 getters and setters on zOS
        if (AdapterUtil.isZOS() && isGlobalTransactionActive())
            suspendTx = suspendGlobalTran();
        try {
            sqlConn.setNetworkTimeout(executor, milliseconds);
        } catch (IncompatibleClassChangeError e) {
            // If the JDBC driver was compiled with java 6
            throw new SQLFeatureNotSupportedException();
        } catch (SQLException sqle) {
            throw sqle;
        } finally {
            if (suspendTx != null)
                resumeGlobalTran(suspendTx);
        }
        currentNetworkTimeout = milliseconds;
        connectionPropertyChanged = true;
    }

    @Override
    public void abort(Executor ex) throws SQLFeatureNotSupportedException {
        try {
            setAborted(true);
            sqlConn.abort(ex);
        } catch (IncompatibleClassChangeError e) {
            // If the JDBC driver was compiled with java 6
            // avoid raising an error so connection management code can continue its own abort processing
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(this, tc, "abort failure", e);
        } catch (SQLException e) {
            // avoid raising an error so connection management code can continue its own abort processing
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(this, tc, "abort failure", e);
        }
    }

    @Override
    public boolean isAborted() {
        return aborted;
    }

    @Override
    public void setAborted(boolean aborted) throws SQLFeatureNotSupportedException {
        this.aborted = aborted;
    }
}
