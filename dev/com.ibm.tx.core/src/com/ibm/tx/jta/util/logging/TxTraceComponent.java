package com.ibm.tx.jta.util.logging;
/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/*  ORIGINS: 27                                                                                          */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002, 2013 */
/* The source code for this program is not published or otherwise divested                               */
/* of its trade secrets, irrespective of what has been deposited with the                                */
/* U.S. Copyright Office.                                                                                */
/*                                                                                                       */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                                         */
/*                                                                                                       */
/*  DESCRIPTION: Standalone Tracer class                                                                 */
/*                                                                                                       */
/*  Change History:                                                                                      */
/*                                                                                                       */
/*  Date      Programmer    Defect   Description                                                         */
/*  --------  ----------    ------   -----------                                                         */
/*  13/08/13  slaterpa      752004   TRANSUMMARY trace                                                   */
/* ***************************************************************************************************** */

import com.ibm.tx.util.logging.TraceComponent;

public class TxTraceComponent implements TraceComponent
{
    private Class _class;
    private String _name;
    private String _traceGroup;
    private String _nlsFile;

    /**
     * Trace guard fields.
     * Even though the interface defines getters and setters for the following
     * fields, the TxTraceComponent implementation treats these as
     * class fields shared by each TraceComponent instance.
     * TxTr initialises these based on the ConfigurationProvider traceLevel.
     */
    public static boolean svEntryEnabled = false;
    public static boolean svEventEnabled = false;
    public static boolean svDebugEnabled = false;
    public static boolean svWarningEnabled = true;

    public TxTraceComponent(Class cl, String traceGroup, String nlsFile)
    {
        _class = cl;
        _traceGroup = traceGroup;
        _nlsFile = nlsFile;
    }

    public TxTraceComponent(String s, String traceGroup, String nlsFile) {
       _name = s;
       _traceGroup = traceGroup;
       _nlsFile = nlsFile;
    }

   public boolean isDebugEnabled()
    {
        return svDebugEnabled;
    }

    public boolean isEntryEnabled()
    {
        return svEntryEnabled;
    }

    public boolean isEventEnabled()
    {
        return svEventEnabled;
    }

    public boolean isWarningEnabled()
    {
        return svWarningEnabled;
    }

    public Object getData()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getClassName()
    {
        return _class != null ?_class.getName() : _name;
    }

	public void setDebugEnabled(boolean enabled)
	{
		svDebugEnabled = enabled;
	}

	public void setEntryEnabled(boolean enabled)
	{
		svEntryEnabled = enabled;
	}

	public void setEventEnabled(boolean enabled)
	{
		svEventEnabled = enabled;
	}

	public void setWarningEnabled(boolean enabled)
	{
		svWarningEnabled = enabled;
	}
}
