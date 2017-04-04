/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2005, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * @(#) 1.4 SERV1/ws/code/security.crypto/src/com/ibm/ws/ssl/config/KeyManagerData.java, WAS.security.crypto, WASX.SERV1, pp0919.25 11/12/07 09:17:10 [5/15/09 18:04:32]
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
 * Configuration class of a key manager object.
 * <p>
 * This class handles the KeyManager information coming from the WCCM model. The
 * information is converted to this class so the model can be released.
 * </p>
 * 
 * @author IBM Corporation
 * @version WAS 7.0
 * @since WAS 7.0
 */
public class KeyManagerData {
    private final static TraceComponent tc = Tr.register(KeyManagerData.class,
                                                         "SSL",
                                                         "com.ibm.ws.ssl.resources.ssl");

    private String kmName = null;
    private String kmProvider = null;
    private String kmAlgorithm = null;
    private String kmCustomClass = null;
    private Properties kmCustomProps = null;

    /**
     * Constructor with a provided name and array of property values.
     * 
     * @param _name
     * @param properties
     */
    public KeyManagerData(String _name, Map<String, String> properties) {
        this.kmName = _name;
        for (Entry<String, String> current : properties.entrySet()) {
            final String key = current.getKey();
            final String value = current.getValue();
            if (key.equalsIgnoreCase("algorithm")) {
                this.kmAlgorithm = value;
            } else if (key.equalsIgnoreCase("provider")) {
                this.kmProvider = value;
            } else if (key.equalsIgnoreCase("keyManagerClass")) {
                this.kmCustomClass = value;
            } else {
                // custom property
                if (null == this.kmCustomProps) {
                    this.kmCustomProps = new Properties();
                }
                this.kmCustomProps.setProperty(key, value);
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
    public KeyManagerData(String name,
                          String provider,
                          String algorithm,
                          String customClass,
                          Properties customAttributes) {
        this.kmName = name;
        this.kmProvider = provider;
        this.kmAlgorithm = algorithm;
        this.kmCustomClass = customClass;
        this.kmCustomProps = customAttributes;
    }

    public String getName() {
        return this.kmName;
    }

    public void setName(String s) {
        this.kmName = s;
    }

    public String getProvider() {
        return this.kmProvider;
    }

    public void setProvider(String s) {
        this.kmProvider = s;
    }

    public String getAlgorithm() {
        return this.kmAlgorithm;
    }

    public void setAlgorithm(String s) {
        this.kmAlgorithm = s;
    }

    public String getKeyManagerClass() {
        return this.kmCustomClass;
    }

    public void setKeyManagerClass(String s) {
        this.kmCustomClass = s;
    }

    public Properties getAdditionalKeyManagerAttrs() {
        return this.kmCustomProps;
    }

    public void setAdditionalKeyManagerAttrs(Map<String, String> attributes) {
        if (null == this.kmCustomProps) {
            this.kmCustomProps = new Properties();
        }
        for (Entry<String, String> attr : attributes.entrySet()) {
            this.kmCustomProps.setProperty(attr.getKey(), attr.getValue());
        }
    }

    public String getKeyManagerString() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "getKeyManagerString");

        String rc = null;

        if (kmCustomClass != null) {
            rc = kmCustomClass;
        } else if (kmAlgorithm != null && kmProvider != null) {
            rc = kmAlgorithm + "|" + kmProvider;
        } else if (kmAlgorithm != null) {
            rc = kmAlgorithm;
        } else {
            rc = JSSEProviderFactory.getKeyManagerFactoryAlgorithm();
        }
        if (tc.isEntryEnabled())
            Tr.exit(tc, "getKeyManagerString -> " + rc);
        return rc;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("KeyManagerData: name=").append(this.kmName);
        sb.append(", algorithm=").append(this.kmAlgorithm);
        sb.append(", provider=").append(this.kmProvider);
        sb.append(", customClass=").append(this.kmCustomClass);

        return sb.toString();
    }

}
