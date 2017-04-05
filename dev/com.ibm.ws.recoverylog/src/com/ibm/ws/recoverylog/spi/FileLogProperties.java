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
/* 06/06/03   beavenj       LIDB2472.2  Create                                */
/* 23/06/03   beavenj       170169      Constructor now correctly records size*/
/* 01/07/03   beavenj       170101      Support for expanding log file        */
/* 15/07/03   beavenj                   Add trace                             */
/* 04-03-02   awilkins   LIDB2775-53.5  z/OS code merge                       */
/* 04-05-08   hursdlg       MD19584     Make maxLogFileSize public            */
/* 31/03/04   mezarin       MD19650     Add equals and hashCode methods       */
/* 30/07/09   johawkes      602532      Remove WAS dependency                 */
/* ************************************************************************** */
package com.ibm.ws.recoverylog.spi;

import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;

//------------------------------------------------------------------------------
// Class: FileLogProperties
//------------------------------------------------------------------------------
/**
 * <p>
 * An implementation of the LogProperties interface that defines the physical
 * characteristics of a file based recovery log.
 * </p>
 * 
 * <p>
 * The file based recovery log stores information in a pair of files ('log1'
 * and 'log2') under the default or user specified directory path. The default
 * directory path used is <WAS_INSTALL>\RecoveryLogs\<SERVER_NAME>\<RLCN>\<RLN>
 * </p>
 */
public class FileLogProperties implements LogProperties
{
    /**
     * WebSphere RAS TraceComponent registration
     */
    private static final TraceComponent tc = Tr.register(FileLogProperties.class,
                                                         TraceConstants.TRACE_GROUP,
                                                         TraceConstants.NLS_FILE);

    /**
     * Indicates that a log that may contain multiple
     * failure scopes is required.
     */
    protected static final int LOG_TYPE_MULTIPLE_SCOPE = 0;

    /**
     * Indicates that a log that will only contain records
     * for a single failure scope is required.
     */
    protected static final int LOG_TYPE_SINGLE_SCOPE = 1;

    /**
     * default log type. Set during initalization.
     */
    static int defaultLogType = LOG_TYPE_SINGLE_SCOPE;

    /**
     * The unique RLI value.
     */
    private final int _logIdentifier;

    /**
     * The unique RLN value.
     */
    private final String _logName;

    /**
     * The phyisical log location
     */
    private final String _logDirectory;

    /**
     * The phyisical log size (in kilobytes)
     */
    private final int _logFileSize;

    /**
     * The phyisical log maximum size (in kilobytes)
     */
    private final int _maxLogFileSize;

    /**
     * The type of the log that is required.
     */
    private final int _logType;

    /**
     * The stem of the log location
     */
    private final String _logDirectoryStem;

    //------------------------------------------------------------------------------
    // Method: FileLogProperties.FileLogProperties
    //------------------------------------------------------------------------------
    /**
     * <p>
     * Constructor for a new FileLogProperties object. A file based recovery log
     * constructed using the resulting object will assume a default size and physical
     * location.
     * </p>
     * 
     * <p>
     * The logIdentifier and logName both uniquely identify a recovery log within
     * the client service.
     * </p>
     * 
     * @param logIdentifier The unique RLI value.
     * @param logName The unique RLN value.
     */
    public FileLogProperties(int logIdentifier, String logName)
    {
        this(logIdentifier, logName, null, 0, 0, null);
    }

    //------------------------------------------------------------------------------
    // Method: FileLogProperties.FileLogProperties
    //------------------------------------------------------------------------------
    /**
     * <p>
     * Constructor for a new FileLogProperties object. A file based recovery log
     * constructed using the resulting object will assume a default physical
     * location with the specified size.
     * </p>
     * 
     * <p>
     * The logIdentifier and logName both uniquely identify a recovery log within
     * the client service.
     * </p>
     * 
     * @param logIdentifier The unique RLI value.
     * @param logName The unique RLN value.
     * @param logFileSize The required size of the recovery log in kilobytes.
     */
    public FileLogProperties(int logIdentifier, String logName, int logFileSize)
    {
        this(logIdentifier, logName, null, logFileSize, logFileSize, null);
    }

