// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.srt.http;

/*
 * @(#)HttpDate.java	1.11 97/07/17
 * 
 * Copyright (c) 1997 Sun Microsystems, Inc. All Rights Reserved.
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
 * This class can be used to efficiently parse and write an RFC 1123
 * formatted date in an HTTP message header.
 *
 * @version	1.11, 07/17/97
 * 
 * @deprecated
 *  WebSphere Application Server development should no longer be using this class
 *  since moving to the JDK classes
 */

import java.io.IOException;
import java.io.OutputStream;


public class HttpDate extends Ascii
{
	/** Seconds, 0-based */
	protected int sec;

	/** Minutes, 0-based */
	protected int min;

	/** Hours, 0-based */
	protected int hour;

	/** Day of the month, 1-based */
	protected int mday;

	/** Month, 0-based */
	protected int mon;

	/** Years since 1900 */
	protected int year;

	/** Weekday: Sun = 0, Sat = 6 */
	protected int wday;

	/**
	 * Days of the week.
	 */
	protected static final byte[][] days =
		{ toBytes("Sunday"), toBytes("Monday"), toBytes("Tuesday"), toBytes("Wednesday"), toBytes("Thursday"), toBytes("Friday"), toBytes("Saturday")};

	/**
	 * Months of the year.
	 */
	protected static final byte[][] months =
		{
			toBytes("Jan"),
			toBytes("Feb"),
			toBytes("Mar"),
			toBytes("Apr"),
			toBytes("May"),
			toBytes("Jun"),
			toBytes("Jul"),
			toBytes("Aug"),
			toBytes("Sep"),
			toBytes("Oct"),
			toBytes("Nov"),
			toBytes("Dec")};

	/**
	 * Number of days in the month.
	 */
	protected static byte[] daysInMonth = { 31, 28, 31, /* Jan, Feb, Mar */
		30, 31, 30, /* Apr, May, Jun */
		31, 31, 30, /* Jul, Aug, Sep */
		31, 30, 31 /* Oct, Nov, Dec */
	};

	/**
	 * Number of days in the month for a leap year.
	 */
	protected static byte[] daysInMonthLeap = { 31, 29, 31, /* Jan, Feb, Mar */
		30, 31, 30, /* Apr, May, Jun */
		31, 31, 30, /* Jul, Aug, Sep */
		31, 30, 31 /* Oct, Nov, Dec */
	};

	/**
	 * Number of days in a year before a given month.
	 */
	protected static short[] daysBeforeMonth = { 0, 31, 59, /* Jan, Feb, Mar */
		90, 120, 151, /* Apr, May, Jun */
		181, 212, 243, /* Jul, Aug, Sep */
		273, 304, 334, /* Oct, Nov, Dec */
		365 };

	/**
	 * Number of days in a leap year before a given month.
	 */
	protected static short[] daysBeforeMonthLeap = { 0, 31, 60, /* Jan, Feb, Mar */
		91, 121, 152, /* Apr, May, Jun */
		182, 213, 244, /* Jul, Aug, Sep */
		274, 305, 335, /* Oct, Nov, Dec */
		366 };

	/* Example RFC 1123 format date string */
	private static final String DATESTR = "Sun, 06 Nov 1994 08:49:37 GMT";

	/**
	 * Length of RFC 1123 format date string as written by getBytes().
	 */
	public static final int DATELEN = DATESTR.length();

	protected final static int DAYS_PER_YEAR = 365;
	protected final static int DAYS_PER_LEAP = DAYS_PER_YEAR * 4 + 1;
	protected final static int SECS_PER_MIN = 60;
	protected final static int SECS_PER_HOUR = SECS_PER_MIN * 60;
	protected final static int SECS_PER_DAY = SECS_PER_HOUR * 24;
	protected final static int BASE_DAY_OF_WEEK = 4; // 01-01-70 was a Thursday

