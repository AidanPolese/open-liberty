/************** Begin Copyright - Do not add comments here **************
 *
 *
 * IBM Confidential OCO Source Material
 * Virtual Member Manager (C) COPYRIGHT International Business Machines Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 */
package com.ibm.ws.security.wim.env.was;

import com.ibm.websphere.crypto.PasswordUtil;
import com.ibm.ws.security.wim.env.IEncryptionUtil;

/**
 * Encryption utility for WAS environment
 */
public class EncryptionUtilImpl implements IEncryptionUtil {

    public EncryptionUtilImpl() {}

    @Override
    public String decode(String encodedValue) {
        return PasswordUtil.passwordDecode(encodedValue);
    }

    @Override
    public String encode(String decodedValue) {
        return PasswordUtil.passwordEncode(decodedValue);
    }
}
