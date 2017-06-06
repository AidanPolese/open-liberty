/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.anno.test.data.sub;

import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlList;

import org.junit.Test;

/**
 *
 */
@InheritAnno(value = { "a", "b" })
public class SubBase {
    @Id
    public String public1 = "SubBase";
    public String public2 = "SubBase";
    protected String protected1 = "SubBase";
    protected String protected2 = "SubBase";
    private final String private1 = "SubBase";
    String package1 = "SubBase";
    String package2 = "SubBase";

    @Test(timeout = 10000)
    public void publicMethod() {}

    public Number publicMethod(int n) {
        return null;
    }

    protected Map<String, String> protectedMethod() {
        return null;
    }

    int packageMethod() {
        return 0;
    }

    protected void annoMethod(int a, String b, @XmlList List<?> c, @XmlAttachmentRef long d) {

    }

    private void privateMethod() {}

}