    //------------------------------------------------------------------------------
    // Method: FileLogProperties.FileLogProperties
    //------------------------------------------------------------------------------
    /**
     * <p>
     * Constructor for a new FileLogProperties object. A file based recovery log
     * constructed using the resulting object will assume a default physical
     * location with the specified size.
     * </p>
     * 
     * <p>
     * The logIdentifier and logName both uniquely identify a recovery log within
     * the client service.
     * </p>
     * 
     * @param logIdentifier The unique RLI value.
     * @param logName The unique RLN value.
     * @param logFileSize The required size of the recovery log in kilobytes.
     * @param maxLogFileSize The required maximum log file sized in kilobytes.
     */
    public FileLogProperties(int logIdentifier, String logName, int logFileSize, int maxLogFileSize)
    {
        this(logIdentifier, logName, null, logFileSize, maxLogFileSize, null);
    }

    //------------------------------------------------------------------------------
    // Method: FileLogProperties.FileLogProperties
    //------------------------------------------------------------------------------
    /**
     * <p>
     * Constructor for a new FileLogProperties object. A file based recovery log
     * constructed using the resulting object will assume a default size with the
     * specified physical location.
     * </p>
     * 
     * <p>
     * The logIdentifier and logName both uniquely identify a recovery log within
     * the client service.
     * </p>
     * 
     * @param logIdentifier The unique RLI value.
     * @param logName The unique RLN value.
     * @param logDirectory The required physical location.
     */
    public FileLogProperties(int logIdentifier, String logName, String logDirectory)
    {
        this(logIdentifier, logName, logDirectory, 0, 0, null);
    }

    //------------------------------------------------------------------------------
    // Method: FileLogProperties.FileLogProperties
    //------------------------------------------------------------------------------
    /**
     * <p>
     * Constructor for a new FileLogProperties object. A file based recovery log
     * constructed using the resulting object will assume the specified size and
     * physical location.
     * </p>
     * 
     * <p>
     * The logIdentifier and logName both uniquely identify a recovery log within
     * the client service.
     * </p>
     * 
     * @param logIdentifier The unique RLI value.
     * @param logName The unique RLN value.
     * @param logDirectory The required physical log directory.
     * @param logFileSize The required size of the recovery log in kilobytes.
     */
    public FileLogProperties(int logIdentifier, String logName, String logDirectory, int logFileSize)
    {
        this(logIdentifier, logName, logDirectory, logFileSize, logFileSize, null);
    }

    public FileLogProperties(int logIdentifier, String logName, String logDirectory, int logFileSize, String logDirStem)
    {
        this(logIdentifier, logName, logDirectory, logFileSize, logFileSize, logDirStem);
    }