	/**
	 * Creates a new HttpDate object.
	 */
	public HttpDate()
	{
	}
	/**
	 * Creates a new HttpDate object using the specified time.
	 * @param ms the time in milliseconds since the epoch
	 */
	//Removed by spike....can't use when pooled
	public HttpDate(long ms)
	{
		setTime(ms);
	}
	/**
	 * Returns number of days before the specified month of the specified
	 * year.
	 * @param mon the month, 0-based
	 * @param year the year since 1900
	 */
	protected static int daysBeforeMonth(int mon, int year)
	{
		return isLeapYear(year) ? daysBeforeMonthLeap[mon] : daysBeforeMonth[mon];
	}
	/**
	 * Returns number of days in the specified month of the specified
	 * year.
	 * @param mon the month, 0-based
	 * @param year the year since 1900
	 */
	protected static int daysInMonth(int mon, int year)
	{
		return isLeapYear(year) ? daysInMonthLeap[mon] : daysInMonth[mon];
	}
	/**
	 * Writes an RFC 1123 formatted date to the specified subarray of bytes.
	 * @param b the byte array
	 * @param off the offset of the bytes
	 * @param len the length of the bytes
	 * @return the number of bytes returned
	 * @exception IllegalArgumentException if len < DATELEN
	 */
	public int getBytes(byte[] b, int off, int len)
	{
		if (len < DATELEN)
		{
			throw new IllegalArgumentException("array too small");
		}
		byte[] t = days[wday];
		b[off++] = t[0];
		b[off++] = t[1];
		b[off++] = t[2];
		b[off++] = (byte) ',';
		b[off++] = (byte) ' ';
		b[off++] = (byte) ('0' + (mday / 10));
		b[off++] = (byte) ('0' + (mday % 10));
		b[off++] = (byte) ' ';
		t = months[mon];
		b[off++] = t[0];
		b[off++] = t[1];
		b[off++] = t[2];
		b[off++] = (byte) ' ';
		int y = year + 1900;
		b[off++] = (byte) ('0' + y / 1000);
		b[off++] = (byte) ('0' + (y %= 1000) / 100);
		b[off++] = (byte) ('0' + (y %= 100) / 10);
		b[off++] = (byte) ('0' + y % 10);
		b[off++] = (byte) ' ';
		b[off++] = (byte) ('0' + hour / 10);
		b[off++] = (byte) ('0' + hour % 10);
		b[off++] = (byte) ':';
		b[off++] = (byte) ('0' + min / 10);
		b[off++] = (byte) ('0' + min % 10);
		b[off++] = (byte) ':';
		b[off++] = (byte) ('0' + sec / 10);
		b[off++] = (byte) ('0' + sec % 10);
		b[off++] = (byte) ' ';
		b[off++] = (byte) 'G';
		b[off++] = (byte) 'M';
		b[off] = (byte) 'T';
		return DATELEN;
	}
	/**
	 * Get the current time as number of milliseconds since the epoch.
	 * Similar to System.currentTimeMillis(), but with one second resolution.
	 */
	public static long getCurrentTime()
	{
		return System.currentTimeMillis();
	}
	/**
	 * Returns the time as number of milliseconds since the epoch.
	 */
	public long getTime()
	{
		int year = this.year;
		// regular days since 1970
		int days = (year - 70) * DAYS_PER_YEAR;
		// now add the leap year days
		days += (year - 69) / 4;
		// no leap on century years
		days -= (year - 1) / 100;
		// except years evenly divisible by 400
		days += (year + 299) / 400;
		// now, add the days elapsed in this year so far
		days += daysBeforeMonth(mon, year) + mday - 1;
		// return number of ms since Jan 1, 1970
		return (sec + 60 * (min + 60 * (hour + 24 * days))) * 1000L;
	}
	/**
	 * Returns true if the specified year is a leap year.
	 * @param year the year
	 */
	protected static boolean isLeapYear(int year)
	{
		return (year & 3) == 0 && (year % 100 != 0 || (year + 300) % 400 != 0);
	}
	/**
	 * Parses an RFC 1123, RFC 1036, or ANSI C asctime() format date string
	 * from the specified byte array.
	 * @param b the bytes to parse
	 * @param off the start offset of the bytes
	 * @param len the length of the bytes
	 * @exception IllegalArgumentException if the date format was invalid
	 */
	public void parse(byte[] b, int off, int len)
	{
		try
		{
			int end = off + len;
			int c, n;
			// Parse day of week
			off = parseDay(b, off);
			while (isWhite(c = b[off++]));
			if (isDigit(c))
			{ // RFC 1123 or RFC 1036 format
				// Parse day of month
				n = c - '0';
				while (isDigit(c = b[off++]))
				{
					n = n * 10 + c - '0';
				}
				mday = n;
				if (isWhite(c))
				{
					while (isWhite(c = b[off++]));
				}
				else if (c == '-')
				{
					c = b[off++];
				}
				else
				{
					throw new IllegalArgumentException();
				}
				off = parseMonth(c, b, off);
				if (isWhite(c = b[off++]))
				{
					while (isWhite(c = b[off++]));
				}
				else if (c == '-')
				{
					c = b[off++];
				}
				else
				{
					throw new IllegalArgumentException();
				}
				// Parse year
				if (!isDigit(c))
				{
					throw new IllegalArgumentException();
				}
				n = c - '0';
				while (isDigit(c = b[off++]))
				{
					n = n * 10 + c - '0';
				}
				if (n < 100)
				{
					n += 1900;
				}
				if (mday > daysInMonth(mon, n))
				{
					throw new IllegalArgumentException();
				}
				year = n - 1900;
				// Parse time of day
				off = parseTime(b, off);
				while (isWhite(c = b[off++]));
				if (toLower(c) != 'g' || toLower(b[off++]) != 'm' || toLower(b[off++]) != 't')
				{
					throw new IllegalArgumentException();
				}
			}
			else
			{ // ANSI C's asctime() format
				// Parse month
				off = parseMonth(c, b, off);
				if (!isWhite(c = b[off++]))
				{
					throw new IllegalArgumentException();
				}
				while (isWhite(c = b[off++]));
				// Parse day of month
				if (!isDigit(c))
				{
					throw new IllegalArgumentException();
				}
				n = c - '0';
				while (isDigit(c = b[off++]))
				{
					n = n * 10 + c - '0';
				}
				mday = n;
				// Parse time of day
				off = parseTime(b, off);
				while (isWhite(c = b[off++]));
				// Parse year (need to check for end of line here)
				n = c - '0';
				while (off < end && isDigit(c = b[off++]))
				{
					n = n * 10 + c - '0';
				}
				if (n < 1900 || mday > daysInMonth(mon, n))
				{
					throw new IllegalArgumentException();
				}
				year = n - 1900;
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			com.ibm.ws.ffdc.FFDCFilter.processException(e, "com.ibm.ws.webcontainer.srt.http.HttpDate.parse", "345", this);
			throw new IllegalArgumentException();
		}
	}
	/**
	 * Parses an RFC 1123, RFC 1036, or ANSI C asctime() format date string
	 * from the specified string.
	 * @param s the string to parse
	 * @exception IllegalArgumentException if the date format was invalid
	 */
	public void parse(String s)
	{
		byte[] b = toBytes(s);
		parse(b, 0, b.length);
	}
	/*
	 * Parses day of week from specified byte array. Returns offset of byte
	 * following last byte parsed or -1 if the date format was invalid.
	 */
	private int parseDay(byte[] b, int off)
	{
		int c, n;
		while (isWhite(c = b[off++]));
		n = toLower(c);
		n = (n << 8) | toLower(b[off++]);
		n = (n << 8) | toLower(b[off++]);
		switch (n)
		{
			case 0x73756e : // Sun
				wday = 0;
				break;
			case 0x6d6f6e : // Mon
				wday = 1;
				break;
			case 0x747565 : // Tue
				wday = 2;
				break;
			case 0x776564 : // Wed
				wday = 3;
				break;
			case 0x746875 : // Thu
				wday = 4;
				break;
			case 0x667269 : // Fri
				wday = 5;
				break;
			case 0x736174 : // Sat
				wday = 6;
				break;
			default :
				throw new IllegalArgumentException();
		}
		if (isAlpha(c = b[off++]))
		{
			byte[] day = days[wday];
			int len = day.length;
			for (int i = 3; i < len; i++)
			{
				if (toLower(c) != day[i])
				{
					throw new IllegalArgumentException();
				}
				c = b[off++];
			}
		}
		if (!isWhite(c) && c != ',')
		{
			throw new IllegalArgumentException();
		}
		return off;
	}
	/*
	 * Parses and sets month from specified byte array. Returns offset of
	 * byte following last byte parsed.
	 */
	private int parseMonth(int c, byte[] b, int off)
	{
		int n = toLower(c);
		n = (n << 8) | toLower(b[off++]);
		n = (n << 8) | toLower(b[off++]);
		switch (n)
		{
			case 0x6a616e : // Jan
				mon = 0;
				break;
			case 0x666562 : // Feb
				mon = 1;
				break;
			case 0x6d6172 : // Mar
				mon = 2;
				break;
			case 0x617072 : // Apr
				mon = 3;
				break;
			case 0x6d6179 : // May
				mon = 4;
				break;
			case 0x6a756e : // Jun
				mon = 5;
				break;
			case 0x6a756c : // Jul
				mon = 6;
				break;
			case 0x617567 : // Aug
				mon = 7;
				break;
			case 0x736570 : // Sep
				mon = 8;
				break;
			case 0x6f6374 : // Oct
				mon = 9;
				break;
			case 0x6e6f76 : // Nov
				mon = 10;
				break;
			case 0x646563 : // Dec
				mon = 11;
				break;
			default :
				throw new IllegalArgumentException();
		}
		return off;
	}
	/*
	 * Parses and sets time of day from specified byte array. Returns offset
	 * of byte following last byte parsed or -1 if the date format was invalid.
	 */
	private int parseTime(byte[] b, int off)
	{
		int c, n;
		while (isWhite(c = b[off++]));
		if (isDigit(c))
		{
			n = c - '0';
			while (isDigit(c = b[off++]))
			{
				n = n * 10 + c - '0';
			}
			if (n < 24 && c == ':' && isDigit(c = b[off++]))
			{
				hour = n;
				n = c - '0';
				while (isDigit(c = b[off++]))
				{
					n = n * 10 + c - '0';
				}
				if (n < 60 && c == ':' && isDigit(c = b[off++]))
				{
					min = n;
					n = c - '0';
					while (isDigit(c = b[off++]))
					{
						n = n * 10 + c - '0';
					}
					if (n < 60 && c == ' ')
					{
						sec = n;
						return off;
					}
				}
			}
		}
		throw new IllegalArgumentException();
	}
	/**
	 * Sets this HttpDate to the current time.
	 */
	public void setTime()
	{
		setTime(getCurrentTime());
	}
	/**
	 * Sets this HttpDate to the specified time.
	 * @param ms the time in milliseconds since the epoch
	 */
	public void setTime(long ms)
	{
		// seconds since 1970
		int sec = (int) (ms / 1000);
		// days since 1970
		int days = sec / SECS_PER_DAY;
		// hours since midnight
		sec -= days * SECS_PER_DAY;
		hour = sec / SECS_PER_HOUR;
		// minutes after the hour
		sec -= hour * SECS_PER_HOUR;
		min = sec / SECS_PER_MIN;
		// seconds after the minute
		this.sec = sec - min * SECS_PER_MIN;
		// day of the week
		wday = (days + BASE_DAY_OF_WEEK) % 7;
		// number of 4-year (leap) periods since 1970
		int leaps = days / DAYS_PER_LEAP;
		// number of days in this period
		days -= leaps * DAYS_PER_LEAP;
		// determine which year in this period
		boolean isLeap = false;
		int year = leaps * 4 + 70; // 1970, 1974, 1978, ...
		if (days >= DAYS_PER_YEAR)
		{
			year++; // 1971, 1975, 1976, ...
			days -= DAYS_PER_YEAR;
			if (days >= DAYS_PER_YEAR)
			{
				year++; // 1972, 1976, 1980, ... (leap)
				days -= DAYS_PER_YEAR;
				if (days >= DAYS_PER_YEAR + 1)
				{
					year++; // 1973, 1977, 1981, ...
					days -= DAYS_PER_YEAR + 1;
				}
				else
				{
					isLeap = true; // is a leap year
				}
			}
		}
		this.year = year;
		// months since January
		short[] mdays = isLeap ? daysBeforeMonthLeap : daysBeforeMonth;
		int mon;
		for (mon = 1; mdays[mon] <= days; mon++);
		this.mon = --mon;
		// day of the month
		mday = days - mdays[mon] + 1;
	}
	/**
	 * Converts the specified string to an array of ascii bytes.
	 * @param s the string
	 */
	protected static byte[] toBytes(String s)
	{
		//byte[] b = new byte[s.length()];
		//s.getBytes(0, b.length, b, 0);
		byte[] b = s.getBytes();
		return b;
	}
	/**
	 * Returns a string representation of the date.
	 */
	public String toString()
	{
		byte[] b = new byte[DATELEN];
		return new String(b, 0, 0, getBytes(b, 0, b.length));
	}
	/**
	 * Writes an RFC 1123 formatted date to the specified output stream.
	 * @param out the output stream
	 * @exception IOException if an I/O error has occurred
	 */
	public void write(OutputStream out) throws IOException
	{
		byte[] b = new byte[DATELEN];
		out.write(b, 0, getBytes(b, 0, b.length));
	}

	public void reset()
	{
		sec = 0;
		min = 0;
		hour = 0;
		mday = 0;
		mon = 0;
		year = 0;
		wday = 0;
	}
}
