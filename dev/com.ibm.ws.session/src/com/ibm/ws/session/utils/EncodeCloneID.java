/*COPYRIGHT_START***********************************************************
 *
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *   IBM DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE, INCLUDING
 *   ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE. IN NO EVENT SHALL IBM BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 *   CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF
 *   USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
 *   OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
 *   OR PERFORMANCE OF THIS SOFTWARE.
 *
 *  @(#) 1.1 SERV1/ws/code/session.store/src/com/ibm/ws/session/utils/EncodeCloneID.java, WAS.session, WASX.SERV1, ff1146.05 10/13/06 16:01:53 [11/21/11 18:33:10]
 *
 * @(#)file   EncodeCloneID.java
 * @(#)version   1.1
 * @(#)date      10/13/06
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.ws.session.utils;

/**
 * This class has methods which return a String representation of the CloneId passed in.
 * The cloneId is so that we can have affinity with regard to sessions
 * 
 * @ibm-private-in-use
 */
public class EncodeCloneID {

    private static final int radix = 32;

    //Tries to get string representation of the number to base 32. 
    //If unsuccessful returns the original as string
    public static String encodeLong(long val) {
        String str = null;
        try {
            str = Long.toString(val, radix);
            return str;
        } catch (Throwable th) {
            com.ibm.ws.ffdc.FFDCFilter.processException(th, "com.ibm.ws.session.utils.EncodeCloneID.encodeLong", "39", "" + val);
        }
        return Long.toString(val);
    }

    //Tries to get string representation of the number to base 32. 
    //If unsuccessful returns the original as string
    public static String encodeString(String str) {
        if (str == null)
            return str;
        if (str.equals("-1"))
            return str;
        try {
            Long lo = new Long(str);
            return encodeLong(lo.longValue());
        } catch (NumberFormatException nfe) {
            // do nothing -- this is expected on zOS, results in string being returned unchanged  242544
        } catch (Throwable th) {
            com.ibm.ws.ffdc.FFDCFilter.processException(th, "com.ibm.ws.session.utils.EncodeCloneID.encodeString", "56", str);
        }
        return str;
    }

}
