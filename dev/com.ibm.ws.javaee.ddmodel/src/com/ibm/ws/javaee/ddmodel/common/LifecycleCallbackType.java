/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.common;

import com.ibm.ws.javaee.dd.common.LifecycleCallback;
import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.DDParser.ParsableListImplements;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;

/*
 * <xsd:complexType name="lifecycle-callbackType">
 * <xsd:sequence>
 * <xsd:element name="lifecycle-callback-class"
 * type="javaee:fully-qualified-classType"
 * minOccurs="0"/>
 * <xsd:element name="lifecycle-callback-method"
 * type="javaee:java-identifierType"/>
 * </xsd:sequence>
 * </xsd:complexType>
 */
public class LifecycleCallbackType extends DDParser.ElementContentParsable implements LifecycleCallback {

    public static class ListType extends ParsableListImplements<LifecycleCallbackType, LifecycleCallback> {
        @Override
        public LifecycleCallbackType newInstance(DDParser parser) {
            return new LifecycleCallbackType();
        }
    }

    @Override
    public String getClassName() {
        return lifecycle_callback_class.getValue();
    }

    @Override
    public String getMethodName() {
        return lifecycle_callback_method.getValue();
    }

    XSDTokenType lifecycle_callback_class = new XSDTokenType();
    XSDTokenType lifecycle_callback_method = new XSDTokenType();

    @Override
    public boolean handleChild(DDParser parser, String localName) throws ParseException {
        if ("lifecycle-callback-class".equals(localName)) {
            parser.parse(lifecycle_callback_class);
            return true;
        }
        if ("lifecycle-callback-method".equals(localName)) {
            parser.parse(lifecycle_callback_method);
            return true;
        }
        return false;
    }

    @Override
    public void describe(DDParser.Diagnostics diag) {
        diag.describe("lifecycle-callback-class", lifecycle_callback_class);
        diag.describe("lifecycle-callback-method", lifecycle_callback_method);
    }
}