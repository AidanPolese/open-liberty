/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/* DESCRIPTION:                                                                      */
/*                                                                                   */
/* Change History:                                                                   */
/*                                                                                   */
/* Date      Programmer  Defect         Description                                  */
/* --------  ----------  ------         -----------                                  */
/* 04-01-09  awilkins    LIDB2775-53.5  Creation - z/OS code merge                   */
/* 04-03-26  awilkins  LIDB2775-53.5.2  More z/OS code merge changes                 */
/* 13/04/04  beavenj     LIDB1578.1     Initial supprort for ha-recovery             */
/* 15/06/04  beavenj     216563         Code Review Changes                          */
/*                                                                                   */
/* ********************************************************************************* */

package com.ibm.ws.recoverylog.spi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.ibm.tx.util.logging.FFDCFilter;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;

//------------------------------------------------------------------------------
// Class: FileFailureScopeFactory
//------------------------------------------------------------------------------
/**
* Factory class for managing file based failure scope objects
*/
public class FileFailureScopeFactory implements FailureScopeFactory
{
    private static final TraceComponent tc = Tr.register(FileFailureScopeFactory.class, TraceConstants.TRACE_GROUP, null);
    private static final byte VERSION = 2;

    //------------------------------------------------------------------------------
    // Method: FileFailureScopeFactory.toFailureScope
    //------------------------------------------------------------------------------
    /**
    * <p>
    * Converts a serialized failurescope in the form of a byte sequence back into
    * a real FailureScope object.
    * </p>
    *
    * @param bytes The serialized FailureScope
    *
    * @return FailureScope A corrisponding FailureScope object.
    */
    public FailureScope toFailureScope(byte[] bytes)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "toFailureScope", new Object[] {RLSUtils.toHexString(bytes), this});

        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        final DataInputStream dis = new DataInputStream(bais);
        
        int version = 0;
        try
        {
          // The first byte is the failure scope ID. We can disgard this
          // as the factory manager has already determined that the 
          // failure scope contained in the byte[] should be inflated by
          // this factory.
          byte failureScopeID = dis.readByte();
          if (tc.isDebugEnabled()) Tr.debug(tc,"FailureScope version manager id is " + failureScopeID);

          version = (int)(dis.readByte());
          if (tc.isDebugEnabled()) Tr.debug(tc,"FailureScope version is " + version);
          
        }
        catch (IOException ioe)
        {
            FFDCFilter.processException(ioe, "com.ibm.ws.recoverylog.spi.FileFailureScopeFactory.toFailureScope", "61", this);
            if (tc.isEventEnabled()) Tr.event(tc, "IOException caught inflating failure scope", ioe);

            // REQD Throw an exception here, or return null from the method?    
        }
        
        FileFailureScope failureScope = null;
        
        if (version == VERSION)
        {
            try
            {
                final String serverName = dis.readUTF();
                failureScope = new FileFailureScope(serverName);
            }
            catch (IOException ioe)
            {
                FFDCFilter.processException(ioe, "com.ibm.ws.recoverylog.spi.FileFailureScopeFactory.toFailureScope", "68", this);
                if (tc.isEventEnabled()) Tr.event(tc, "IOException caught inflating failure scope", ioe);
                
                // REQD Throw an exception here, or return null from the method?    
            }
        }
        else
        {
            if (tc.isEventEnabled()) Tr.event(tc, "FailureScope version level not recognized. Expected version " + VERSION);
            // REQD Throw an exception if versions do not match, or return null from the method?
        }
             
        if (tc.isEntryEnabled()) Tr.exit(tc, "toFailureScope", failureScope);
        return failureScope;
    }
    
    //------------------------------------------------------------------------------
    // Method: FileFailureScopeFactory.toByteArray
    //------------------------------------------------------------------------------
    /**
    * <p>
    * Converts a FailureScope into a serialized form (a byte sequence)
    * </p>
    *
    * @param failureScope The target FailureScope
    *
    * @return byte[] A serialiazed form of the FailureScope.
    */
    public byte[] toByteArray(FailureScope failureScope)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "toByteArray", new Object[] {failureScope, this});
        
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(baos);
        
        byte[] bytes = null;
        
        try
        {
            dos.writeByte(FailureScopeFactory.FILE_FAILURE_SCOPE_ID.byteValue());
            dos.writeByte(VERSION);
            dos.writeUTF(failureScope.serverName());
            dos.flush();
            dos.close();            
            bytes = baos.toByteArray();
        }
        catch (IOException ioe)
        {
            FFDCFilter.processException(ioe, "com.ibm.ws.recoverylog.spi.FileFailureScopeFactory.toByteArray", "104", this);
            if (tc.isEventEnabled()) Tr.event(tc, "IOException caught deflating failure scope", ioe);
            
            // REQD Throw an exception here, or leave method to return null?    
        }
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "toByteArray", bytes);
        return bytes;
    }
}
