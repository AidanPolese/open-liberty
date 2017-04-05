/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.jbatch.container.persistence.jpa;

/**
 * @author skurz
 *
 */
// Hide this for now internally to the package to see how far it bleeds out.
public interface EntityConstants {

	// Partition numbering begins at '0', so '-1' is a natural choice to denote the top-level thread, in contrast.
	final int TOP_LEVEL_THREAD = -1;

	// Since '0' is a maybe-valid (if seemingly useless boundary value) for plan size, let's distinguish with -1 the state
	// where the value hasn't been initialized.
	public final int PARTITION_PLAN_SIZE_UNINITIALIZED = -1;

	final int MAX_EXIT_STATUS_LENGTH = 512;

	final int MAX_STEP_NAME = 128;
}
