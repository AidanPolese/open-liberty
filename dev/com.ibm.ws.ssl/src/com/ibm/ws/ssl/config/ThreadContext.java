/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * @(#) 1.5 SERV1/ws/code/security.crypto/src/com/ibm/ws/ssl/config/ThreadContext.java, WAS.security.crypto, WASX.SERV1, pp0919.25 2/13/09 22:16:01 [5/15/09 18:04:44]
 *
 * Date         Defect        CMVC ID    Description
 *
 * 08/19/05     LIDB3557-1.1  pbirk      3557 Initial Code Drop
 * 12/15/05     LIDB3187-56   ttorres    3187-56 Cert Mapping
 * 01/06/06     333344        pbirk      Added methods for signer acceptance without storage.
 * 06/21/06     368562        danmorris  Added methods get/setOutboundConnectionInfo
 * 02/13/09     PK76448       yammer     Added methods get/setOutboundConnectionInfoInternal
 */

package com.ibm.ws.ssl.config;

import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Properties;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * Thread local context for SSL.
 * <p>
 * Used by Thread manager class to manage the SSL properties set on the thread.
 * This context allows information to be stored on the thread.
 * </p>
 * 
 * @author IBM Corporation
 * @version WAS 7.0
 * @since WAS 7.0
 */
public class ThreadContext {
    private static final TraceComponent tc = Tr.register(ThreadContext.class, "SSL", "com.ibm.ws.ssl.resources.ssl");

    private Properties sslProperties = null;
    private boolean setSignerOnThread = false;
    private boolean autoAcceptBootstrapSigner = false;
    private boolean autoAcceptBootstrapSignerWithoutStorage = false;
    private X509Certificate[] signer = null;
    private Map<String, Object> inboundConnectionInfo = null;
    private Map<String, Object> outboundConnectionInfo = null;
    private Map<String, Object> outboundConnectionInfoInternal = null;

    /**
     * Constructor.
     */
    public ThreadContext() {
        // do nothing
    }

    /**
     * Access the current properties for this context.
     * 
     * @return Properties - null if not set
     */
    public Properties getProperties() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "getProperties");
        return this.sslProperties;
    }

    /**
     * Set the properties for this thread context.
     * 
     * @param sslProps
     */
    public void setProperties(Properties sslProps) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "setProperties");
        this.sslProperties = sslProps;
    }

    /**
     * Query whether the signer flag is set on this context.
     * 
     * @return boolean
     */
    public boolean getSetSignerOnThread() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "getSetSignerOnThread: " + this.setSignerOnThread);
        return this.setSignerOnThread;
    }

    /**
     * Query whether the autoaccept bootstrap signer flag is set on this context.
     * 
     * @return boolean
     */
    public boolean getAutoAcceptBootstrapSigner() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "getAutoAcceptBootstrapSigner: " + this.autoAcceptBootstrapSigner);
        return this.autoAcceptBootstrapSigner;
    }

    /**
     * Access the inbound connection info object for this context.
     * 
     * @return Map<String,Object> - null if not set
     */
    public Map<String, Object> getInboundConnectionInfo() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "getInboundConnectionInfo");
        return this.inboundConnectionInfo;
    }

    /**
     * Set the autoaccept bootstrap signer flag on this context to the input
     * value.
     * 
     * @param flag
     */
    public void setAutoAcceptBootstrapSigner(boolean flag) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "setAutoAcceptBootstrapSigner -> " + flag);
        this.autoAcceptBootstrapSigner = flag;
    }

    /**
     * Query whether the autoaccept bootstrap signer without storage flag is set
     * on this context.
     * 
     * @return boolean
     */
    public boolean getAutoAcceptBootstrapSignerWithoutStorage() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "getAutoAcceptBootstrapSignerWithoutStorage: " + this.autoAcceptBootstrapSignerWithoutStorage);
        return this.autoAcceptBootstrapSignerWithoutStorage;
    }

    /**
     * Set the autoaccept bootstrap signer without storage flag to the input
     * value.
     * 
     * @param flag
     */
    public void setAutoAcceptBootstrapSignerWithoutStorage(boolean flag) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "setAutoAcceptBootstrapSignerWithoutStorage -> " + flag);
        this.autoAcceptBootstrapSignerWithoutStorage = flag;
    }

    /**
     * Set the signer flag on this context to the input value.
     * 
     * @param flag
     */
    public void setSetSignerOnThread(boolean flag) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "setSetSignerOnThread: " + flag);
        this.setSignerOnThread = flag;
    }

    /**
     * Query the signer chain set on this context.
     * 
     * @return X509Certificate[] - null if not set
     */
    public X509Certificate[] getSignerChain() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "getSignerChain");
        return signer == null ? null : signer.clone();
    }

    /**
     * Set the signer chain on this context to the input value.
     * 
     * @param signerChain
     */
    public void setSignerChain(X509Certificate[] signerChain) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "setSignerChain");
        this.signer = signerChain == null ? null : signerChain.clone();
    }

    /**
     * Set the inbound connection info on this context to the input value.
     * 
     * @param connectionInfo
     */
    public void setInboundConnectionInfo(Map<String, Object> connectionInfo) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "setInboundConnectionInfo");
        this.inboundConnectionInfo = connectionInfo;
    }

    /**
     * Query the outbound connection info map of this context.
     * 
     * @return Map<String,Object> - null if not set
     */
    public Map<String, Object> getOutboundConnectionInfo() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "getOutboundConnectionInfo");
        return this.outboundConnectionInfo;
    }

    /**
     * Set the outbound connection info of this context to the input value.
     * 
     * @param connectionInfo
     */
    public void setOutboundConnectionInfo(Map<String, Object> connectionInfo) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "setOutboundConnectionInfo");
        this.outboundConnectionInfo = connectionInfo;
    }

    /**
     * Get the internal outbound connection info object for this context.
     * 
     * @return Map<String,Object> - null if not set
     */
    public Map<String, Object> getOutboundConnectionInfoInternal() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "getOutboundConnectionInfoInternal");
        return this.outboundConnectionInfoInternal;
    }

    /**
     * Set the internal outbound connection info object for this context.
     * 
     * @param connectionInfo
     */
    public void setOutboundConnectionInfoInternal(Map<String, Object> connectionInfo) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "setOutboundConnectionInfoInternal :" + connectionInfo);
        this.outboundConnectionInfoInternal = connectionInfo;
    }
}
