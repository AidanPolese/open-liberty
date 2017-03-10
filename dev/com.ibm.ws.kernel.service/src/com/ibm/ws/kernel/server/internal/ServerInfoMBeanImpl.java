/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.server.internal;

import java.util.Map;

import javax.management.DynamicMBean;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

import com.ibm.websphere.kernel.server.ServerInfoMBean;
import com.ibm.ws.kernel.productinfo.DuplicateProductInfoException;
import com.ibm.ws.kernel.productinfo.ProductInfo;
import com.ibm.ws.kernel.productinfo.ProductInfoParseException;
import com.ibm.ws.kernel.productinfo.ProductInfoReplaceException;
import com.ibm.wsspi.kernel.service.location.VariableRegistry;
import com.ibm.wsspi.kernel.service.location.WsLocationAdmin;
import com.ibm.wsspi.kernel.service.location.WsLocationConstants;

/**
 * Because this exposes information that is already contained in the collective
 * repository, this MBean is not published to the collective.
 */
@Component(service = { ServerInfoMBean.class, DynamicMBean.class },
           immediate = true,
           configurationPolicy = ConfigurationPolicy.IGNORE,
           property = { "service.vendor=IBM",
                        "jmx.objectname=" + ServerInfoMBean.OBJECT_NAME,
                        "publishAttributesToCollectiveController=false" })
public class ServerInfoMBeanImpl extends StandardMBean implements ServerInfoMBean {
    static final String VAR_DEFAULTHOSTNAME = "${defaultHostName}";

    private WsLocationAdmin locAdmin;
    private VariableRegistry varReg;

    private final static String JAVA_SPEC_VERSION = System.getProperty("java.specification.version");
    private final static String JAVA_RUNTIME_VERSION = System.getProperty("java.runtime.version");

    public ServerInfoMBeanImpl() throws NotCompliantMBeanException {
        super(ServerInfoMBean.class);
    }

    @Reference(service = WsLocationAdmin.class)
    protected void setWsLocationAdmin(WsLocationAdmin ref) {
        this.locAdmin = ref;
    }

    protected void unsetWsLocationAdmin(WsLocationAdmin ref) {
        if (this.locAdmin == ref) {
            this.locAdmin = null;
        }
    }

    @Reference(service = VariableRegistry.class)
    protected void setVariableRegistry(VariableRegistry ref) {
        this.varReg = ref;
    }

    protected void unsetVariableRegistry(VariableRegistry ref) {
        if (this.varReg == ref) {
            this.varReg = null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getDefaultHostname() {
        final String hostname = varReg.resolveString(VAR_DEFAULTHOSTNAME);
        // If we have no specified host name (we get the raw variable back), or the host name is empty, default to localhost
        if (VAR_DEFAULTHOSTNAME.equals(hostname) || hostname.trim().isEmpty()) {
            return "localhost";
        } else if ("*".equals(hostname)) {
            return null; // Carried over behaviour from CollectiveHostName
        } else {
            return hostname.toLowerCase();
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getUserDirectory() {
        return locAdmin.resolveString(WsLocationConstants.SYMBOL_USER_DIR);
    }

    /** {@inheritDoc} */
    @Override
    public String getInstallDirectory() {
        return locAdmin.resolveString(WsLocationConstants.SYMBOL_INSTALL_DIR);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return locAdmin.getServerName();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.websphere.kernel.server.ServerInfoMBean#getLibertyVersion()
     */
    @Override
    public String getLibertyVersion() {
        Map<String, ProductInfo> productIdToVersionPropertiesMap = null;

        try {
            productIdToVersionPropertiesMap = ProductInfo.getAllProductInfo();

            for (ProductInfo versionProperties : productIdToVersionPropertiesMap.values()) {
                if (versionProperties.getReplacedBy() == null) {
                    return versionProperties.getVersion();
                }
            }
        } catch (ProductInfoParseException e) {
            // ignore exceptions - best effort to get a pretty string
        } catch (DuplicateProductInfoException e) {
            // ignore exceptions - best effort to get a pretty string
        } catch (ProductInfoReplaceException e) {
            // ignore exceptions - best effort to get a pretty string
        } ;

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.websphere.kernel.server.ServerInfoMBean#getJavaSpecVersion()
     */
    @Override
    public String getJavaSpecVersion() {
        return JAVA_SPEC_VERSION;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.websphere.kernel.server.ServerInfoMBean#getJavaRuntimeVersion()
     */
    @Override
    public String getJavaRuntimeVersion() {
        return JAVA_RUNTIME_VERSION;
    }

}
