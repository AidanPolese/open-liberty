/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *                                 Version X copied from CMVC
 * ============================================================================
 */

package com.ibm.ws.sib.admin;

import java.util.Map;
import java.util.Set;

import com.ibm.websphere.sib.Reliability;
import com.ibm.ws.sib.utils.SIBUuid12;

/**
 * @author philip
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface VirtualLinkDefinition {

    /**
     * Get the UUID
     * 
     * @return string containing the UUID of the link
     */
    public SIBUuid12 getUuid();

    /**
     * Get the Name
     * 
     * @return string containing the name of the link
     */
    public String getName();

    /**
     * Return the type of the VirtualLink.
     * 
     * @return the type of this VirtualLink, which may be "SIBVirtualMQLink" or
     *         "SIBVirtualGatewayLink".
     */
    public String getType();

    /**
     * Get the ForeignBusDefinition in which this VirtualLink is defined
     * 
     * @return
     */
    public ForeignBusDefinition getForeignBus();

    /**
     * Retrieve the set of messaging engine UUIDs that "localise" the link. Currently
     * this set will have a maximum size of one.
     * 
     * @return A Set of messaging engine UUIDs
     */
    public Set getLinkLocalitySet();

    /**
     * Return TopicSpace mappings.
     * 
     * @return
     */
    public Map getTopicSpaceMappings();

    /**
     * Return the Inbound Userid for this VirtualLink.
     * 
     * @return
     */
    public String getInboundUserid();

    /**
     * Return the Outbound Userid for this VirtualLink.
     * 
     * @return
     */
    public String getOutboundUserid();

    /**
     * Return the exception destination.
     * 
     * @return
     */
    public String getExceptionDestination();

    /**
     * Return whether local queue points are
     * preferred (now optional in WAS7.x)
     * 
     * @return boolean
     */
    public boolean getPreferLocal();

    /**
     * Get the reliability level at which exceptioned messages on a link will
     * automatically be discarded. The default level is BEST_EFFORT_NONPERSISTENT.
     * 
     * @return Reliability
     */
    public Reliability getExceptionDiscardReliability();

}
