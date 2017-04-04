// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.srt.http;

/*
 * @(#)Ascii.java	1.4 97/03/03
 * 
 * Copyright (c) 1995-1997 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.0
 */

/**
 * This class implements some basic ASCII character handling functions.
 *
 * @version	1.4, 03/03/97
 * @author	David Connelly
 * 
 * @deprecated
 *  WebSphere Application Server development should no longer be using this class
 *  since moving to the JDK classes
 */

public
class Ascii
{
    /*
     * Character translation tables.
     */
    private static final byte[] toUpper = new byte[256];
    private static final byte[] toLower = new byte[256];

    /*
     * Character type tables.
     */
    private static final boolean[] isAlpha = new boolean[256];
    private static final boolean[] isUpper = new boolean[256];
    private static final boolean[] isLower = new boolean[256];
    private static final boolean[] isWhite = new boolean[256];
    private static final boolean[] isDigit = new boolean[256];

    /*
     * Initialize character translation and type tables.
     */
    static {
        for ( int i = 0; i < 256; i++ )
        {
            toUpper[i] = (byte)i;
            toLower[i] = (byte)i;
        }
        for ( int lc = 'a'; lc <= 'z'; lc++ )
        {
            int uc = lc + 'A' - 'a';
            toUpper[lc] = (byte)uc;
            toLower[uc] = (byte)lc;
            isAlpha[lc] = true;
            isAlpha[uc] = true;
            isLower[lc] = true;
            isUpper[uc] = true;
        }
        isWhite[ ' '] = true;
        isWhite['\t'] = true;
        isWhite['\r'] = true;
        isWhite['\n'] = true;
        isWhite['\f'] = true;
        isWhite['\b'] = true;
        for ( int d = '0'; d <= '9'; d++ )
        {
            isDigit[d] = true;
        }
    }

    /**
     * Returns true if the specified ASCII character is upper or lower case.
     */
    public static boolean isAlpha(int c)
    {
        return isAlpha[c & 0xff];
    }
    /**
     * Returns true if the specified ASCII character is a digit.
     */
    public static boolean isDigit(int c)
    {
        return isDigit[c & 0xff];
    }
    /**
     * Returns true if the specified ASCII character is lower case.
     */
    public static boolean isLower(int c)
    {
        return isLower[c & 0xff];
    }
    /**
     * Returns true if the specified ASCII character is upper case.
     */
    public static boolean isUpper(int c)
    {
        return isUpper[c & 0xff];
    }
    /**
     * Returns true if the specified ASCII character is white space.
     */
    public static boolean isWhite(int c)
    {
        return isWhite[c & 0xff];
    }
    /**
     * Parses an unsigned integer from the specified subarray of bytes.
     * @param b the bytes to parse
     * @param off the start offset of the bytes
     * @param len the length of the bytes
     * @exception NumberFormatException if the integer format was invalid
     */
    public static int parseInt(byte[] b, int off, int len)
    throws NumberFormatException
    {
        int c;
        if ( b == null || len <= 0 || !isDigit(c = b[off++]) )
        {
            throw new NumberFormatException();
        }
        int n = c - '0';
        while ( --len > 0 )
        {
            if ( !isDigit(c = b[off++]) )
            {
                throw new NumberFormatException();
            }
            n = n * 10 + c - '0';
        }
        return n;
    }
    /**
     * Returns the lower case equivalent of the specified ASCII character.
     */
    public static int toLower(int c)
    {
        return toLower[c & 0xff] & 0xff;
    }
    /**
     * Returns the upper case equivalent of the specified ASCII character.
     */
    public static int toUpper(int c)
    {
        return toUpper[c & 0xff] & 0xff;
    }
}
