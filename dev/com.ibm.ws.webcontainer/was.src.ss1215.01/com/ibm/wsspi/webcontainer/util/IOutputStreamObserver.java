// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.webcontainer.util;

/**
 * Observes the events of an OutputStream.
 */
public interface IOutputStreamObserver
{
/**
 * Notification that the OutputStream has been closed.
 */
    void alertClose();
/**
 * Notification that the OutputStream has been written to for the first time.
 */
    void alertFirstWrite();

/**
 * Notification that the OutputStream has been flushed to for the first time.
 */
    void alertFirstFlush();
/**
 * Notification that there has been an exception in the OutputStream.
 */
    void alertException();
}
