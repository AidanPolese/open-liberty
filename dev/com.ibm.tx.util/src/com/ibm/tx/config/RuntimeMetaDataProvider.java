package com.ibm.tx.config;

public interface RuntimeMetaDataProvider 
{
	public boolean isClientSideJTADemarcationAllowed();
	public boolean isHeuristicHazardAccepted();
	public boolean isUserTransactionLookupPermitted(String name);
	public int getTransactionTimeout();
}
