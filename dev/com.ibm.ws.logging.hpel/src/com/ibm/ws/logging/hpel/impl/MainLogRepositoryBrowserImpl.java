//%Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009,2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change History:
 *
 * Reason           Version        Date       User id     Description
 * ----------------------------------------------------------------------------
 * F017049-18055.1    8.0        12/22/2009   spaunganm
 * F017049-18796.1    8.0        12/22/2009   spaungam    Add support for z/OS HPEL according to new file structure
 * 634600             8.0        01/13/2010   spaungam    refactor code to allow for write manager to use for retention purposes
 * F017049-18504      8.0        02/25/2010   spaungam    Add support for subprocess reading
 * 662627             8.0        06/27/2010    belyi      Return 'null' if timestamps on all instance directories are bigger then requested timestamp.
 */
package com.ibm.ws.logging.hpel.impl;

import java.io.File;
import java.io.FileFilter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.ws.logging.hpel.LogRepositoryBrowser;
import com.ibm.ws.logging.hpel.MainLogRepositoryBrowser;
import com.ibm.ws.logging.object.hpel.RepositoryPointerImpl;

/**
 * Implementation of the browser over instances recorded in a file system as directories
 * containing log files and potentially directories for sub-processes.
 */
public class MainLogRepositoryBrowserImpl extends LogRepositoryBaseImpl implements MainLogRepositoryBrowser {
    private final static String BUNDLE_NAME = "com.ibm.ws.logging.hpel.resources.HpelMessages";
    private final static String className = MainLogRepositoryBrowserImpl.class.getName();
    private final static Logger logger = Logger.getLogger(className, BUNDLE_NAME);

    public MainLogRepositoryBrowserImpl(File repositoryLocation) {
        super(repositoryLocation);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.logging.hpel.LogInstanceBrowser#find(com.ibm.ws.logging.hpel.impl.RepositoryPointerImpl)
     */
    @Override
    public LogRepositoryBrowser find(RepositoryPointerImpl location, boolean ignoreTimestamp) {
        String[] instanceIds = location.getInstanceIds();
        if (instanceIds.length == 0) {
            logger.logp(Level.SEVERE, className, "find", "HPEL_NotRepositoryLocation");
            return null;
        }

        String locProcId = ignoreTimestamp ? parsePIDandLabel(instanceIds[0]) : instanceIds[0];
        LogRepositoryBrowser result = null;
        File[] files = listFiles(instanceFilter);
        if (files == null) {
            return null;
        }
        for (File file : files) {
            String curProcId = ignoreTimestamp ? parsePIDandLabel(file.getName()) : file.getName();
            if (locProcId.equalsIgnoreCase(curProcId)) {
                result = new LogRepositoryBrowserImpl(file, new String[] { file.getName() });
                for (int i = 1; i < instanceIds.length && result != null; i++) {
                    Map<String, LogRepositoryBrowser> map = result.getSubProcesses();
                    result = map.get(instanceIds[i]);
                }
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.logging.hpel.LogInstanceBrowser#findNext(com.ibm.ws.logging.hpel.impl.RepositoryPointerImpl, long)
     */
    @Override
    public LogRepositoryBrowser findNext(RepositoryPointerImpl location,
                                         long timelimit) {
        String[] instanceIds = location.getInstanceIds();
        if (instanceIds.length == 0) {
            logger.logp(Level.SEVERE, className, "findNext", "HPEL_NotRepositoryLocation");
            return null;
        }

        return findNext(parseTimeStamp(instanceIds[0]), timelimit);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.logging.hpel.LogInstanceBrowser#findByMillis(long)
     */
    @Override
    public LogRepositoryBrowser findByMillis(long timestamp) {
        File[] files = listFiles(instanceFilter);
        if (files == null) {
            return null;
        }

        File result = null;
        long max = Long.MIN_VALUE;
        for (File file : files) {
            long time = parseTimeStamp(file.getName());
            // Select file with a maximum time stamp which is smaller than 'timestamp'.
            if (max < time && (timestamp < 0 || time <= timestamp)) {
                max = time;
                result = file;
            }
        }

        // If all files has timestamp bigger than 'timestamp' return null.
        if (result == null) {
            return null;
        } else {
            return new LogRepositoryBrowserImpl(result, new String[] { result.getName() });
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.logging.hpel.LogInstanceBrowser#findNext(com.ibm.ws.logging.hpel.LogFileBrowser, long)
     */
    @Override
    public LogRepositoryBrowser findNext(LogRepositoryBrowser current, long timelimit) {
        long cur;
        if (current == null) {
            cur = Long.MIN_VALUE;
        } else if (current instanceof LogRepositoryBrowserImpl) {
            // Check if it's an instance browser.
            if (((LogRepositoryBrowserImpl) current).getIds().length == 1) {
                cur = parseTimeStamp(((LogRepositoryBrowserImpl) current).getLocation().getName());
            } else {
                // 'subprocess' browsers are not ordered and as such do not have 'next'.
                return null;
            }
        } else {
            return null;
        }

        return findNext(cur, timelimit);
    }

    /**
     * Find instance with a smallest timestamp bigger than value of <code>cur</code>.
     * 
     * @param cur time stamp of the previous instance.
     * @param timelimit time limit after which we are not interested in the result.
     * @return LogFileBrowser instance or <code>null</code> if there's not such instance or
     *         if its timestamp is bigger than <code>timelimit</code>.
     */
    private LogRepositoryBrowser findNext(long cur, long timelimit) {
        File[] files = listFiles(instanceFilter);
        if (files == null) {
            return null;
        }

        File result = null;
        long min = Long.MAX_VALUE;
        for (File file : files) {
            long time = parseTimeStamp(file.getName());
            // Select directory with a smallest time stamp bigger than time stamp of the 'current'.
            if (cur < time && time < min) {
                min = time;
                result = file;
            }
        }

        // return 'null' if found directory has time stamp outside of the time limit.
        if (result == null || (timelimit > 0 && timelimit < min)) {
            return null;
        }

        return new LogRepositoryBrowserImpl(result, new String[] { result.getName() });
    }

    /**
     * @param instanceFilter
     * @return
     */
    private File[] listFiles(final FileFilter instanceFilter) {
        return AccessController.doPrivileged(new PrivilegedAction<File[]>() {
            @Override
            public File[] run() {
                return getLocation().listFiles(instanceFilter);
            }
        });
    }

}
