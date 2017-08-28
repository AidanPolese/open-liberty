/*
 *
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2016
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 */

package com.ibm.ws.security.mp.jwt.impl.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.security.mp.jwt.MicroProfileJwtConfig;
import com.ibm.ws.security.mp.jwt.TraceConstants;
import com.ibm.ws.security.mp.jwt.error.MpJwtProcessingException;

/*
 * Store the data for a httpServletRequest session
 *
 * Initialize when a session starts and
 * discard after it ends
 */
public class MicroProfileJwtTaiRequest {
    private static TraceComponent tc = Tr.register(MicroProfileJwtTaiRequest.class, TraceConstants.TRACE_GROUP, TraceConstants.MESSAGE_BUNDLE);

    protected String providerName; // providerId

    protected HttpServletRequest request;

    //protected List<MicroProfileJwtConfig> filteredConfigs = null;
    protected List<MicroProfileJwtConfig> genericConfigs = null;
    MicroProfileJwtConfig microProfileJwtConfig = null;
    MpJwtProcessingException taiException = null;

    /**
     * Called by TAI for now
     *
     * @param request2
     */
    public MicroProfileJwtTaiRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * @param service
     */
    //    public void addFilteredConfig(MicroProfileJwtConfig mpJwtConfig) {
    //        if (mpJwtConfig != null) {
    //            if (this.filteredConfigs == null) {
    //                this.filteredConfigs = new ArrayList<MicroProfileJwtConfig>();
    //            }
    //            if (!this.filteredConfigs.contains(mpJwtConfig)) {
    //                this.filteredConfigs.add(mpJwtConfig);
    //            }
    //        }
    //    }

    /**
     * This is supposed to be called once in a request only.
     *
     * @param service
     */
    public void setSpecifiedConfig(MicroProfileJwtConfig mpJwtConfig) {
        this.microProfileJwtConfig = mpJwtConfig;
    }

    /**
     * @param service
     */
    public void addGenericConfig(MicroProfileJwtConfig mpJwtConfig) {
        if (mpJwtConfig != null) {
            if (this.genericConfigs == null) {
                this.genericConfigs = new ArrayList<MicroProfileJwtConfig>();
            }
            if (!this.genericConfigs.contains(mpJwtConfig)) {
                this.genericConfigs.add(mpJwtConfig);
            }
        }
    }

    public String getProviderName() {
        if (this.providerName == null) {
            if (this.microProfileJwtConfig != null) {
                this.providerName = this.microProfileJwtConfig.getUniqueId();
            }
        }
        return this.providerName;
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    //    public List<MicroProfileJwtConfig> getFilteredConfigs() {
    //        return this.filteredConfigs;
    //    }

    public List<MicroProfileJwtConfig> getGenericConfigs() {
        return this.genericConfigs;
    }

    public Set<MicroProfileJwtConfig> getAllMatchingConfigs() {
        Set<MicroProfileJwtConfig> allConfigs = new HashSet<MicroProfileJwtConfig>();
        if (microProfileJwtConfig != null) {
            allConfigs.add(microProfileJwtConfig);
        }
        //        if (filteredConfigs != null) {
        //            allConfigs.addAll(filteredConfigs);
        //        }
        if (genericConfigs != null) {
            allConfigs.addAll(genericConfigs);
        }
        return allConfigs;
    }

    public Set<String> getAllMatchingConfigIds() {
        Set<String> allConfigIds = new HashSet<String>();
        Set<MicroProfileJwtConfig> allConfigs = getAllMatchingConfigs();
        for (MicroProfileJwtConfig config : allConfigs) {
            allConfigIds.add(config.getUniqueId());
        }
        return allConfigIds;
    }

    /**
     * @return
     */
    public MicroProfileJwtConfig getTheOnlyConfig() throws MpJwtProcessingException {
        if (taiException != null) {
            MpJwtProcessingException exception = this.taiException;
            this.taiException = null;
            throw exception;
        }
        if (this.microProfileJwtConfig == null) {
            //            if (this.filteredConfigs != null) {
            //                if (this.filteredConfigs.size() == 1) {
            //                    this.microProfileJwtConfig = this.filteredConfigs.get(0);
            //                } else {
            //                    // error handling -- multiple mpJwtConfig qualified and we do not know how to select
            //                    String configIds = getConfigIds(filteredConfigs);
            //                    throw new MpJwtProcessingException("SOCIAL_LOGIN_MANY_PROVIDERS", null, new Object[] { configIds });
            //                }
            //            } else
            if (this.genericConfigs != null) {
                if (this.genericConfigs.size() == 1) {
                    this.microProfileJwtConfig = this.genericConfigs.get(0);
                } else {
                    // error handling -- multiple mpJwtConfig qualified and we do not know how to select
                    String configIds = getConfigIds(genericConfigs);
                    throw new MpJwtProcessingException("SOCIAL_LOGIN_MANY_PROVIDERS", null, new Object[] { configIds });
                }
            }
        }
        // mpJwtConfig should not be null, since we checked hasServices() before we are here
        // It either get one qualified SocialLoginConfig.
        // Or threw an exception due to multiple qualified configs.
        return this.microProfileJwtConfig;
    }

    /**
     * @return
     */
    String getConfigIds(List<MicroProfileJwtConfig> multiConfigs) {
        String result = "";
        if (multiConfigs == null) {
            return result;
        }
        boolean bInit = true;
        for (MicroProfileJwtConfig mpJwtConfig : multiConfigs) {
            if (!bInit) {
                result = result.concat(", ");
            } else {
                bInit = false;
            }
            result = result.concat(mpJwtConfig.getUniqueId());
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Micro Profile Jwt TaiRequest [provider:").append(getProviderName()).append(" request:").append(this.request).append("]");
        return sb.toString();
    }

    /**
     * @return
     */
    public boolean hasServices() {
        return this.genericConfigs != null ||
                this.microProfileJwtConfig != null;
    }

    public void setTaiException(MpJwtProcessingException taiException) {
        this.taiException = taiException;
    }

}
