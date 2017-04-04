// 1.2, 4/14/08
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.wsspi.cache;

/**
 * This interface is used by CacheEntry to indicate that the value implements the GenerateContents() method. 
 * @ibm-spi 
 * @since WAS7.0
 */
public interface GenerateContents {
    /**
     * Returns the contents of this object in byte array.
     * 
     * @return byte[] the byte array of the object
     */
	public byte[] generateContents();
}
