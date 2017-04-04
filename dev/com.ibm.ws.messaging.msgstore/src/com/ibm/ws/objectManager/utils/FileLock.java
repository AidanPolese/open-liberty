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
 * Change activity:
 *
 *  Reason           Date    Origin     Description
 * --------------- -------- -------- ------------------------------------------
 *  607710         21/08/09 gareth    Add isAnyTracingEnabled() check around trace
 * ============================================================================
 */

/**
 * <p>Encapsulate the locking of a file.
 * 
 * @author IBM Corporation
 */
public abstract class FileLock
{
    static final Class cclass = FileLock.class;
    static Trace trace = Utils.traceFactory.getTrace(cclass,
                                                     UtilsConstants.MSG_GROUP_UTILS);

    /**
     * Create a platform specific FileLock instance.
     * 
     * @param file to be locked. The file must be already open.
     * @param fileName of the file.
     * @return FileLock for the file.
     * @throws java.io.IOException
     */
    public static FileLock getFileLock(java.io.RandomAccessFile file, String fileName) throws java.io.IOException
    {
        return (FileLock) Utils.getImpl("com.ibm.ws.objectManager.utils.FileLockImpl",
                                        new Class[] { java.io.RandomAccessFile.class, String.class },
                                        new Object[] { file, fileName });
    } // getFileLock().

    /**
     * Test the lock.
     * 
     * @return true if the lock is held, otherwise false.
     */
    public abstract boolean tryLock();

    /**
     * Unlock the file.
     * 
     * @throws java.io.IOException
     */
    public abstract void release() throws java.io.IOException;
}
