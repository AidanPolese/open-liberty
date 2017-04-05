/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jndi;

import javax.naming.StringRefAddr;

import com.ibm.websphere.crypto.PasswordUtil;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.websphere.ras.annotation.Trivial;

public class WSEncryptedStringRefAddr extends StringRefAddr {
    /**  */
    private static final long serialVersionUID = -9121266108095804314L;
    private static final TraceComponent tc = Tr.register(WSEncryptedStringRefAddr.class);

    public WSEncryptedStringRefAddr(String addrType, String addr) {
        super(addrType, addr);
    }

    @Override
    @Sensitive
    public Object getContent() {
        String value = (String) super.getContent();
        if (PasswordUtil.isEncrypted(value)) {
            try {
                value = PasswordUtil.decode(value);
            } catch (Exception e) {
                Tr.error(tc, "jndi.decode.failed", value, e);
                // when the exception is caught, the original value will be returend.
            }
        }
        return value;
    }

    /**
     * In order to avoid displaying the plain text string.
     */

    @Override
    @Trivial
    public String toString() {
        return "Type: " + getType() + "\nContent: " + super.getContent() + "\n";
    }
}