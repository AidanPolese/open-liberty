package com.ibm.ws.objectManager.utils.concurrent.locks;

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
 */

/**
 * Native JVM implementation.
 */
public class ReentrantReadWriteLockImpl
                implements ReentrantReadWriteLock {
    java.util.concurrent.locks.ReentrantReadWriteLock readWriteLock;
    ReadLock readLock;
    WriteLock writeLock;

    public ReentrantReadWriteLockImpl() {
        readWriteLock = new java.util.concurrent.locks.ReentrantReadWriteLock();
        readLock = new ReadLock(readWriteLock);
        writeLock = new WriteLock(readWriteLock);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.objectManager.utils.concurrent.locks.ReentrantReadWriteLock#readLock()
     */
    public Lock readLock() {
        return readLock;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.objectManager.utils.concurrent.locks.ReentrantReadWriteLock#writeLock()
     */
    public Lock writeLock() {
        return writeLock;
    }

    public class ReadLock implements Lock {
        java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock javaReadLock;

        private ReadLock(java.util.concurrent.locks.ReentrantReadWriteLock readWriteLock) {
            javaReadLock = readWriteLock.readLock();
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.ibm.ws.objectManager.utils.concurrent.locks.ReadLock#lock()
         */
        public final void lock() {
            javaReadLock.lock();
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.ibm.ws.objectManager.utils.concurrent.locks.ReadLock#lockInterruptibly()
         */
        public final void lockInterruptibly()
                        throws InterruptedException {
            javaReadLock.lockInterruptibly();
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.ibm.ws.objectManager.utils.concurrent.locks.ReadLock#unlock()
         */
        public final void unlock() {
            javaReadLock.unlock();
        }
    } // inner class ReadLock.

    public class WriteLock implements Lock {
        java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock javaWriteLock;

        private WriteLock(java.util.concurrent.locks.ReentrantReadWriteLock readWriteLock) {
            javaWriteLock = readWriteLock.writeLock();
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.ibm.ws.objectManager.utils.concurrent.locks.WriteLock#lock()
         */
        public final void lock() {
            javaWriteLock.lock();
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.ibm.ws.objectManager.utils.concurrent.locks.WriteLock#lockInterruptibly()
         */
        public final void lockInterruptibly()
                        throws InterruptedException {
            javaWriteLock.lockInterruptibly();
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.ibm.ws.objectManager.utils.concurrent.locks.WriteLock#unlock()
         */
        public final void unlock() {
            javaWriteLock.unlock();
        }
    } // inner class WriteLock.
} // class ReentrantReadWriteLock.