package com.ibm.ws.objectManager.utils;

/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2013
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 *
 *
 * Change activity:
 *
 *  Reason           Date    Origin     Description
 * --------------- -------- -------- ------------------------------------------
 *  251161         07/04/05 gareth    Add ObjectManager code to CMVC
 *  343689         04/04/06 gareth    Modify trace output cont.
 *  607710         21/08/09 gareth    Add isAnyTracingEnabled() check around trace
 * ============================================================================
 */

/**
 * <p>Encapsulate the locking of the log file.
 * 
 * @author IBM Corporation
 */
public class FileLockImpl
                extends FileLock {
    private static final Class cclass = FileLockImpl.class;
    private static Trace trace = Utils.traceFactory.getTrace(cclass,
                                                             UtilsConstants.MSG_GROUP_UTILS);

    private java.nio.channels.FileChannel fileChannel;
    private java.nio.channels.FileLock fileLock = null;

    /**
     * Make a file lock using NIO.
     * 
     * @param file the open file.
     * @param fileName of the file.
     * @throws java.io.IOException
     */
    public FileLockImpl(java.io.RandomAccessFile file,
                        String fileName)
        throws java.io.IOException {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this,
                        cclass,
                        "<init>",
                        new Object[] { file,
                                      fileName });

        // Make sure no one else can write to the log.
        // Obtain an exclusive lock on the fileChannel.
        fileChannel = file.getChannel();
        fileLock = fileChannel.tryLock();

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this,
                       cclass,
                       "<init>");
    } // FileLockImpl();

    /**
     * Test the lock.
     * 
     * @return true if the lock is held, otherwise false.
     */
    public boolean tryLock()
    {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this
                        , cclass
                        , "tryLock"
                            );

        boolean isLocked = true;
        if (fileLock == null) // Did we get the lock?
            isLocked = false;

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this
                       , cclass
                       , "tryLock"
                       , "returns isLocked=" + isLocked + "(bloolean)"
                            );
        return isLocked;
    } // tryLock().

    /**
     * Unlock the file.
     * 
     * @throws java.io.IOException
     */
    public final void release()
                    throws java.io.IOException {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this,
                        cclass,
                        "release");

        // Give up the lock.
        if (fileLock != null)
            fileLock.release();
        fileLock = null;

        if (fileChannel != null)
            fileChannel.close();
        fileChannel = null;

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this,
                       cclass,
                       "release");
    } // release().
} // FileLock class.
