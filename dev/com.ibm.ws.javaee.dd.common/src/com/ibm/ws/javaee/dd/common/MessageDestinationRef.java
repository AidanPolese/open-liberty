/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.common;

/**
 * Represents &lt;message-destination-ref>.
 */
public interface MessageDestinationRef
                extends ResourceGroup, Describable
{
    /**
     * Represents an unspecified value for {@link #getUsageValue}.
     */
    int USAGE_UNSPECIFIED = -1;

    /**
     * Represents "Consumes" for {@link #getUsageValue}.
     * 
     * @see org.eclipse.jst.j2ee.common.MessageDestinationUsageType#CONSUMES
     */
    int USAGE_CONSUMES = 0;

    /**
     * Represents "Produces" for {@link #getUsageValue}.
     * 
     * @see org.eclipse.jst.j2ee.common.MessageDestinationUsageType#PRODUCES
     */
    int USAGE_PRODUCES = 1;

    /**
     * Represents "ConsumesProduces" for {@link #getUsageValue}.
     * 
     * @see org.eclipse.jst.j2ee.common.MessageDestinationUsageType#CONSUMES_PRODUCES
     */
    int USAGE_CONSUMES_PRODUCES = 2;

    /**
     * @return &lt;message-destination-type>, or null if unspecified
     */
    String getType();

    /**
     * @return &lt;message-destination-type>
     *         <ul>
     *         <li>{@link #USAGE_UNSPECIFIED} if unspecified
     *         <li>{@link #USAGE_CONSUMES} - Consumes
     *         <li>{@link #USAGE_PRODUCES} - Produces
     *         <li>{@link #USAGE_CONSUMES_PRODUCES} - ConsumesProduces
     */
    int getUsageValue();

    /**
     * @return &lt;message-destination-link>, or null if unspecified
     */
    String getLink();
}