    /**
     * <p>
     * Constructor for a new FileLogProperties object. A file based recovery log
     * constructed using the resulting object will assume the specified size and
     * physical location.
     * </p>
     * 
     * <p>
     * The logIdentifier and logName both uniquely identify a recovery log within
     * the client service.
     * </p>
     * 
     * @param logIdentifier The unique RLI value.
     * @param logName The unique RLN value.
     * @param logDirectory The required physical log directory.
     * @param logFileSize The required size of the recovery log in kilobytes.
     * @param maxLogFileSize The required maximum size of the recovery log in kilobytes.
     */
    public FileLogProperties(int logIdentifier, String logName, String logDirectory, int logFileSize, int maxLogFileSize, String logDirStem)
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "FileLogProperties", new java.lang.Object[] { new Integer(logIdentifier),
                                                                      logName,
                                                                      logDirectory,
                                                                      new Integer(logFileSize),
                                                                      new Integer(maxLogFileSize),
                                                                      logDirStem });

        // Cache the supplied information.
        _logIdentifier = logIdentifier;
        _logName = logName;
        _logFileSize = logFileSize;
        _maxLogFileSize = maxLogFileSize;
        _logDirectory = logDirectory;
        _logDirectoryStem = logDirStem;
        _logType = defaultLogType;

        if (tc.isEntryEnabled())
            Tr.exit(tc, "FileLogProperties", this);
    }

    //------------------------------------------------------------------------------
    // Method: FileLogProperties.logIdentifier
    //------------------------------------------------------------------------------
    /**
     * Returns the unique (within service) "Recovery Log Identifier" (RLI) value.
     * 
     * @return int The unique RLI value.
     */
    @Override
    public int logIdentifier()
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "logIdentifier", this);
        if (tc.isEntryEnabled())
            Tr.exit(tc, "logIdentifier", new Integer(_logIdentifier));
        return _logIdentifier;
    }

    //------------------------------------------------------------------------------
    // Method: FileLogProperties.logName
    //------------------------------------------------------------------------------
    /**
     * Returns the unique (within service) "Recovery Log Name" (RLN).
     * 
     * @return String The unique RLN value.
     */
    @Override
    public String logName()
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "logName", this);
        if (tc.isEntryEnabled())
            Tr.exit(tc, "logName", _logName);
        return _logName;
    }

    //------------------------------------------------------------------------------
    // Method: FileLogProperties.logDirectory
    //------------------------------------------------------------------------------
    /**
     * Returns the physical location where a recovery log constructed from the target
     * object will reside.
     * 
     * @return String The phyisical log directory path
     */
    public String logDirectory()
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "logDirectory", this);
        if (tc.isEntryEnabled())
            Tr.exit(tc, "logDirectory", _logDirectory);
        return _logDirectory;
    }

    //------------------------------------------------------------------------------
    // Method: FileLogProperties.logDirectoryStem
    //------------------------------------------------------------------------------
    /**
     * Returns the stem of the location where a recovery log constructed from the target
     * object will reside.
     * 
     * @return String The stem of the log directory path
     */
    public String logDirectoryStem()
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "logDirectoryStem", this);
        if (tc.isEntryEnabled())
            Tr.exit(tc, "logDirectoryStem", _logDirectoryStem);
        return _logDirectoryStem;
    }

    //------------------------------------------------------------------------------
    // Method: FileLogProperties.logFileSize
    //------------------------------------------------------------------------------
    /**
     * Returns the physical log size of a recovery log constructed from the target
     * object.
     * 
     * @return int The phyisical log size (in kilobytes)
     */
    public int logFileSize()
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "logFileSize", this);
        if (tc.isEntryEnabled())
            Tr.exit(tc, "logFileSize", new Integer(_logFileSize));
        return _logFileSize;
    }

    //------------------------------------------------------------------------------
    // Method: FileLogProperties.maxLogFileSize
    //------------------------------------------------------------------------------
    /**
     * Returns the maximum physical log size of a recovery log constructed from the
     * target object.
     * 
     * @return int The maximum phyisical log size (in kilobytes)
     */
    public int maxLogFileSize()
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "maxLogFileSize", this);
        if (tc.isEntryEnabled())
            Tr.exit(tc, "maxLogFileSize", new Integer(_maxLogFileSize));
        return _maxLogFileSize;
    }

    protected int logType()
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "logType", this);
        if (tc.isEntryEnabled())
            Tr.exit(tc, "logType", new Integer(_logType));
        return _logType;
    }

    //------------------------------------------------------------------------------
    // Method: FileLogProperties.equals
    //---------------------------------------------------------------------@MD19650A
    /**
     * Determine if two LogProperties references are the same.
     * 
     * @param logProps The log properties to be checked
     * @return boolean true If compared objects are equal.
     */
    @Override
    public boolean equals(Object lp)
    {
        if (lp == null)
            return false;
        else if (lp == this)
            return true;
        else if (lp instanceof FileLogProperties)
        {
            FileLogProperties flp = (FileLogProperties) lp;

            if (flp.logIdentifier() == this.logIdentifier() &&
                flp.logFileSize() == this.logFileSize() &&
                flp.maxLogFileSize() == this.maxLogFileSize() &&
                flp.logType() == this.logType() &&
                flp.logName().equals(this.logName()))
            {
                if (flp.logDirectory() != null && this.logDirectory() != null)
                {
                    if ((this.logDirectory()).equals(flp.logDirectory()))
                        return true;
                }
                if (flp.logDirectory() == null && this.logDirectory() == null)
                {
                    return true;
                }
            }
        }

        return false;
    }

    //------------------------------------------------------------------------------
    // Method: FileLogProperties.hashCode
    //---------------------------------------------------------------------@MD19650A
    /**
     * HashCode implementation.
     * 
     * @return int The hash code value.
     */
    @Override
    public int hashCode()
    {
        int hashCode = 0;

        hashCode += _logIdentifier / 5;
        hashCode += _logName.hashCode() / 5;
        hashCode += _logFileSize / 5;
        hashCode += _maxLogFileSize / 5;
        if (_logDirectory != null)
            hashCode += _logDirectory.hashCode() / 5;
        hashCode += _logType;

        return hashCode;
    }

    public static void setDefaultLogType(int t)
    {
        defaultLogType = t;
    }
}
