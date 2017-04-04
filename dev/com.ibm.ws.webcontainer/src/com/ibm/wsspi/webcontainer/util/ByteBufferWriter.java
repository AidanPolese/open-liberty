// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//
//
//CHANGE HISTORY
//Defect        Date        Modified By     Description
//--------------------------------------------------------------------------------------
// 485661       12/10/07    mmolden         Do not release corrupted buffers
package com.ibm.wsspi.webcontainer.util;

import com.ibm.wsspi.bytebuffer.WsByteBuffer;

public interface ByteBufferWriter
{
  public void writeByteBuffer(WsByteBuffer[] buf);
}
