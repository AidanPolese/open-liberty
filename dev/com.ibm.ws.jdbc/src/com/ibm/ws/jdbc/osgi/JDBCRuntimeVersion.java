/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jdbc.osgi;

import java.sql.BatchUpdateException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.resource.ResourceException;
import javax.security.auth.Subject;
import javax.sql.PooledConnection;

import org.osgi.framework.Version;

import com.ibm.ws.rsadapter.jdbc.WSJdbcCallableStatement;
import com.ibm.ws.rsadapter.jdbc.WSJdbcConnection;
import com.ibm.ws.rsadapter.jdbc.WSJdbcDatabaseMetaData;
import com.ibm.ws.rsadapter.jdbc.WSJdbcObject;
import com.ibm.ws.rsadapter.jdbc.WSJdbcPreparedStatement;
import com.ibm.ws.rsadapter.jdbc.WSJdbcResultSet;
import com.ibm.ws.rsadapter.jdbc.WSJdbcStatement;
import com.ibm.ws.rsadapter.spi.StatementCacheKey;
import com.ibm.ws.rsadapter.spi.WSConnectionRequestInfoImpl;
import com.ibm.ws.rsadapter.spi.WSManagedConnectionFactoryImpl;
import com.ibm.ws.rsadapter.spi.WSRdbManagedConnectionImpl;

/**
 * Interface used to proxy method calls to external packages which may require
 * a higher java version.
 */
public interface JDBCRuntimeVersion {

    public static final String VERSION = "version";
    public static final Version VERSION_4_0 = new Version(4, 0, 0);
    public static final Version VERSION_4_1 = new Version(4, 1, 0);
    public static final Version VERSION_4_2 = new Version(4, 2, 0);

    public Version getVersion();

    public WSJdbcConnection newConnection(WSRdbManagedConnectionImpl mc,
                                          Connection conn,
                                          Object key,
                                          Object currentThreadID);

    public WSRdbManagedConnectionImpl newManagedConnection(WSManagedConnectionFactoryImpl mcf1,
                                                           PooledConnection poolConn1,
                                                           Connection conn,
                                                           Subject sub,
                                                           WSConnectionRequestInfoImpl cxRequestInfo) throws ResourceException;

    public WSJdbcDatabaseMetaData newDatabaseMetaData(DatabaseMetaData metaDataImpl,
                                                      WSJdbcConnection connWrapper) throws SQLException;

    public WSJdbcStatement newStatement(Statement stmtImplObject, WSJdbcConnection connWrapper, int theHoldability);

    public WSJdbcPreparedStatement newPreparedStatement(PreparedStatement pstmtImplObject, WSJdbcConnection connWrapper,
                                                        int theHoldability, String pstmtSQL) throws SQLException;

    public WSJdbcPreparedStatement newPreparedStatement(PreparedStatement pstmtImplObject, WSJdbcConnection connWrapper,
                                                        int theHoldability, String pstmtSQL,
                                                        StatementCacheKey pstmtKey) throws SQLException;

    public WSJdbcCallableStatement newCallableStatement(CallableStatement cstmtImplObject, WSJdbcConnection connWrapper,
                                                        int theHoldability, String cstmtSQL) throws SQLException;

    public WSJdbcCallableStatement newCallableStatement(CallableStatement cstmtImplObject, WSJdbcConnection connWrapper,
                                                        int theHoldability, String cstmtSQL,
                                                        StatementCacheKey cstmtKey) throws SQLException;

    public WSJdbcResultSet newResultSet(ResultSet rsImpl, WSJdbcObject parent);

    public BatchUpdateException newBatchUpdateException(BatchUpdateException copyFrom, String newMessage);
}
