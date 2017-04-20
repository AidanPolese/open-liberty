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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.rsadapter.jdbc.WSJdbcConnection;
import com.ibm.ws.rsadapter.jdbc.WSJdbcDatabaseMetaData;
import com.ibm.ws.rsadapter.jdbc.WSJdbcUtil;

public class WSJdbcDatabaseMetaData41 extends WSJdbcDatabaseMetaData {

    public WSJdbcDatabaseMetaData41(DatabaseMetaData metaDataImpl, WSJdbcConnection connWrapper) throws SQLException {
        super(metaDataImpl, connWrapper);
    }

    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        try {
            return mDataImpl.generatedKeyAlwaysReturned();
        } catch (IncompatibleClassChangeError e) {
            // If the JDBC driver was compiled with java 6
            throw new SQLFeatureNotSupportedException();
        } catch (SQLException ex) {
            FFDCFilter.processException(ex, "com.ibm.ws.rsadapter.jdbc.WSJdbcDatabaseMetaData41.generatedKeyAlwaysReturned", "38", this);
            throw WSJdbcUtil.mapException(this, ex);
        } catch (NullPointerException nullX) {
            // No FFDC code needed; we might be closed.
            throw runtimeXIfNotClosed(nullX);
        }
    }

    @Override
    public ResultSet getPseudoColumns(String catalog, String schemaPattern,
                                      String tableNamePattern, String columnNamePattern) throws SQLException {
        ResultSet rset;
        try {
            rset = mDataImpl.getPseudoColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
        } catch (IncompatibleClassChangeError e) {
            // If the JDBC driver was compiled with java 6
            throw new SQLFeatureNotSupportedException();
        } catch (SQLException ex) {
            FFDCFilter.processException(ex, "com.ibm.ws.rsadapter.jdbc.WSJdbcDatabaseMetaData.getPseudoColumns", "56", this);
            throw WSJdbcUtil.mapException(this, ex);
        } catch (NullPointerException nullX) {
            // No FFDC code needed; we might be closed.
            throw runtimeXIfNotClosed(nullX);
        }
        rset = ((WSJdbcConnection) parentWrapper).createResultSetWrapper(rset, this);
        childWrappers.add(rset);
        return rset;
    }
}
