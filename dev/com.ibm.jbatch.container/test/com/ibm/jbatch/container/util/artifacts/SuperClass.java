package com.ibm.jbatch.container.util.artifacts;

import javax.batch.api.BatchProperty;
import javax.inject.Inject;

public class SuperClass {

	@Inject
	@BatchProperty(name="numRecords")
	protected String numberRecords;

	@Inject
	@BatchProperty
	private String privateSuper;

	@Inject
	@BatchProperty
	protected String ibmBatch;

	/**
	 * @return the privateSuper
	 */
	public String getPrivateSuper() {
		return privateSuper;
	}

	/**
	 * @return the ibmBatch
	 */
	public String getIbmBatch() {
		return ibmBatch;
	}

	/**
	 * @return the numRecords
	 */
	public String getNumRecords() {
		return numberRecords;
	}

}
