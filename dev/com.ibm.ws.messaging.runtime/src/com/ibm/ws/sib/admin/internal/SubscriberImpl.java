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
 * Reason                                                Date   Origin   Description
 * ---------------                                       ------ -------- --------------------------------------------
 * Newly added to liberty release to add MBean support     051212
 * ============================================================================
 */

package com.ibm.ws.sib.admin.internal;

import com.ibm.websphere.messaging.mbean.SubscriberMBean;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.admin.Controllable;
import com.ibm.ws.sib.admin.JsConstants;
import com.ibm.ws.sib.processor.runtime.SIMPLocalSubscriptionControllable;
import com.ibm.ws.sib.utils.ras.SibTr;


public class SubscriberImpl implements SubscriberMBean, Controllable {

  private static final String CLASS_NAME = "com.ibm.ws.sib.admin.internal.SubscriberImpl";
  private static final TraceComponent tc =
		SibTr.register(SubscriberImpl.class, JsConstants.TRGRP_AS, JsConstants.MSG_BUNDLE);

	// Debugging aid
	static {
		if (tc.isDebugEnabled())
			SibTr.debug(tc, "/com/ibm/ws/sib/admin/internal/SubscriberImpl.java");
	}

	// The instance of the actual controllable object 
	private SIMPLocalSubscriptionControllable _c = null;

	  // Properties of the MBean
	  java.util.Properties props = new java.util.Properties();

	  public java.util.Properties getProperties() {
	    return (java.util.Properties) props.clone();
	  }
	
	public SubscriberImpl(JsMessagingEngineImpl me, Controllable c) {
		if (tc.isEntryEnabled())
			SibTr.entry(tc, CLASS_NAME + "().<init>");

		_c = (SIMPLocalSubscriptionControllable) c;

        props.setProperty(((JsBusImpl) me.getBus()).getMBeanType(), ((JsBusImpl) me.getBus()).getName());
        props.setProperty(me.getMBeanType(), me.getName());
		props.setProperty("id", _c.getId());

//		activateMBean(JsConstants.MBEAN_TYPE_SP, ((Controllable) c).getName(), props);

		if (tc.isEntryEnabled())
			SibTr.exit(tc, CLASS_NAME + "().<init>");
	}

	// ATTRIBUTES 

	public String getId() {
		if (tc.isEntryEnabled())
			SibTr.entry(tc, "getId");
		if (tc.isEntryEnabled())
			SibTr.exit(tc, "getId", _c.getId());
		return _c.getId();
	}

	public String getName() {
		if (tc.isEntryEnabled())
			SibTr.entry(tc, "getName");
		if (tc.isEntryEnabled())
			SibTr.exit(tc, "getName", ((Controllable) _c).getName());
		return ((Controllable) _c).getName();
	}

	@Override
	public String getUuid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getConfigId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteEngineUuid() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// OPERATIONS

}

