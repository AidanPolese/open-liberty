/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* ORIGINS: 27                                                                */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2004    */
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                              */
/*                                                                            */
/* DESCRIPTION:                                                               */
/*                                                                            */
/* Change History:                                                            */
/*                                                                            */
/* Date      Programmer  Defect         Description                           */
/* --------  ----------  ------         -----------                           */
/* 03-09-29  awilkins                                                         */
/*           beavenj     175817         Changes for CScope logging (NIO)      */
/* 04-01-09  awilkins    LIDB2775-53.5  z/OS code merge                       */
/* 04-03-24  awilkins  LIDB2775.53.5.1  Non-mapped files                      */
/*                                                                            */
/* ************************************************************************** */
package com.ibm.ws.recoverylog.spi;

import java.nio.ByteBuffer;

import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;

// REQD: LogRecord, ReadableLogRecord and WritableLogRecord need to be re-factored
//       in the 5.1.2 timeframe. They are not very well organized.
 
//------------------------------------------------------------------------------
// Class: WritableLogRecord 
//------------------------------------------------------------------------------
/**
* <p>
* The WritableLogRecord class extends LogRecord with additional function to write 
* the record header and tail structures. It also provides an interface to
* allow other recovery log components to write the record content.
* </p>
*
* <p>
* Because this class uses a duplicate of the original MappedByteBuffer (see
* LogRecord), each instance has an indepednent byte cursor. As a result, it
* is safe to allow concurrent write calls from multiple instances of this
* class.
* </p>
*/                                                                          
public class WriteableLogRecord extends LogRecord
{
    private static final TraceComponent tc = Tr.register(WriteableLogRecord.class, TraceConstants.TRACE_GROUP, null);    
                                                                                        
    /**
    * The sequence number of this log record (see record structure above)
    */
    private long _sequenceNumber = 0;

    //------------------------------------------------------------------------------
    // Method: WritableLogRecord.WritableLogRecord
    //------------------------------------------------------------------------------
    /**
    * Constructor for a new WritableLogRecord. As part of construction, this class 
    * creates the record header sructure. On exit from construction, the buffers
    * byte cursor will be positioned on the very next byte after the header, ready
    * for other recovery log components to write the record content.
    *
    * @param buffer The target buffer.
    * @param sequenceNumber The sequence number of this new log record.
    * @param length The length of the data that other recovery log components will
    *               write to the record.
    */
    protected WriteableLogRecord(ByteBuffer buffer, long sequenceNumber, int length, int absolutePosition)
    {
        super(buffer, absolutePosition);
        
        if (tc.isEntryEnabled()) Tr.entry(tc, "WriteableLogRecord", new Object[]{buffer, new Long(sequenceNumber), new Integer(length), new Integer(absolutePosition)});
        
        _buffer.put(RECORD_MAGIC_NUMBER);
        _buffer.putLong(sequenceNumber);
        _buffer.putInt(length);        
        
        _sequenceNumber = sequenceNumber;        
                                      
        if (tc.isEntryEnabled()) Tr.exit(tc, "WriteableLogRecord", this);
    }

    //------------------------------------------------------------------------------
    // Method: WritableLogRecord.put
    //------------------------------------------------------------------------------
    /**
    * Setter method used to write bytes.length bytes from the buffer into the mapped
    * byte buffer at the current byte cursor position. The byte cursor is advanced by 
    * bytes.length.
    *
    * @param bytes The source byte array from which the bytes will be written.
    */
    protected void put(byte[] bytes)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "put", new Object[] {RLSUtils.toHexString(bytes, RLSUtils.MAX_DISPLAY_BYTES), this});

        if (tc.isDebugEnabled()) Tr.debug(tc, "Writing at position " + _buffer.position());
        
        _buffer.put(bytes);

        if (tc.isEntryEnabled()) Tr.exit(tc, "put");
    }

    //------------------------------------------------------------------------------
    // Method: WritableLogRecord.putInt
    //------------------------------------------------------------------------------
    /**
    * Setter method used to write an integer to the mapped byte buffer at the current
    * byte cursor position. The byte cursor is advanced by the size of an integer.
    *
    * @param data The int value to be written.
    */
    protected void putInt(int data)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "putInt", new Object[] { this, new Integer(data)});

        if (tc.isDebugEnabled()) Tr.debug(tc, "Writing at position " + _buffer.position());
        
        _buffer.putInt(data);

        if (tc.isEntryEnabled()) Tr.exit(tc, "putInt");
    }

    //------------------------------------------------------------------------------
    // Method: WritableLogRecord.putLong
    //------------------------------------------------------------------------------
    /**
    * Setter method used to write a long to the mapped byte buffer at the current
    * byte cursor position. The byte cursor is advanced by the size of a long.
    *
    * @param data The long value to be written.
    */
    protected void putLong(long data)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "putLong", new Object[] { this, new Long(data)});

        if (tc.isDebugEnabled()) Tr.debug(tc, "Writing at position " + _buffer.position());
        _buffer.putLong(data);

        if (tc.isEntryEnabled()) Tr.exit(tc, "putLong");
    }

    //------------------------------------------------------------------------------
    // Method: WritableLogRecord.putShort
    //------------------------------------------------------------------------------
    /**
    * Setter method used to write a short to the mapped byte buffer at the current
    * byte cursor position. The byte cursor is advanced by the size of a short.
    *
    * @param data The short value to be written.
    */
    protected void putShort(short data)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "putShort", new Object[] { this, new Short(data)});

        if (tc.isDebugEnabled()) Tr.debug(tc, "Writing at position " + _buffer.position());
        _buffer.putShort(data);

        if (tc.isEntryEnabled()) Tr.exit(tc, "putShort");
    }

    //------------------------------------------------------------------------------
    // Method: WritableLogRecord.putBoolean
    //------------------------------------------------------------------------------
    /**
    * Setter method used to write a boolean to the mapped byte buffer at the current
    * byte cursor position. The byte cursor is advanced by the size of a boolean.
    *
    * @param data The boolean value to be written.
    */
    protected void putBoolean(boolean data)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "putBoolean", new Object[] { this, new Boolean(data)});

        if (tc.isDebugEnabled()) Tr.debug(tc, "Writing at position " + _buffer.position());
        _buffer.put(data ? TRUE : FALSE);

        if (tc.isEntryEnabled()) Tr.exit(tc, "putBoolean");
    }

    //------------------------------------------------------------------------------
    // Method: WritableLogRecord.close
    //------------------------------------------------------------------------------
    /**
    * Close the WritableLogRecord. Creates the record tail sructure. On exit from
    * this method, the buffers byte cursor will be positioned on the very next byte
    * after the log record. No further access to this instance is permitted after
    * this call has been issued.
    */
    protected void close()
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "close", this);                
        
        _buffer.putLong(_sequenceNumber);
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "close");
    }
}
