// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.ibm.ws.common.internal.encoder.Base64Coder;


/**
 * @author chow
 * 
 * This class is a MessageDigest program which get MessageDigest object to do the digest calculation. 
 */
public class MessageDigestUtility {
	
	/**
	 * Create MessageDigest based on algorithm type.
	 * @param Algorithm
	 * @return MessageDigest for specified algorithm type
	 * @throws NoSuchAlgorithmException
	 */
	public static MessageDigest createMessageDigest(String Algorithm) throws NoSuchAlgorithmException {
		return MessageDigest.getInstance(Algorithm);
	}

	/**
	 * Calculate the digest specified by byte array of data
	 * @param messageDigest
	 * @param data
	 * @return digest in string with base64 encoding.
	 */
    public static String processMessageDigestForData(MessageDigest messageDigest, byte[] data) {
    	String output = ""; //$NON-NLS-1$
        if (messageDigest != null) {
            // Get the digest for the given data
        	messageDigest.update(data);
            byte[] digest = messageDigest.digest();
            output = Base64Coder.encode(digest);
        }
        return output;
    }
    
	/**
	 * Calculate the digest specified by integer of data
	 * @param messageDigest
	 * @param data
	 * @return digest in string with base64 encoding.
	 */
    public static String processMessageDigestForData(MessageDigest messageDigest, int data) {
    	return processMessageDigestForData(messageDigest, intToByteArray(data));
    }
    
    /**
     * Convert from an integer to byte array
     * @param value
     * @return byte array represented integer value.
     */
    public static final byte[] intToByteArray(int value) {
    	return new byte[]{(byte)(value >>> 24), (byte)(value >> 16 & 0xff), (byte)(value >> 8 & 0xff), (byte)(value & 0xff)};
  	}
}
