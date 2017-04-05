package com.ibm.tx.util.logging;
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
/*  09-11-09  johawkes   F743-305.1 EJB 3.1                                          */
/*  13-08-13  slaterpa   752004    TRANSUMMARY trace                                 */
/* ********************************************************************************* */

public interface Tracer
{
    public void audit(TraceComponent tc, String s);

    public void audit(TraceComponent tc, String s, Object o);

    public void audit(TraceComponent tc, String s, Object[] o);

    public void debug(TraceComponent tc, String s);

    public void debug(TraceComponent tc, String s, Object o);

    public void debug(TraceComponent tc, String s, Object[] o);

    public void entry(TraceComponent tc, String s);

    public void entry(TraceComponent tc, String s, Object o);

    public void entry(TraceComponent tc, String s, Object[] o);

    public void error(TraceComponent tc, String s);

    public void error(TraceComponent tc, String s, Object o);

    public void error(TraceComponent tc, String s, Object[] o);

    public void event(TraceComponent tc, String s);

    public void event(TraceComponent tc, String s, Object o);

    public void event(TraceComponent tc, String s, Object[] o);

    public void exit(TraceComponent tc, String s);

    public void exit(TraceComponent tc, String s, Object o);

    public void exit(TraceComponent tc, String s, Object[] o);

    public void fatal(TraceComponent tc, String s);

    public void fatal(TraceComponent tc, String s, Object o);

    public void fatal(TraceComponent tc, String s, Object[] o);

    public void info(TraceComponent tc, String s);

    public void info(TraceComponent tc, String s, Object o);

    public void info(TraceComponent tc, String s, Object[] o);

    public void warning(TraceComponent tc, String s);

    public void warning(TraceComponent tc, String s, Object o);

    public void warning(TraceComponent tc, String s, Object[] o);

    public TraceComponent register(Class cl, String traceGroup, String nlsFile);

    public TraceComponent register(String s, String traceGroup, String nlsFile);
    
    public void initTrace();
}
