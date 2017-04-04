/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
/*
 * Some of the code was derived from code supplied by the Apache Software Foundation licensed under the Apache License, Version 2.0.
 */
package com.ibm.ws.transport.iiop.security.config.css;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.security.csiv2.config.CompatibleMechanisms;
import com.ibm.ws.transport.iiop.security.config.tss.TSSCompoundSecMechConfig;
import com.ibm.ws.transport.iiop.security.config.tss.TSSCompoundSecMechListConfig;

/**
 * @version $Rev: 503274 $ $Date: 2007-02-03 10:19:18 -0800 (Sat, 03 Feb 2007) $
 */
public class CSSCompoundSecMechListConfig implements Serializable {
    private static TraceComponent tc = Tr.register(CSSCompoundSecMechListConfig.class);
    private boolean stateful;
    private final ArrayList<CSSCompoundSecMechConfig> mechs = new ArrayList<CSSCompoundSecMechConfig>();

    private static final String spaces = "  ";

    public boolean isStateful() {
        return stateful;
    }

    public void setStateful(boolean stateful) {
        this.stateful = stateful;
    }

    public void add(CSSCompoundSecMechConfig mech) {
        mechs.add(mech);
    }

    public CSSCompoundSecMechConfig mechAt(int i) {
        return mechs.get(i);
    }

    public int size() {
        return mechs.size();
    }

    public LinkedList<CompatibleMechanisms> findCompatibleList(TSSCompoundSecMechListConfig mechList) {
        LinkedList<CompatibleMechanisms> compatibleMechanismsList = new LinkedList<CompatibleMechanisms>();
        StringBuilder cantHandleMsg = new StringBuilder();

        // Iterate according to the target's mechanism preference.
        int size = mechList.size();
        for (int i = 0; i < size; i++) {
            TSSCompoundSecMechConfig requirement = mechList.mechAt(i);
            for (CSSCompoundSecMechConfig aConfig : mechs) {
                if (aConfig.canHandle(requirement)) {
                    compatibleMechanismsList.add(new CompatibleMechanisms(aConfig, requirement));
                } else {
                    buildCantHandleMsg(cantHandleMsg, aConfig);
                }
            }
        }
        if (compatibleMechanismsList.isEmpty()) {
            Tr.error(tc, "CSIv2_CLIENT_COMPATIBLE_CHECK_FAILED", cantHandleMsg.toString());
        }
        return compatibleMechanismsList;
    }

    private void buildCantHandleMsg(StringBuilder cantHandleMsg, CSSCompoundSecMechConfig aConfig) {
        cantHandleMsg.append(spaces).append(spaces);
        cantHandleMsg.append(aConfig.getCantHandleMsg()).append("\n");
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, aConfig.getCantHandleMsg());
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        toString("", buf);
        return buf.toString();
    }

    @Trivial
    void toString(String spaces, StringBuilder buf) {
        buf.append(spaces).append("CSSCompoundSecMechListConfig: [\n");
        for (CSSCompoundSecMechConfig aConfig : mechs) {
            aConfig.toString(spaces + "  ", buf);
            buf.append("\n");
        }
        buf.append(spaces).append("]\n");
    }

}
