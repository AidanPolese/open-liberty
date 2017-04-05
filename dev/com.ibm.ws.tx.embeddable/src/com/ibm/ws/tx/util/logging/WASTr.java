/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009, 2013 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/*  DESCRIPTION:                                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  YY-MM-DD  Developer  Defect    Description                                       */
/*  --------  ---------  ------    -----------                                       */
/*  13-08-10  slaterpa   752004     TRANSUMMARY trace                                */
/* ********************************************************************************* */
package com.ibm.ws.tx.util.logging;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.tx.util.logging.Tracer;

public class WASTr implements Tracer
{
    public void debug(com.ibm.tx.util.logging.TraceComponent tc, String s)
    {
        Tr.debug((TraceComponent)tc.getData(), s);
    }

    public void debug(com.ibm.tx.util.logging.TraceComponent tc, String s, Object o)
    {
        Tr.debug((TraceComponent)tc.getData(), s, o);
    }

    public void debug(com.ibm.tx.util.logging.TraceComponent tc, String s, Object[] o)
    {
        Tr.debug((TraceComponent)tc.getData(), s, o);
    }

    public void entry(com.ibm.tx.util.logging.TraceComponent tc, String s, Object o)
    {
        Tr.entry((TraceComponent)tc.getData(), s, o);
    }

    public void entry(com.ibm.tx.util.logging.TraceComponent tc, String s, Object[] o)
    {
        Tr.entry((TraceComponent)tc.getData(), s, o);
    }

    public void entry(com.ibm.tx.util.logging.TraceComponent tc, String s)
    {
        Tr.entry((TraceComponent)tc.getData(), s);
    }

    public void error(com.ibm.tx.util.logging.TraceComponent tc, String s, Object o)
    {
        Tr.error((TraceComponent)tc.getData(), s, o);
    }

    public void error(com.ibm.tx.util.logging.TraceComponent tc, String s, Object[] o)
    {
        Tr.error((TraceComponent)tc.getData(), s, o);
    }

    public void error(com.ibm.tx.util.logging.TraceComponent tc, String s)
    {
        Tr.error((TraceComponent)tc.getData(), s);
    }

    public void event(com.ibm.tx.util.logging.TraceComponent tc, String s)
    {
        Tr.event((TraceComponent)tc.getData(), s);
    }

    public void event(com.ibm.tx.util.logging.TraceComponent tc, String s, Object o)
    {
        Tr.event((TraceComponent)tc.getData(), s, o);
    }

    public void event(com.ibm.tx.util.logging.TraceComponent tc, String s, Object[] o)
    {
        Tr.event((TraceComponent)tc.getData(), s, o);
    }

    public void exit(com.ibm.tx.util.logging.TraceComponent tc, String s)
    {
        Tr.exit((TraceComponent)tc.getData(), s);
    }

    public void exit(com.ibm.tx.util.logging.TraceComponent tc, String s, Object o)
    {
        Tr.exit((TraceComponent)tc.getData(), s, o);
    }

    public void exit(com.ibm.tx.util.logging.TraceComponent tc, String s, Object[] o)
    {
        Tr.exit((TraceComponent)tc.getData(), s, o);
    }

    public void fatal(com.ibm.tx.util.logging.TraceComponent tc, String s, Object o)
    {
        Tr.fatal((TraceComponent)tc.getData(), s, o);
    }

    public void fatal(com.ibm.tx.util.logging.TraceComponent tc, String s, Object[] o)
    {
        Tr.fatal((TraceComponent)tc.getData(), s, o);
    }

    public void info(com.ibm.tx.util.logging.TraceComponent tc, String s, Object o)
    {
        Tr.info((TraceComponent)tc.getData(), s, o);
    }

    public void info(com.ibm.tx.util.logging.TraceComponent tc, String s, Object[] o)
    {
        Tr.info((TraceComponent)tc.getData(), s, o);
    }

    public com.ibm.tx.util.logging.TraceComponent register(Class cl, String traceGroup, String nlsFile)
    {
        return new WASTraceComponent(Tr.register(cl, traceGroup, nlsFile));
    }
    
    @Override
    public com.ibm.tx.util.logging.TraceComponent register(String s, String traceGroup, String nlsFile) {
       
       return new WASTraceComponent(Tr.register(s, traceGroup, nlsFile));
    }

    public void warning(com.ibm.tx.util.logging.TraceComponent tc, String s, Object o)
    {
        Tr.warning((TraceComponent)tc.getData(), s, o);
    }

    public void warning(com.ibm.tx.util.logging.TraceComponent tc, String s, Object[] o)
    {
        Tr.warning((TraceComponent)tc.getData(), s, o);
    }

    public void audit(com.ibm.tx.util.logging.TraceComponent tc, String s)
    {
        Tr.audit((TraceComponent)tc.getData(), s);
    }

    public void audit(com.ibm.tx.util.logging.TraceComponent tc, String s, Object o)
    {
        Tr.audit((TraceComponent)tc.getData(), s, o);
    }

    public void audit(com.ibm.tx.util.logging.TraceComponent tc, String s, Object[] o)
    {
        Tr.audit((TraceComponent)tc.getData(), s, o);
    }

    public void fatal(com.ibm.tx.util.logging.TraceComponent tc, String s)
    {
        Tr.fatal((TraceComponent)tc.getData(), s);
    }

    public void info(com.ibm.tx.util.logging.TraceComponent tc, String s)
    {
        Tr.info((TraceComponent)tc.getData(), s);
    }

    public void warning(com.ibm.tx.util.logging.TraceComponent tc, String s)
    {
        Tr.warning((TraceComponent)tc.getData(), s);
    }

    public void initTrace()
    {
    }
}