package com.ibm.tx.jta.util;

/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5639-D57, 5630-A36, 5630-A37, 5724-D18 (C) COPYRIGHT International Business Machines Corp. 2002, 2016 */
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                              */
/*                                                                            */
/*  DESCRIPTION:                                                              */
/*                                                                            */
/*  Change History:                                                           */
/*                                                                            */
/*  Date      Programmer    Defect     Description                            */
/*  --------  ----------    ------     -----------                            */
/*  13/04/04  beavenj       LIDB1578.1 Initial supprort for ha-recovery       */
/*  06/06/07  johawkes      443467     Moved                                  */
/*  02/06/09  mallam        596067     package move                           */
/*  14/06/12  nyoung        735581     Support custom Transaction logging     */
/*  26/04/16  dmatthew      PI61057    EmbeddedEJBContainer allow disable logs*/
/* ************************************************************************** */

import java.util.Properties;

import com.ibm.tx.TranConstants;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;

public final class TranLogConfiguration
{
    private static final TraceComponent tc = Tr.register(TranLogConfiguration.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    public static final int TYPE_NONE = 0;
    public static final int TYPE_STREAM = 1;
    public static final int TYPE_FILE = 2;
    public static final int TYPE_CUSTOM = 3;

    private String _streamName;
    private String _originalLogDirectory;
    private String _expandedLogDirectory;
    private int _logFileSize;
    private boolean _enabled = true;
    private int _type = TYPE_NONE;
    private String _customId;
    private Properties _customProps;

    public TranLogConfiguration()
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "TranLogConfiguration");

        _enabled = false;

        if (tc.isEntryEnabled())
            Tr.exit(tc, "TranLogConfiguration", this);
    }

    public TranLogConfiguration(String streamName)
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "TranLogConfiguration", streamName);

        _type = TYPE_STREAM;
        _streamName = streamName;

        if (tc.isEntryEnabled())
            Tr.exit(tc, "TranLogConfiguration", this);
    }

    public TranLogConfiguration(String original, String expanded, int logFileSize)
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "TranLogConfiguration", new Object[] { original, expanded, logFileSize });

        _type = TYPE_FILE;
        _originalLogDirectory = original;
        _expandedLogDirectory = expanded;
        _logFileSize = logFileSize;
        if (original != null && ";0".equals(original.trim()))
            _enabled = false;

        if (tc.isEntryEnabled())
            Tr.exit(tc, "TranLogConfiguration", this);
    }

    public TranLogConfiguration(String customId, Properties props)
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "TranLogConfiguration", new Object[] { customId, props });

        _type = TYPE_CUSTOM;
        _customId = customId;
        _customProps = props;

        if (tc.isEntryEnabled())
            Tr.exit(tc, "TranLogConfiguration", this);
    }

    public int type()
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "type", _type);
        return _type;
    }

    public String streamName()
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "streamName", _streamName);
        return _streamName;
    }

    public String originalLogDirectory()
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "originalLogDirectory", _originalLogDirectory);
        return _originalLogDirectory;
    }

    public String expandedLogDirectory()
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "expandedLogDirectory", _expandedLogDirectory);
        return _expandedLogDirectory;
    }

    public int logFileSize()
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "logFileSize", _logFileSize);
        return _logFileSize;
    }

    public boolean enabled()
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "enabled", _enabled);
        return _enabled;
    }

    public String customId()
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "customId", _customId);
        return _customId;
    }

    public Properties customProperties()
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "customProperties", _customProps);
        return _customProps;
    }
}