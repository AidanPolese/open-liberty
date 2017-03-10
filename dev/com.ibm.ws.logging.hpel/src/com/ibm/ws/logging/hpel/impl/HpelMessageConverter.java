// %Z% %I% %W% %G% %U% [%H% %T%]
/**
 * COMPONENT_NAME: WAS.ras
 *
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2004, 2006, 2010, 2015
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * DESCRIPTION:
 *
 * Change History:
 *
 * Reason      Version   Date        User id     Description
 * ----------------------------------------------------------------------------
 * LIDB2667        6.0   06-11-2004  dbourne     CBE
 * D225693         6.0   09-09-2004  vratnala    CBE Code Review Changes
 * D247062       6.0.1   12-10-2004  dbourne     Add missing mappings
 * LIDB1793-24     7.0   06-03-2005  dbourne     Detach from runtime and make public 
 * 311563	   6.1   04-04-2006  tomasz 	 Updated the message id list
 * D362412	   6.1   04-12-2006  andymc      Added method determineMsgType
 * F017049-27489   8.0   06-16-2010  belyi       Remove references to WAS.
 * 714313          8.0   08-25-2011  belyi       Add back the recognition of IBM4.4.1 message IDs.
 * RTCD51304       8.5    01-16-2011 olteamh     Move to a different package
 * 193542          8.5    11-10-2015 rishim      adding support for alphanumeric messageIDs
 */
package com.ibm.ws.logging.hpel.impl;

/**
 * 
 * The MessageConverter class provides the functions required to convert message IDs used in WAS
 * from the WAS v5 format to the IBM SWG message standard compliant structure available in later WAS releases.
 * 
 */
public class HpelMessageConverter {

    /**
     * Determines if a message starts with IBM SWG message ID.
     * 
     * @param msg the log record message
     * @return substring containing message ID or null if message does not
     *         start with IBM SWG pattern.
     */
    public static String getMessageId(String msg) {
        if (msg == null) {
            return null;
        }
        // check for IBM SWG pattern - "AAAAANNNNS:"
        // 5 chars followed by 4 numbers, followed by one of E/W/I, followed by a colon
        if (msg.length() >= 10) { // 10 chars in pattern 

            // copy first 9 characters into an array
            char[] lm = new char[9];
            msg.getChars(0, 9, lm, 0);

            // match characters in array against AAAAANNNNS: pattern

            if ((lm[8] >= '0') && (lm[8] <= '9')
                && (lm[7] >= '0') && (lm[7] <= '9')
                && (lm[6] >= '0') && (lm[6] <= '9')
                && (lm[5] >= '0') && (lm[5] <= '9')
                && ((lm[4] >= 'A') && (lm[4] <= 'Z') || (lm[4] >= '0') && (lm[4] <= '9'))
                && ((lm[3] >= 'A') && (lm[3] <= 'Z') || (lm[3] >= '0') && (lm[3] <= '9'))
                && ((lm[2] >= 'A') && (lm[2] <= 'Z') || (lm[2] >= '0') && (lm[2] <= '9'))
                && ((lm[1] >= 'A') && (lm[1] <= 'Z') || (lm[1] >= '0') && (lm[1] <= '9'))
                && ((lm[0] >= 'A') && (lm[0] <= 'Z') || (lm[0] >= '0') && (lm[0] <= '9'))) {
                return msg.substring(0, 10);
            }

        }

        // Check for IBM4.4.1 pattern - "AAAANNNNS:"
        // 4 chars followed by 4 numbers, followed by one of E/W/I, followed by a colon
        if (msg.length() >= 9) { // 9 chars in pattern

            // copy first 8 characters into an array
            char[] lm = new char[8];
            msg.getChars(0, 8, lm, 0);

            // match characters in array against AAAAANNNNS: pattern
            if ((lm[7] >= '0') && (lm[7] <= '9')
                && (lm[6] >= '0') && (lm[6] <= '9')
                && (lm[5] >= '0') && (lm[5] <= '9')
                && (lm[4] >= '0') && (lm[4] <= '9')
                && ((lm[3] >= 'A') && (lm[3] <= 'Z') || (lm[3] >= '0') && (lm[3] <= '9'))
                && ((lm[2] >= 'A') && (lm[2] <= 'Z') || (lm[2] >= '0') && (lm[2] <= '9'))
                && ((lm[1] >= 'A') && (lm[1] <= 'Z') || (lm[1] >= '0') && (lm[1] <= '9'))
                && ((lm[0] >= 'A') && (lm[0] <= 'Z') || (lm[0] >= '0') && (lm[0] <= '9'))) {

                // v5
                return msg.substring(0, 9);
            }
        }
        return null;
    }

}
