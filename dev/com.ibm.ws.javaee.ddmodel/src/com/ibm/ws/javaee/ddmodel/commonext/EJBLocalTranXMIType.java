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
package com.ibm.ws.javaee.ddmodel.commonext;

import com.ibm.ws.javaee.dd.commonext.LocalTransaction;
import com.ibm.ws.javaee.ddmodel.DDParser;

/**
 * Manual implementation of the localTran XMI element.
 */
public class EJBLocalTranXMIType extends LocalTransactionType {
    private enum Resolver {
        BEAN(ResolverEnum.APPLICATION),
        CONTAINER(ResolverEnum.CONTAINER_AT_BOUNDARY);

        final ResolverEnum value;

        private Resolver(ResolverEnum value) {
            this.value = value;
        }
    }

    public EJBLocalTranXMIType() {
        super(true);
    }

    @Override
    public boolean handleAttribute(DDParser parser, String nsURI, String localName, int index) throws DDParser.ParseException {
        if (nsURI == null) {
            if ("boundary".equals(localName)) {
                this.boundary = parser.parseEnumAttributeValue(index, LocalTransaction.BoundaryEnum.class);
                return true;
            }
            if ("resolver".equals(localName)) {
                this.resolver = parser.parseEnumAttributeValue(index, Resolver.class).value;
                return true;
            }
            if ("unresolvedAction".equals(localName)) {
                this.unresolved_action = parser.parseEnumAttributeValue(index, LocalTransaction.UnresolvedActionEnum.class);
                return true;
            }
        }

        // Do not delegate to super.handleAttribute.
        return false;
    }
}
