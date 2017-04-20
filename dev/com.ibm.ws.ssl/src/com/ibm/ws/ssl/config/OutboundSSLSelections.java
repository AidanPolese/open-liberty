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
package com.ibm.ws.ssl.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ssl.Constants;
import com.ibm.websphere.ssl.SSLConfig;
import com.ibm.ws.config.xml.internal.nester.Nester;

/**
 * OutboundSSLSelections
 * <p>
 * This class handles processing of dynamic outbound connectionInfo.
 * </p>
 *
 */
public class OutboundSSLSelections {
    private static final TraceComponent tc = Tr.register(OutboundSSLSelections.class, "SSL", "com.ibm.ws.ssl.resources.ssl");

    // used to hold SSL configurations and outbound connection info
    private final Map<String, String> dynamicHostPortSelections = new HashMap<String, String>();

    // used to hold SSL configurations and outbound connection info
    private final Map<String, String> dynamicHostSelections = new HashMap<String, String>();

    // used to hold SSL configurations and outbound connection info
    private final Map<String, String> dynamicSelections = new HashMap<String, String>();

    // used to cache lookups for future use.
    private final Map<Map<String, Object>, SSLConfig> dynamicLookupCache = new HashMap<Map<String, Object>, SSLConfig>();

    // used to cache misses for dynamic selections.
    @SuppressWarnings("unchecked")
    private final Set<Map<String, Object>> dynamicLookupMisses = new TreeSet(new DynamicSSLCacheMissComparator());

    /**
     * Constructor.
     */
    public OutboundSSLSelections() {
        // do nothing
    }

    public Map<String, String> getDynamicSelections() {
        return dynamicSelections;
    }

    /**
     * @param oldConnectionInfo
     */
    public void removeDynamicSelection(String oldConnectionInfo) {
        if (dynamicSelections.containsKey(oldConnectionInfo))
            dynamicSelections.remove(oldConnectionInfo);
        if (dynamicHostSelections.containsKey(oldConnectionInfo))
            dynamicHostSelections.remove(oldConnectionInfo);
        if (dynamicHostPortSelections.containsKey(oldConnectionInfo))
            dynamicHostPortSelections.remove(oldConnectionInfo);
    }

