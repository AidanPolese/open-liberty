// @(#) 1.2 SERV1/ws/code/ffdc.bundle/src/com/ibm/ffdc/util/formatting/DateFormatProvider.java, WAS.ffdc.bundle, WASX.SERV1 11/15/07 08:01:41 [2/22/12 09:30:26]
/**
 * COMPONENT_NAME: WAS.ffdc
 *
 * ORIGINS: 27         (used for IBM originated files)
 *
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * DESCRIPTION:
 *
 * Change History:
 *
 * Reason        Version  Date        User id     Description
 * ----------------------------------------------------------------------------
 * li4366.08     7.0      10/09/2007  mcasile     Original code
 * 477704.5      7.0      11/15/2007  mcasile     Insert appropriate copyright/oco info
 */
package com.ibm.ws.logging.internal.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateFormatProvider {

    /**
     * Return a format string that will produce a reasonable standard way for
     * formatting time (but still using the current locale)
     * 
     * @return The format string
     */
    public static DateFormat getDateFormat() {
        String pattern;
        int patternLength;
        int endOfSecsIndex;
        // Retrieve a standard Java DateFormat object with desired format.
        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
        if (formatter instanceof SimpleDateFormat) {
            // Retrieve the pattern from the formatter, since we will need to
            // modify it.
            SimpleDateFormat sdFormatter = (SimpleDateFormat) formatter;
            pattern = sdFormatter.toPattern();
            // Append milliseconds and timezone after seconds
            patternLength = pattern.length();
            endOfSecsIndex = pattern.lastIndexOf('s') + 1;
            String newPattern = pattern.substring(0, endOfSecsIndex) + ":SSS z";
            if (endOfSecsIndex < patternLength)
                newPattern += pattern.substring(endOfSecsIndex, patternLength);
            // 0-23 hour clock (get rid of any other clock formats and am/pm)
            newPattern = newPattern.replace('h', 'H');
            newPattern = newPattern.replace('K', 'H');
            newPattern = newPattern.replace('k', 'H');
            newPattern = newPattern.replace('a', ' ');
            newPattern = newPattern.trim();
            sdFormatter.applyPattern(newPattern);
            formatter = sdFormatter;
        } else {
            formatter = new SimpleDateFormat("yy.MM.dd HH:mm:ss:SSS z");
        }
        return formatter;
    }
}
