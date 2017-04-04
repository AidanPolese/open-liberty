package com.ibm.tx.util;
//
// COMPONENT_NAME: WAS.transactions                                                                      
//                                                                                                
// ORIGINS: 27                                                                                          
//                                                                                                 
// IBM Confidential OCO Source Material                                                                  
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2008
// The source code for this program is not published or otherwise divested                               
// of its trade secrets, irrespective of what has been deposited with the                                
// U.S. Copyright Office.                                                                                
//                                                                                                 
// %Z% %I% %W% %G% %U% [%H% %T%]                                                                         
//                                                                                                 
// DESCRIPTION:                                                                                         
//                                                                                                 
// Change History:                                                                                      
//                                                                                                 
//                                                                                                 
// Date      Programmer    Defect   Description                                                         
// --------  ----------    ------   -----------
// 08-07-17  johawkes      536926   Created

public class Utils
{
    /**
     * Converts a byte array to a string.
     */
    public static String toString(byte[] b) {
       StringBuffer result = new StringBuffer(b.length);
       for (int i = 0; i < b.length; i++)
          result.append((char) b[i]);
       return (result.toString());
    }

    public static byte[] byteArray(String s) {
       return byteArray(s, false);
    }
    
    public static byte[] byteArray(String s, boolean keepBothBytes) {
       byte[] result = new byte[s.length() * (keepBothBytes ? 2 : 1)];
       for (int i = 0; i < result.length; i++)
          result[i] = keepBothBytes ? (byte) (s.charAt(i / 2) >> (i & 1) * 8) : (byte) (s.charAt(i));
       return result;
    }
}