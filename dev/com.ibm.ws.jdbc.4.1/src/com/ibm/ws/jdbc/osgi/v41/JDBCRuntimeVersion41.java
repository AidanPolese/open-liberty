/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.jdbc.osgi.v41;

import java.sql.BatchUpdateException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.concurrent.Executor;

import org.osgi.framework.Version;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.jdbc.osgi.JDBCRuntimeVersion;

@Trivial
public class JDBCRuntimeVersion41 implements JDBCRuntimeVersion {
    @Override
    public Version getVersion() {
        return VERSION_4_1;
    }

    @Override
    public void doSetSchema(Connection sqlConn, String schema) throws SQLException {
        try {
            sqlConn.setSchema(schema);
        } catch (IncompatibleClassChangeError e) { // pre-4.1 driver
            throw new SQLFeatureNotSupportedException(e);
        }
    }

    @Override
    public String doGetSchema(Connection sqlConn) throws SQLException {
        try {
            return sqlConn.getSchema();
        } catch (IncompatibleClassChangeError e) { // pre-4.1 driver
            throw new SQLFeatureNotSupportedException(e);
        }
    }

    @Override
    public void doAbort(Connection sqlConn, Executor ex) throws SQLException {
        try {
            sqlConn.abort(ex);
        } catch (IncompatibleClassChangeError e) { // pre-4.1 driver
            throw new SQLFeatureNotSupportedException(e);
        }
    }

    @Override
    public void doSetNetworkTimeout(Connection sqlConn, Executor ex, int millis) throws SQLException {
        try {
            sqlConn.setNetworkTimeout(ex, millis);
        } catch (IncompatibleClassChangeError e) { // pre-4.1 driver
            throw new SQLFeatureNotSupportedException(e);
        }
    }

    @Override
    public int doGetNetworkTimeout(Connection sqlConn) throws SQLException {
        try {
            return sqlConn.getNetworkTimeout();
        } catch (IncompatibleClassChangeError e) { // pre-4.1 driver
            throw new SQLFeatureNotSupportedException(e);
        }
    }

    @Override
    public ResultSet doGetPseudoColumns(DatabaseMetaData md, String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        try {
            return md.getPseudoColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
        } catch (IncompatibleClassChangeError e) { // pre-4.1 driver
            throw new SQLFeatureNotSupportedException(e);
        }
    }

    @Override
    public boolean doGeneratedKeyAlwaysReturned(DatabaseMetaData md) throws SQLException {
        try {
            return md.generatedKeyAlwaysReturned();
        } catch (IncompatibleClassChangeError e) { // pre-4.1 driver
            throw new SQLFeatureNotSupportedException(e);
        }
    }

    @Override
    public <T> T doGetObject(CallableStatement cstmt, int parameterIndex, Class<T> type) throws SQLException {
        try {
            return cstmt.getObject(parameterIndex, type);
        } catch (IncompatibleClassChangeError e) { // pre-4.1 driver
            throw new SQLFeatureNotSupportedException(e);
        }
    }

    @Override
    public <T> T doGetObject(CallableStatement cstmt, String parameterName, Class<T> type) throws SQLException {
        try {
            return cstmt.getObject(parameterName, type);
        } catch (IncompatibleClassChangeError e) { // pre-4.1 driver
            throw new SQLFeatureNotSupportedException(e);
        }
    }

    @Override
    public <T> T doGetObject(ResultSet rs, int columnIndex, Class<T> type) throws SQLException {
        try {
            return rs.getObject(columnIndex, type);
        } catch (IncompatibleClassChangeError e) { // pre-4.1 driver
            throw new SQLFeatureNotSupportedException(e);
        }
    }

    @Override
    public <T> T doGetObject(ResultSet rs, String columnLabel, Class<T> type) throws SQLException {
        try {
            return rs.getObject(columnLabel, type);
        } catch (IncompatibleClassChangeError e) { // pre-4.1 driver
            throw new SQLFeatureNotSupportedException(e);
        }
    }

    @Override
    public long getLargeUpdateCount(Statement stmt) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setLargeMaxRows(Statement stmt, long max) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public long getLargeMaxRows(Statement stmt) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public long[] executeLargeBatch(Statement stmt) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public long executeLargeUpdate(Statement stmt, String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public long executeLargeUpdate(Statement stmt, String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public long executeLargeUpdate(Statement stmt, String sql, int[] columnIndexes) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public long executeLargeUpdate(Statement stmt, String sql, String[] columnNames) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean supportsRefCursors(DatabaseMetaData md) throws SQLException {
        return false; // matches default implementation
    }

    @Override
    public long getMaxLogicalLobSize(DatabaseMetaData md) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public BatchUpdateException newBatchUpdateException(BatchUpdateException copyFrom, String newMessage) {
        return new BatchUpdateException(newMessage, copyFrom.getSQLState(), copyFrom.getErrorCode(), copyFrom.getUpdateCounts());
    }

    @Override
    public void registerOutputParameter(CallableStatement cstmt, int parameterIndex, Object sqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void registerOutputParameter(CallableStatement cstmt, int parameterIndex, Object sqlType, int scale) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void registerOutputParameter(CallableStatement cstmt, int parameterIndex, Object sqlType, String typeName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void registerOutputParameter(CallableStatement cstmt, String parameterName, Object sqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void registerOutputParameter(CallableStatement cstmt, String parameterName, Object sqlType, int scale) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void registerOutputParameter(CallableStatement cstmt, String parameterName, Object sqlType, String typeName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setObject(CallableStatement cstmt, String parameterName, Object x, Object targetSqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setObject(CallableStatement cstmt, String parameterName, Object x, Object tragetSqlType, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public long executeLargeUpdate(PreparedStatement pstmt) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setObject(PreparedStatement pstmt, int parameterIndex, Object x, Object targetSqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public long setObject(PreparedStatement pstmt, int parameterIndex, Object x, Object targetSqlType, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void updateObject(ResultSet rs, int columnIndex, Object x, Object targetSqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void updateObject(ResultSet rs, int columnIndex, Object x, Object targetSqlType, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void updateObject(ResultSet rs, String columnLabel, Object x, Object targetSqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void updateObject(ResultSet rs, String columnLabel, Object x, Object targetSqlType, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
