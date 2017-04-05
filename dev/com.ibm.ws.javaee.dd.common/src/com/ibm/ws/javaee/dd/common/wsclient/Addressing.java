/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.common.wsclient;

/**
 * Represents &lt;addressing> in &lt;port-component-ref> in &lt;service-ref>.
 */
public interface Addressing
{
    /**
     * Represents an unspecified value for {@link #getAddressingResponsesTypeValue}.
     */
    int ADDRESSING_RESPONSES_UNSPECIFIED = -1;

    /**
     * Represents "ALL" for {@link #getAddressingResponsesTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.webservice.wscommon.AddressingResponsesType#ALL
     */
    int ADDRESSING_RESPONSES_ALL = 0;

    /**
     * Represents "ANONYMOUS" for {@link #getAddressingResponsesTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.webservice.wscommon.AddressingResponsesType#ANONYMOUS
     */
    int ADDRESSING_RESPONSES_ANONYMOUS = 1;

    /**
     * Represents "ANONYMOUS" for {@link #getAddressingResponsesTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.webservice.wscommon.AddressingResponsesType#NON_ANONYMOUS
     */
    int ADDRESSING_RESPONSES_NON_ANONYMOUS = 2;

    /**
     * @return true if &lt;enabled> is specified
     * @see #isEnabled
     */
    boolean isSetEnabled();

    /**
     * @return &lt;enabled> if specified
     * @see #isSetEnabled
     */
    boolean isEnabled();

    /**
     * @return true if &lt;required> is specified
     * @see #isRequired
     */
    boolean isSetRequired();

    /**
     * @return &lt;required> if specified
     * @see #isSetRequired
     */
    boolean isRequired();

    /**
     * @return &lt;responses>
     *         <ul>
     *         <li>{@link #ADDRESSING_RESPONSES_UNSPECIFIED} if unspecified
     *         <li>{@link #ADDRESSING_RESPONSES_ANONYMOUS} - ANONYMOUS
     *         <li>{@link #ADDRESSING_RESPONSES_NON_ANONYMOUS} - NON_ANONYMOUS
     *         <li>{@link #ADDRESSING_RESPONSES_ALL} - ALL
     *         </ul>
     */
    int getAddressingResponsesTypeValue();
}
