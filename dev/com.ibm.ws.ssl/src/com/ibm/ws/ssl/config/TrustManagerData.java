/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2005, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * @(#) 1.4 SERV1/ws/code/security.crypto/src/com/ibm/ws/ssl/config/TrustManagerData.java, WAS.security.crypto, WASX.SERV1, pp0919.25 11/12/07 09:17:24 [5/15/09 18:04:33]
 *
 * Date         Defect        CMVC ID    Description
 *
 * 08/19/05     LIDB3557-1.1  pbirk      3557 Initial Code Drop
 * 11/18/06     LIDB4119-33   paulben    Runtime Config Service
 */

package com.ibm.ws.ssl.config;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ssl.JSSEProviderFactory;

/**
 * Configuration class for a trust manager definition.
 * <p>
 * This class holds the attributes of a TrustManager configured in the WCCM
 * model.
 * </p>
 * 
 * @author IBM Corporation
 * @version WAS 7.0
 * @since WAS 7.0
 */
public class TrustManagerData {
    private final static TraceComponent tc = Tr.register(TrustManagerData.class, "SSL", "com.ibm.ws.ssl.resources.ssl");

    private String tmName;
    private String tmProvider;
    private String tmAlgorithm;
    private String tmCustomClass;
    private Properties tmCustomProps;

    /**
     * Constructor with a given name and list of configuration properties.
     * 
     * @param _name
     * @param properties
     */
    public TrustManagerData(String _name, Map<String, String> properties) {
        this.tmName = _name;
        for (Entry<String, String> prop : properties.entrySet()) {
            final String key = prop.getKey();
            final String value = prop.getValue();
            if (key.equalsIgnoreCase("algorithm")) {
                this.tmAlgorithm = value;
            } else if (key.equalsIgnoreCase("provider")) {
                this.tmProvider = value;
            } else if (key.equalsIgnoreCase("trustManagerClass")) {
                this.tmCustomClass = value;
            } else {
                // custom property
                if (null == this.tmCustomProps) {
                    this.tmCustomProps = new Properties();
                }
                this.tmCustomProps.setProperty(key, value);

            }
        }
    }

    /**
     * Constructor.
     * 
     * @param name
     * @param provider
     * @param algorithm
     * @param customClass
     * @param customAttributes
     */
    public TrustManagerData(String name, String provider, String algorithm, String customClass, Properties customAttributes) {
        this.tmName = name;
        this.tmProvider = provider;
        this.tmAlgorithm = algorithm;
        this.tmCustomClass = customClass;
        this.tmCustomProps = customAttributes;
    }

    public String getName() {
        return this.tmName;
    }

    public void setName(String s) {
        this.tmName = s;
    }

    public String getProvider() {
        return this.tmProvider;
    }

    public void setProvider(String s) {
        this.tmProvider = s;
    }

    public String getAlgorithm() {
        return this.tmAlgorithm;
    }

    public void setAlgorithm(String s) {
        this.tmAlgorithm = s;
    }

    public String getTrustManagerClass() {
        return this.tmCustomClass;
    }

    public void setTrustManagerClass(String s) {
        this.tmCustomClass = s;
    }

    public Properties getAdditionalTrustManagerAttrs() {
        return this.tmCustomProps;
    }

    public void setAdditionalTrustManagerAttrs(Map<String, String> attributes) {
        if (null == this.tmCustomProps) {
            this.tmCustomProps = new Properties();
        }
        for (Entry<String, String> attr : attributes.entrySet()) {
            this.tmCustomProps.setProperty(attr.getKey(), attr.getValue());
        }
    }

    public String getTrustManagerString() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getTrustManagerString");
        String rc = null;

        if (tmCustomClass != null) {
            rc = tmCustomClass;
        } else if (tmAlgorithm != null && tmProvider != null) {
            rc = tmAlgorithm + "|" + tmProvider;
        } else if (tmAlgorithm != null) {
            rc = tmAlgorithm;
        } else {
            rc = JSSEProviderFactory.getTrustManagerFactoryAlgorithm();
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getTrustManagerString -> " + rc);
        return rc;
    }

    /*
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder sb = new StringBuilder(128);

        sb.append("TrustManagerData: name=").append(this.tmName);
        sb.append(", algorithm=").append(this.tmAlgorithm);
        sb.append(", provider=").append(this.tmProvider);
        sb.append(", customClass=").append(this.tmCustomClass);

        return sb.toString();
    }
}
