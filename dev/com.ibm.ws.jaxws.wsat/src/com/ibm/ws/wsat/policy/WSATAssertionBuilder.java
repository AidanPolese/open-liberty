/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.wsat.policy;

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.cxf.ws.policy.builder.primitive.PrimitiveAssertion;
import org.apache.neethi.Assertion;
import org.apache.neethi.AssertionBuilderFactory;
import org.apache.neethi.Policy;
import org.apache.neethi.builders.AssertionBuilder;
import org.apache.neethi.builders.PolicyContainingPrimitiveAssertion;
import org.apache.neethi.builders.xml.XMLPrimitiveAssertionBuilder;
import org.w3c.dom.Element;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxws.wsat.Constants;

/**
 *
 */
public class WSATAssertionBuilder implements AssertionBuilder<Element> {
    private static final TraceComponent tc = Tr.register(WSATAssertionBuilder.class, Constants.TRACE_GROUP, null);

    @Override
    public QName[] getKnownElements() {
        return new QName[] { Constants.AT_ASSERTION_QNAME };
    }

    @Override
    public Assertion build(Element elem, AssertionBuilderFactory factory)
                    throws IllegalArgumentException {

        String localName = elem.getLocalName();
        QName qn = new QName(elem.getNamespaceURI(), localName);

        if (Constants.AT_ASSERTION_QNAME.equals(qn)) {
            Assertion nap = new XMLPrimitiveAssertionBuilder() {
                @Override
                public Assertion newPrimitiveAssertion(Element element, Map<QName, String> mp) {
                    if (isIgnorable(element)) {
                        throw new RuntimeException("WS-AT does not accept Ignorable attribute is TRUE");
                    }
                    return new PrimitiveAssertion(Constants.AT_ASSERTION_QNAME,
                                    isOptional(element), isIgnorable(element), mp);
                }

                @Override
                public Assertion newPolicyContainingAssertion(Element element,
                                                              Map<QName, String> mp,
                                                              Policy policy) {
                    if (isIgnorable(element)) {
                        throw new RuntimeException("WS-AT does not accept Ignorable attribute is TRUE");
                    }
                    return new PolicyContainingPrimitiveAssertion(
                                    Constants.AT_ASSERTION_QNAME,
                                    isOptional(element), isIgnorable(element),
                                    mp,
                                    policy);
                }
            }.build(elem, factory);
            return nap;
        }
        return null;
    }

}
