/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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
    public MicroProfileJwtConfig getOnlyMatchingConfig() throws MpJwtProcessingException {
        throwExceptionIfPresent();
        if (microProfileJwtConfig == null) {
            microProfileJwtConfig = findAppropriateGenericConfig();
        }
        return microProfileJwtConfig;
    }

    void throwExceptionIfPresent() throws MpJwtProcessingException {
        if (taiException != null) {
            MpJwtProcessingException exception = this.taiException;
            this.taiException = null;
            throw exception;
        }
    }

    MicroProfileJwtConfig findAppropriateGenericConfig() throws MpJwtProcessingException {
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
                handleTooManyConfigurations();
            }
        }
        return this.microProfileJwtConfig;
    }

    void handleTooManyConfigurations() throws MpJwtProcessingException {
        // error handling -- multiple mpJwtConfig qualified and we do not know how to select
        String configIds = getConfigIds(genericConfigs);
        String msg = Tr.formatMessage(tc, "TOO_MANY_MP_JWT_PROVIDERS", new Object[] { configIds });
        Tr.error(tc, msg);
        throw new MpJwtProcessingException(msg);
    }

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
        return (this.genericConfigs != null || this.microProfileJwtConfig != null);
    }

    public void setTaiException(MpJwtProcessingException taiException) {
        this.taiException = taiException;
    }

}
