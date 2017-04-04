// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// 1.14 SERV1/ws/code/ecutils/src/com/ibm/ws/threadContext/ComponentMetaDataAccessorImpl.java, WAS.ejbcontainer, WAS80.SERV1, h1116.09 5/20/10 09:50:40
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2001,2013
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  ComponentMetaDataAccessorImpl.java
//
// Source File Description:
//
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
//LIDB1181.2.1 ASV     20011005 rajeshk  : new for Feature LI1181
//LIDB549.20 ASV       20020222 jimvo    : Eliminate JavaNameSpaceAccessor dependency
// d122727   ASV       20020325 kjlaw    : use DefaultComponentMetaData object
//                                         as initial value for ThreadContext.
// p125735   ASV50     20020416 jimvo    : provide begin and end context methods as
//                                         a precusor to removing getThreadContext
// d143991   ASV50     20020827 leealber : Tr.Misuse fix-up
// LIDB3133-8  WASX    20030507 rschnier : Added ibm-private tag
// 206479    WASX      20040601 cheng1   : Added getComponentMetaDataIndex()
// LI3795-56 WAS61     20050815 tkb      : Perf: Make class final
// d306998.5 WAS61     20060110 tkb      : PERF: improve trace performance
// d646139.1 WAS80     20100519 bkail    : Generify ThreadContext
// --------- --------- -------- --------- -------------------------------------
package com.ibm.ws.threadContext;

//import com.ibm.ejs.csi.DefaultComponentMetaData; //122727
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.kernel.LibertyProcess;
import com.ibm.ws.kernel.boot.internal.BootstrapConstants;
import com.ibm.ws.runtime.metadata.ComponentMetaData;

/**
 * 
 * <p>Accessor for ComponentMetaData. It provides static methods for easy access.
 * It is a singleton.
 * 
 * @ibm-private-in-use
 */
public final class ComponentMetaDataAccessorImpl {
    private static final TraceComponent tc =
                    Tr.register(ComponentMetaDataAccessorImpl.class,
                                "Runtime", "com.ibm.ws.runtime.runtime"); // d143991

    private static ComponentMetaDataAccessorImpl cmdai =
                    new ComponentMetaDataAccessorImpl();

    private ThreadContext<ComponentMetaData> threadContext = null;

    // this default CMD is used in cases like the client container where there is only one possible
    // active component - so that container is responsible for setting the defaultCMD via the
    // DefaultCMD service.
    private ComponentMetaData defaultCMD;

    private ComponentMetaDataAccessorImpl() {
        //        threadContext = new ThreadContextImpl<ComponentMetaData>(DefaultComponentMetaData.getInstance()); //122727
        threadContext = new ThreadContextImpl<ComponentMetaData>();
    }

    /**
     * 
     * @return ComponentMetaDataAccessorImpl
     */
    public static ComponentMetaDataAccessorImpl getComponentMetaDataAccessor() {
        return cmdai;
    }

    /**
     * @return ComponentMetaData
     */
    public ComponentMetaData getComponentMetaData() {
        ComponentMetaData cmd = threadContext.getContext();
        return cmd == null ? defaultCMD : cmd;
    }

    /**
     * @return ThreadContext
     * @deprecated use beginContext and endContext methods provided by ComponentMetaDataImpl
     */
    @Deprecated
    public ThreadContext<ComponentMetaData> getThreadContext() {
        return threadContext;
    }

    /**
     * Begin the context for the ComponentMetaData provided.
     * 
     * @param ComponentMetaData It Must not be null. Tr.error will be logged if it is null.
     * @return Previous Object, which was on the stack. It can be null.
     */
    public Object beginContext(ComponentMetaData cmd) { // modified to return object d131914
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            if (cmd != null)
                Tr.debug(tc, "begin context " + cmd.getJ2EEName());
            else
                Tr.debug(tc, "NULL was passed.");
        }

        if (cmd == null) {
            Tr.error(tc, "WSVR0603E"); // d143991
            throw new IllegalArgumentException(Tr.formatMessage(tc, "WSVR0603E"));
        }

        return threadContext.beginContext(cmd); //131914
    }

    /**
     * Establish default context for when there should be no ComponentMetaData on the thread.
     * 
     * @return Previous component metadata which was on the thread. It can be null.
     */
    public ComponentMetaData beginDefaultContext() {
        return threadContext.beginContext(null);
    }

    /**
     * End the context for the current ComponentMetaData
     * 
     * @return Object which was removed (pop) from the stack.
     */
    public Object endContext() { // modified to return object d131914
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            ComponentMetaData cmd = getComponentMetaData();
            Tr.debug(tc, "end context " + (cmd == null ? null : cmd.getJ2EEName()));
        }
        return threadContext.endContext(); //d131914
    }

    /**
     * @return The index of ComponentMetaData
     */
    public int getComponentMetaDataIndex() {
        return threadContext.getContextIndex();
    }

    public void setDefaultCMD(ComponentMetaData defaultCMD) {
        if (isClient()) {
            this.defaultCMD = defaultCMD;
        } else if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "setDefaultCMD called in non-client process - ignoring " + defaultCMD, new Throwable("StackTrace"));
        }

    }

    private boolean isClient() {
        Bundle b = FrameworkUtil.getBundle(ComponentMetaDataAccessorImpl.class);
        BundleContext bc = b == null ? null : b.getBundleContext();
        ServiceReference<LibertyProcess> sr = bc == null ? null : bc.getServiceReference(LibertyProcess.class);
        return BootstrapConstants.LOC_PROCESS_TYPE_CLIENT.equals((sr == null ? null : sr.getProperty(BootstrapConstants.LOC_PROPERTY_PROCESS_TYPE)));
    }
} //ComponentMetaDataAccessorImpl end
