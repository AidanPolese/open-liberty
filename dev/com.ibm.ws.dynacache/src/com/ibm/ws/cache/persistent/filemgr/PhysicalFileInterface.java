// 1.4, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.persistent.filemgr;

import java.io.IOException;

interface PhysicalFileInterface
{

    public String filename();
    
    public long length() throws IOException;

    public void close() throws IOException;

    public void flush() throws IOException;

    public int read() throws IOException;

    public int read(byte[] v) throws IOException;

    public int read(byte[] v, int off, int len) throws IOException;

    public int readInt() throws IOException;

    public long readLong() throws IOException;

    public short readShort() throws IOException;

    public void seek(long loc) throws IOException;

    public void write(byte[] v) throws IOException;

    public void write(byte[] v, int off, int len) throws IOException;

    public void write(int v) throws IOException;
    
    public void writeInt(int v) throws IOException;

    public void writeLong(long v) throws IOException;

    public void writeShort(short v) throws IOException;

}





