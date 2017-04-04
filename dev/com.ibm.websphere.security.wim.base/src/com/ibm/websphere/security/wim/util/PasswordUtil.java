/************** Begin Copyright - Do not add comments here **************
 *
 *  
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 * 
 * Change History:
 * 
 * Tag          Person      Defect/Feature              Comments
 * -------       ------      --------------      --------------------------------------------------
 *              ankit_jain	92798	         Change the NLS formatting method for exception message
 * 07/10/2013   ankit_jain      105090           Mask password for all classes in trace for VMM  
 */
package com.ibm.websphere.security.wim.util;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.websphere.security.wim.ras.WIMMessageHelper;
import com.ibm.websphere.security.wim.ras.WIMMessageKey;
import com.ibm.wsspi.security.wim.exception.WIMSystemException;

/**
 * The utility which provides helper functions related with password.
 * 
 * @author Ankit Jain
 */
public class PasswordUtil
{
    /**
     * IBM Copyright string.
     */
    static final String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_SHORT_2012;

    private static final TraceComponent tc = Tr.register(PasswordUtil.class);

    /**
     * Gets the byte array of the given password from uisng UTF-8 encoding.
     * 
     * @param password the string of the password to encode.
     * @return the byte array representation of the text string
     */
    @Sensitive
    public static byte[] getByteArrayPassword(@Sensitive String password) throws WIMSystemException
    {
        String METHODNAME = "getByteArrayPassword";
        try {
            if (password != null) {
                return password.getBytes("UTF-8");
            }
            else {
                return null;
            }
        } catch (java.io.UnsupportedEncodingException e) {
            if (tc.isErrorEnabled()) {
                Tr.error(tc, WIMMessageKey.GENERIC, WIMMessageHelper.generateMsgParms(e.toString()));
            }
            throw new WIMSystemException(WIMMessageKey.GENERIC, Tr.formatMessage(
                                                                                 tc,
                                                                                 WIMMessageKey.GENERIC,
                                                                                 WIMMessageHelper.generateMsgParms(e.toString())));
        }
    }

    /**
     * Erase the password byte array by setting its elements to zero.
     * For security reason, all password byte array should be erased before the references to it is dropped.
     * 
     * @param pwdBytes The password bypte array to be erased.
     */
    @Trivial
    public static void erasePassword(@Sensitive byte[] pwdBytes)
    {
        if (pwdBytes != null) {
            for (int i = 0; i < pwdBytes.length; i++) {
                pwdBytes[i] = 0x00;
            }
        }

    }
}
