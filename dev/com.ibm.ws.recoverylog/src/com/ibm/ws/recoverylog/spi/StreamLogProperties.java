/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2002, 2004    */
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
/*  Date      Programmer    Defect      Description                           */
/*  --------  ----------    ------      -----------                           */
/* 04-03-02   awilkins   LIDB2775-53.5  z/OS code merge                       */
/* 04-06-04   kaczyns       MD19667     StreamName to upper case              */
/* 04-06-04   kaczyns       MD19650     Add equals and hashCode methods       */
/* 05-07-13   kaczyns       PK08027     Add custom property string            */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;

/**
 * Implementation of the LogProperties interface on z/OS.  This implementation
 * writes to the System Logger instead of to the filesystem (HFS).
 */
public class StreamLogProperties implements LogProperties
{
    private static final TraceComponent tc = Tr.register(StreamLogProperties.class, TraceConstants.TRACE_GROUP, null);

    // The name of the logstream compression interval custom property
    static public final String COMPRESS_INTERVAL_NAME =         /* @PK08027A*/
        new String("RLS_LOGSTREAM_COMPRESS_INTERVAL");          /* @PK08027A*/

    // The unique RLI value.
    private int _logIdentifier = 0;

    //The unique RLN value.
    private String _logName = null;

    // The name of the LogStream.
    private String _streamName = null;
    
    public StreamLogProperties(int logId, String logName, String streamName)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "StreamLogProperties", new Object[]{new Integer(logId), logName, streamName});

        _logIdentifier = logId;
        _logName = logName;

        if (streamName == null)
        {
           IllegalArgumentException iae = 
               new IllegalArgumentException(
                   "Null logstream high level qualifier");
            if (tc.isEntryEnabled()) Tr.event(tc, iae.getMessage(), iae);
            throw iae;
        }    
        
        _streamName = streamName.toUpperCase();

        if (tc.isEntryEnabled()) Tr.exit(tc, "StreamLogProperties", this);
    }

    public int logIdentifier()
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "logIdentifier");
        if (tc.isEntryEnabled()) Tr.exit(tc, "logIdentifier", new Integer(_logIdentifier));
        return _logIdentifier;
    }

    public String logName()
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "logName", this);
        if (tc.isEntryEnabled()) Tr.exit(tc, "logName", _logName);
        return _logName;
    }

    public String streamName()
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "streamName", this);
        if (tc.isEntryEnabled()) Tr.exit(tc, "streamName", _streamName);
        return _streamName;
    }

    /**
     * Determine if two LogProperties references are the same. @MD19650A.
     * @param logProps The log properties to be checked
     * @return boolean true If compared objects are equal.
     */
    public boolean equals(Object lp)
    {
        if(lp == null) return false;
        else if (lp == this) return true;
        else if (lp instanceof StreamLogProperties)
        {
            StreamLogProperties zlp = (StreamLogProperties)lp;
            if (zlp.logIdentifier()== this.logIdentifier() &&
                zlp.logName().equals(this.logName()) &&
                zlp.streamName().equals(this.streamName()))
                return true;
        }
        return false;
    }

    /**
     * HashCode implementation. @MD19650A
     * @return int The hash code value.
     */
    public int hashCode()
    {
        int hashCode = 0;

        hashCode += _logIdentifier/3;
        hashCode += _logName.hashCode()/3;
        hashCode += _streamName.hashCode()/3;

        return hashCode;
  }
}