    /***
     * This method loads the dynamic selection info appropriate.
     *
     * @param config
     ***/
    public synchronized void loadOutboundConnectionInfo(String sslCfgAlias, Map<String, Object> config, Set<String> newConnectionInfo) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "loadOutboundConnectionInfo");

        List<Map<String, Object>> outboundEntries = Nester.nest("outboundConnection", config);

        if (!outboundEntries.isEmpty()) {
            for (Map<String, Object> outboundEntry : outboundEntries) {
                String key = null;
                String host = (String) outboundEntry.get("host");

                String certAlias = (String) outboundEntry.get("clientCertificate");
                if (certAlias != null) {
                    sslCfgAlias = sslCfgAlias + ":" + certAlias;
                }

                String port = (String) outboundEntry.get("port");
                if (port != null) {
                    key = host + "," + port.toString();
                    if (dynamicHostPortSelections.containsKey(key)) {
                        if (!dynamicHostPortSelections.get(key).equals(sslCfgAlias)) {
                            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                                Tr.debug(tc, "loadOutboundConnectionInfo", "Existing " + key + " : " + dynamicHostPortSelections.get(key) + ",  trying to add " + sslCfgAlias);
                            }
                            issueConflictWarning(host, port, dynamicHostPortSelections.get(key));
                        }
                    } else {
                        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                            Tr.debug(tc, "loadOutboundConnectionInfo", "Adding " + key + " to the host port list, sslCfgAlias " + sslCfgAlias);
                        }
                        dynamicHostPortSelections.put(key, sslCfgAlias);
                    }
                } else {
                    key = host + ",*";
                    if (dynamicHostSelections.containsKey(key)) {
                        if (!dynamicHostSelections.get(key).equals(sslCfgAlias))
                            issueConflictWarning(host, "*", dynamicHostSelections.get(key));
                    } else {
                        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                            Tr.debug(tc, "loadOutboundConnectionInfo", "Adding " + key + " to the host list");
                        }
                        dynamicHostSelections.put(key, sslCfgAlias);
                    }
                }

                newConnectionInfo.add(key);
                dynamicSelections.put(key, sslCfgAlias);
            }
        }

        // always clear the caches
        dynamicLookupCache.clear();
        synchronized (dynamicLookupMisses) {
            dynamicLookupMisses.clear();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "loadOutboundConnectionInfo");
    }

    /**
     * @param cfgAlias
     * @param key
     * @param sslCfgAlias
     */
    private void issueConflictWarning(String host, String port, String cfgAlias) {

        Tr.warning(tc, "ssl.dynamicSelection.conflict.CWPKI0815W", host, port, cfgAlias);

    }

    /***
     * This method returns a Properties object where the
     * connection information from the server.xml is used to match to the
     * connectionInfo HashMap passed in as a parameter. The HashMap contains
     * information about the target host/port.
     *
     * @param connectionInfo
     * @return Properties
     ***/
    public Properties getPropertiesFromDynamicSelectionInfo(Map<String, Object> connectionInfo) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getPropertiesFromDynamicSelectionInfo", new Object[] { connectionInfo });

        if (connectionInfo == null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "No connection information provided.");
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "getPropertiesFromDynamicSelectionInfo");
            return null; // this allows it to move forward with other precedence.
        }

        if (dynamicSelections.isEmpty()) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "There are no dynamic outbound selections configured.");
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "getPropertiesFromDynamicSelectionInfo");
            return null;
        }

        /***
         * First look for a cached MISS in case it has already been looked
         * up from this particular connection info data.
         ***/

        synchronized (dynamicLookupMisses) {
            if (dynamicLookupMisses.contains(connectionInfo)) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "This connectionInfo was checked before, found in the lookup misses cache.");
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "getPropertiesFromDynamicSelectionInfo");
                return null;
            }
        }

        /***
         * Next look for a cache HIT in case it has already been looked up from this
         * particular connection info data.
         ***/
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "Dynamic outbound lookup cache size is " + dynamicLookupCache.size());
        SSLConfig cachedConfig = dynamicLookupCache.get(connectionInfo);

        if (cachedConfig != null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "Found in cache.");
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "getPropertiesFromDynamicSelectionInfo", cachedConfig);
            return cachedConfig;
        }

        String direction = (String) connectionInfo.get(Constants.CONNECTION_INFO_DIRECTION);

        if (direction != null && direction.equals(Constants.DIRECTION_INBOUND)) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "Connection information is for an inbound connection return null.");
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "getPropertiesFromDynamicSelectionInfo");
            return null;
        }

        String connInfoRemoteHost = (String) connectionInfo.get(Constants.CONNECTION_INFO_REMOTE_HOST);
        String connInfoRemotePort = (String) connectionInfo.get(Constants.CONNECTION_INFO_REMOTE_PORT);

        /***
         * Look through all of the SSLConfigs in this list which all contain a
         * property with values for com.ibm.ssl.dynamicSelectionInfo.
         ***/

        if (connInfoRemoteHost != null && connInfoRemotePort != null) {
            SSLConfig sslCfg = lookForMatchInList(dynamicHostPortSelections, connInfoRemoteHost, connInfoRemotePort);

            if (sslCfg != null) {
                dynamicLookupCache.put(connectionInfo, sslCfg);
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "Found in the host and port list.");
                if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                    Tr.exit(tc, "getPropertiesFromDynamicSelectionInfo", sslCfg);
                return sslCfg;
            }

            sslCfg = lookForMatchInList(dynamicHostSelections, connInfoRemoteHost, connInfoRemotePort);
            if (sslCfg != null) {
                dynamicLookupCache.put(connectionInfo, sslCfg);
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "Found in the host list.");
                if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                    Tr.exit(tc, "getPropertiesFromDynamicSelectionInfo", sslCfg);
                return sslCfg;
            }
        }

        synchronized (dynamicLookupMisses) {
            if (dynamicLookupMisses.size() > 50) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Cache miss tree set size is > 50, clearing the TreeSet.");
                dynamicLookupMisses.clear();
            }
            dynamicLookupMisses.add(connectionInfo);
            if (tc.isDebugEnabled())
                Tr.debug(tc, "Cache miss tree set size is " + dynamicLookupMisses.size() + " entries.");
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "No match found in host or host and port list.");
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getPropertiesFromDynamicSelectionInfo");
        return null;
    }

    protected SSLConfig lookForMatchInList(Map<String, String> selectionList, String remoteHost, String remotePort) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "lookForMatchInList", new Object[] { remoteHost, remotePort });

        /***
         * Look through all of the SSLConfigs in this list which all contain a
         * property with values for com.ibm.ssl.dynamicSelectionInfo.
         ***/

        for (String dynamicSelectionInfo : selectionList.keySet()) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "SSLConfig dynamic selection info: " + dynamicSelectionInfo);

            /***
             * Split the dynamic selection info into separate entries for
             * host,port.
             ***/

            /***
             * Break each entry up into individual values.
             ***/
            String[] dynamicSelectionAttributes = dynamicSelectionInfo.split(",");

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "This entry has " + dynamicSelectionAttributes.length + " attributes.");

            /***
             * If an entry is not equal to two elements, host and
             * port, then it will not be used in the evaluation.
             ***/
            if (dynamicSelectionAttributes != null && dynamicSelectionAttributes.length == 2) {
                String host = dynamicSelectionAttributes[0];
                String port = dynamicSelectionAttributes[1];

                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "Host: " + host + ", Port: " + port);

                if (port == null) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                        Tr.debug(tc, "Ending evaluation, one of the values is null.");
                    continue;
                }

                /***
                 * The host must be a valid value in order to continue evaluation.
                 ***/

                if (!host.equals("*") && (remoteHost == null || !doesHostMatch(host, remoteHost))) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                        Tr.debug(tc, "Host does not match.");
                    continue;
                }

                /***
                 * The port is similar to the protocol in that it may be * but
                 * otherwise must be an exact match.
                 ***/
                if (!port.equals("*") && (remotePort == null || !doesPortMatch(port, remotePort))) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                        Tr.debug(tc, "Port does not match.");
                    continue;
                }

                /***
                 * Cache the connectionInfo / SSLConfig association for later use.
                 ***/

                String sslCfgAlias = selectionList.get(dynamicSelectionInfo);

                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "Found a dynamic selection match! with ssl configuration: " + sslCfgAlias);

                SSLConfig config = getSSLConfigForAlias(sslCfgAlias);

                if (config != null) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                        Tr.exit(tc, "lookForMatchInList", config);
                    return config;
                }
                continue;
            }
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "No match found list");

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "lookForMatchInList");
        return null;

    }

    /**
     * @param sslCfgAlias
     * @return
     */
    private SSLConfig getSSLConfigForAlias(String sslAliasAndCert) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getSSLConfigForAlias", new Object[] { sslAliasAndCert });

        String sslAlias = null;
        String sslCert = null;

        if (sslAliasAndCert != null && sslAliasAndCert.indexOf(":") != -1) {
            String[] split = sslAliasAndCert.split(":");

            if (split != null && split.length == 2) {
                sslAlias = split[0];
                sslCert = split[1];
            }
        } else if (sslAliasAndCert != null) {
            sslAlias = sslAliasAndCert;
        }

        SSLConfig config = SSLConfigManager.getInstance().getSSLConfig(sslAlias);

        if (config != null) {
            // see if selection cert matches default SSL cert
            if (sslCert != null) {
                String clientAlias = config.getProperty(Constants.SSLPROP_KEY_STORE_CLIENT_ALIAS);
                if (clientAlias == null || !clientAlias.equals(sslAlias)) {
                    // change the cert in the config
                    config = new SSLConfig(config);
                    config.setProperty(Constants.SSLPROP_KEY_STORE_CLIENT_ALIAS, sslCert);
                }
            }

            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "getSSLConfigForAlias", new Object[] { config });
            return config;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getSSLConfigForAlias");
        return null;
    }

    /*
     * Method to check if the hostname's match. Will check for domains to match
     * if the connectionObjHost parameter starts with "*."
     */
    private boolean doesHostMatch(String connectionObjHost, String remoteHost) {
        boolean match = false;

        if (remoteHost.equalsIgnoreCase(connectionObjHost))
            match = true;

        // see if we need to do a domain match
        if (connectionObjHost.startsWith("*.")) {
            String compareHost = connectionObjHost.substring(1);
            if (remoteHost.toLowerCase().endsWith(compareHost.toLowerCase()))
                match = true;
        }

        return match;
    }

    /*
     * Method to check if port numbers match.
     */
    private boolean doesPortMatch(String connectionObjPort, String remotePort) {
        boolean match = false;

        if (remotePort.equalsIgnoreCase(connectionObjPort))
            match = true;

        return match;
    }

}
