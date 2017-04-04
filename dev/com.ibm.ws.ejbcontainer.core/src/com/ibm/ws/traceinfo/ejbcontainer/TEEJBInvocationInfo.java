/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2003, 2006
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.traceinfo.ejbcontainer;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.ejs.container.EJSDeployedSupport;
import com.ibm.ejs.container.EJSWrapperBase;

/**
 * Processor to write out and read in a EJB method invocation record.
 */
public class TEEJBInvocationInfo extends TEEJBInvocationInfoBase
{
    private static final String CLASS_NAME = TEEJBInvocationInfo.class.getName();
    private static final TraceComponent tc =
                    Tr.register(CLASS_NAME,
                                "TEExplorer",
                                "com.ibm.ws.traceinfo.ejbcontainer");

    /**
     * This is called by the EJB container server code to write a
     * EJB method call preinvoke begins record to the trace log, if enabled.
     */
    public static void tracePreInvokeBegins(EJSDeployedSupport s, EJSWrapperBase wrapper)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            StringBuffer sbuf = new StringBuffer();

            sbuf
                            .append(MthdPreInvokeEntry_Type_Str).append(DataDelimiter)
                            .append(MthdPreInvokeEntry_Type).append(DataDelimiter);

            writeDeployedSupportInfo(s, sbuf, wrapper, null);

            Tr.debug(tc, sbuf.toString());
        }
    }

    /**
     * This is called by the EJB container server code to write a
     * EJB method call preinvoke ends record to the trace log, if enabled.
     */
    public static void tracePreInvokeEnds(EJSDeployedSupport s, EJSWrapperBase wrapper)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            StringBuffer sbuf = new StringBuffer();

            sbuf
                            .append(MthdPreInvokeExit_Type_Str).append(DataDelimiter)
                            .append(MthdPreInvokeExit_Type).append(DataDelimiter);

            writeDeployedSupportInfo(s, sbuf, wrapper, null);

            Tr.debug(tc, sbuf.toString());
        }
    }

    /**
     * This is called by the EJB container server code to write a
     * EJB method call preinvoke exceptions record to the trace log, if enabled.
     */
    public static void tracePreInvokeException(EJSDeployedSupport s,
                                               EJSWrapperBase wrapper,
                                               Throwable t)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            StringBuffer sbuf = new StringBuffer();

            sbuf
                            .append(MthdPreInvokeException_Type_Str).append(DataDelimiter)
                            .append(MthdPreInvokeException_Type).append(DataDelimiter);

            writeDeployedSupportInfo(s, sbuf, wrapper, t);

            Tr.debug(tc, sbuf.toString());
        }
    }

    /**
     * This is called by the EJB container server code to write a
     * EJB method call postinvoke begins record to the trace log, if enabled.
     */
    public static void tracePostInvokeBegins(EJSDeployedSupport s,
                                             EJSWrapperBase wrapper)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            StringBuffer sbuf = new StringBuffer();

            sbuf
                            .append(MthdPostInvokeEntry_Type_Str).append(DataDelimiter)
                            .append(MthdPostInvokeEntry_Type).append(DataDelimiter);

            writeDeployedSupportInfo(s, sbuf, wrapper, null);

            Tr.debug(tc, sbuf.toString());
        }
    }

    /**
     * This is called by the EJB container server code to write a
     * EJB method call postinvoke ends record to the trace log, if enabled.
     */
    public static void tracePostInvokeEnds(EJSDeployedSupport s, EJSWrapperBase wrapper)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            StringBuffer sbuf = new StringBuffer();

            sbuf
                            .append(MthdPostInvokeExit_Type_Str).append(DataDelimiter)
                            .append(MthdPostInvokeExit_Type).append(DataDelimiter);

            writeDeployedSupportInfo(s, sbuf, wrapper, null);

            Tr.debug(tc, sbuf.toString());
        }
    }

    /*
     * This is called by the EJB container server code to write a
     * EJB method call postinvoke exceptions record to the trace log, if enabled.
     */
    public static void tracePostInvokeException(EJSDeployedSupport s,
                                                EJSWrapperBase wrapper,
                                                Throwable t)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            StringBuffer sbuf = new StringBuffer();

            sbuf
                            .append(MthdPostInvokeException_Type_Str).append(DataDelimiter)
                            .append(MthdPostInvokeException_Type).append(DataDelimiter);

            writeDeployedSupportInfo(s, sbuf, wrapper, t);

            Tr.debug(tc, sbuf.toString());
        }
    }

    /**
     * Returns true if trace for this class is enabled. This is used to guard the
     * caller to avoid unncessary processing before the trace is depositied.
     */
    // d173022
    public static boolean isTraceEnabled()
    {
        return (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled());
    }
}
