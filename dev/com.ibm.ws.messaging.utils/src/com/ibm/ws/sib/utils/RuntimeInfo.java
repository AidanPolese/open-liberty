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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * LIDB2117        031124 vaughton Original
 * LIDB3418-56     210405 nottinga Updated for componentization
 * 328282          051213 vaughton sib.config.properties
 * 341625          060126 djvines  Resolve unused imports
 * SIB0048b.uti.1  060922 mattheg  Add isFatClient() / reimplement isThinClient()
 * 400794          061129 mnuttall Prevent static block throwing NoClassDefFoundError
 *                                 when _isThinClient. Fix (c) block. Add _underscores
 *                                 to class member variables.
 * PK58698         080102 vaughton Fix loading of sib.properties file
 * 493023          080118 sibcopyr Automatic update of trace guards
 * PK60008         170408 pbroad   Provide access to the default thread pool
 * 516215          280408 sibcopyr Automatic update of trace guards
 * 516268          280408 vaughton Suppress msg when property from sib.config.properties
 * 624993          180810 venugopv Fixing probable leak of InputStream in getProperty()
 * 71830           290712 kavitha  Code modified for Liberty
 * 91142           160413 urwashi  Remove getDefaultServerThreadpool method
 * ============================================================================
 */

package com.ibm.ws.sib.utils;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.kernel.service.util.PrivHelper;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * This class can be used to discover information about the runtime environment
 * in which the caller is currently running.
 */

public final class RuntimeInfo {

    public final static String SIB_PROPERTY_SEPARATOR = ".";
    public final static String SIB_PROPERTY_PREFIX = "sib" + SIB_PROPERTY_SEPARATOR;

    private static final TraceComponent tc = SibTr.register(RuntimeInfo.class, UtConstants.MSG_GROUP, UtConstants.MSG_BUNDLE);

    private static boolean _isClustered;
    private static boolean _isServer;
    private static boolean _isClientContainer;
    private static boolean _isFatClient;
    private static boolean _isThinClient;

    static {
        boolean thinClientPropertySet = false;
        try {
            _isClustered = false;
            _isServer = true;
        } catch (Exception e) { // Ignore any exception because we could be in a client
            // No FFDC code needed
            SibTr.exception(tc, e);
        } catch (NoClassDefFoundError e) { // We may get this in thin clients!
            // No FFDC code needed
            if (!thinClientPropertySet) { // 400794: Don't write this exception out if we're in the thin client
                SibTr.exception(tc, e);
            }
        }

        if (!_isClustered && !_isServer) {
            if ("client".equals(PrivHelper.getProperty("com.ibm.ws.container"))) {
                _isClientContainer = true;
            } else if (thinClientPropertySet) {
                _isThinClient = true;
            } else {
                _isFatClient = true;
            }
        }

    }

    /**
     * Is the current process a clustered server
     * 
     * @return boolean
     */

    public static boolean isClusteredServer() {
        return _isClustered;
    }

    /**
     * Is the current process a non-clustered server
     * 
     * @return boolean
     */

    public static boolean isServer() {
        return _isServer;
    }

    /**
     * Is the current process a client container
     * 
     * @return boolean
     */

    public static boolean isClientContainer() {
        return _isClientContainer;
    }

    /**
     * This method returns true if the current process is a client that is running outside
     * of the client container but with the full WAS libraries (either the client libraries
     * or the server libraries).
     * 
     * @return boolean
     */

    public static boolean isFatClient() {
        return _isFatClient;
    }

    /**
     * This method returns true if the current process is a client that is running with the
     * 'Portly' client libraries (i.e. the cutdown JMS client libraries only).
     * 
     * @return boolean
     */

    public static boolean isThinClient() {
        return _isThinClient;
    }

    /*
     * The following section is code that allows callers to retrieve information
     * from the sib.properties file
     */

    /**
     * Retrieves the named property from the sib.properties file or if not found a
     * System property.
     * 
     * @param property The non-null name of the property.
     * 
     * @return The string value of found property otherwise null.
     */

    public static String getProperty(String property) {
        // We have to get the property from Configuration (server.xml) in Liberty, we need to implement this
        return null;
    }

    /**
     * Retrieves the named property by searching sib.config.properties then if not found
     * sib.properties or if not found a System property. If no property value is found
     * then the default value is returned. 328282
     * 
     * @param property The non-null name of the property.
     * @param defval The possibly null default value to return if property does not exist.
     * 
     * @return The string value of found property otherwise defval.
     */

    public static String getProperty(String property, String defval) {
        // We have to get the property from Configuration (server.xml) in Liberty, we need to implement this  
        return defval;
    }

    /**
     * Retrieve the named property using getProperty(String property, String defval) and if the
     * returned value is different to the default value call Runtime.changedPropertyValue.
     * 
     * @param property The non-null name of the property.
     * @param defval The possibly null default value to return if property does not exist.
     * 
     * @return The string value of found property otherwise defval.
     */

    public static String getPropertyWithMsg(String property, String defval) {
        return defval;
    }

    /**
     * Returns true if we are running on 64bit java
     * 
     * @return boolean
     */
    public static boolean is64bit() {
        String bitsize = PrivHelper.getProperty("sun.arch.data.model", "32");
        boolean is64bit = bitsize.equals("64");
        return is64bit;
    }

    //Venu mock mock
    //For now return false. However further investigation has to be done to set the correct value
    public static boolean isCRAJvm() {
        return true;
        //return  true;
    }
}
