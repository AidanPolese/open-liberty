/*
 * 
 * 
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
 * 
 * Change activity:
 *
 * Reason            Date   Origin   Description
 * ---------------   ------ -------- ------------------------------------------
 *                          matrober TestCase Original - 25 March 03
 * 169473            130603 matrober Log Throwables in LTC
 * ============================================================================
 */
package com.ibm.mqst.jetsam;

/**
 * @author matrober
 *
 * This interface defines the methods provided by a JETSAMLog
 */
public interface JETSAMLog extends java.io.Serializable
{
	
	// **************** LIFECYCLE METHODS ************************
	public void open(boolean newFile);
	public boolean isOpen();
	public void close();
	public String getFileName();
	
	// ***************** LOGGING METHODS *************************
	public void comment(String text);
	public void comment(Exception e);
	public void comment(Throwable e);
	public void comment(String text, Exception e);
	public void comment(String text, Error e);
	public void error(String text);
	public void error(Exception e);
	public void error(Throwable e);
	public void error(String text, Exception e);
	public void error(String text, Error e);
	public int getErrors();
	public void setErrors(int setErrors);
	public void blankLine();
	public void section(String sectionName);
	public void header(String hdrStr);
	public void timestamp();
	
	// *************** PERFORMANCE METHODS ***********************	
	public void performance(String name);
	public void performanceStats();

}
