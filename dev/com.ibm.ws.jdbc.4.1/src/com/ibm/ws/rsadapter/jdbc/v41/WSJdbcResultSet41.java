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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.rsadapter.jdbc.WSJdbcObject;
import com.ibm.ws.rsadapter.jdbc.WSJdbcResultSet;
import com.ibm.ws.rsadapter.jdbc.WSJdbcUtil;

public class WSJdbcResultSet41 extends WSJdbcResultSet {

    public WSJdbcResultSet41(ResultSet rsImpl, WSJdbcObject parent) {
        super(rsImpl, parent);
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        try {
            T result = rsetImpl.getObject(columnIndex, type);
            addFreedResources(result);
            return result;
        } catch (IncompatibleClassChangeError e) {
            // If the JDBC driver was compiled with java 6
            throw new SQLFeatureNotSupportedException();
        } catch (SQLException ex) {
            FFDCFilter.processException(ex, "com.ibm.ws.rsadapter.jdbc.WSJdbcResultSet41.getObject", "39", this);
            throw WSJdbcUtil.mapException(this, ex);
        } catch (NullPointerException nullX) {
            // No FFDC code needed; we might be closed.
            throw runtimeXIfNotClosed(nullX);
        }
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        try {
            T result = rsetImpl.getObject(columnLabel, type);
            addFreedResources(result);
            return result;
        } catch (IncompatibleClassChangeError e) {
            // If the JDBC driver was compiled with java 6
            throw new SQLFeatureNotSupportedException();
        } catch (SQLException ex) {
            FFDCFilter.processException(ex, "com.ibm.ws.rsadapter.jdbc.WSJdbcResultSet41.getObject", "57", this);
            throw WSJdbcUtil.mapException(this, ex);
        } catch (NullPointerException nullX) {
            // No FFDC code needed; we might be closed.
            throw runtimeXIfNotClosed(nullX);
        }
    }
}