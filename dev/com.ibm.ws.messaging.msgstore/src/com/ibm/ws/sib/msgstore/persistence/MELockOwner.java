package com.ibm.ws.sib.msgstore.persistence;

import java.sql.Timestamp;

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
 *  Reason         Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 * SIB0003.ms.1    26/05/05 gareth   Turn on SpillDispatcher
 * 496893          21/02/08 gareth   Improve filestore robustness
 * F1332-52014         30/09/11 balgirid New Database Locking Mechanism
 * F008622         18/12/11 urwashi  Adding the constructor that takes original parameters that worked before R000382
 * 723936          20/12/11 balgirid Feature Review comments
 * ============================================================================
 */

public class MELockOwner
{
    private String _meUUID;
    private String _incUUID;
    private int    _version;
    private int    _migrationVersion;
    private Timestamp _dbLastTimestamp; //R000382
    
    private String _meName;
    private Timestamp _dbCurrentTimestamp; //R000382
    private String _meStatus; //R000382
    
    public MELockOwner(String meUUID, String incUUID, int version, int migrationVersion, String meName, Timestamp dbLastTimestamp, String meInfo, Timestamp dbCurrentTimestamp, String meStatus)
    {
        _meUUID           = meUUID;
        _incUUID          = incUUID;
        _version          = version;
        _migrationVersion = migrationVersion;
        _meName           = meName;
        _dbLastTimestamp  = dbLastTimestamp; //R000382
        
        _dbCurrentTimestamp      = dbCurrentTimestamp; //R000382
        _meStatus = meStatus; //R000382
    }
      public MELockOwner(String meUUID, String incUUID, int version, int migrationVersion, String meName)
    {
        _meUUID           = meUUID;
        _incUUID          = incUUID;
        _version          = version;
        _migrationVersion = migrationVersion;
        _meName           = meName;
        _dbLastTimestamp  = null;         
        _dbCurrentTimestamp  = null; 
        _meStatus = "";
    }

    public String getMeUUID()
    {
        return _meUUID;
    }

    public String getIncUUID()
    {
        return _incUUID;
    }

    public int getVersion()
    {
        return _version;
    }

    public int getMigrationVersion()
    {
        return _migrationVersion;
    }
    
    public Timestamp getDBLastTimestamp() 
    {
	    return _dbLastTimestamp;
	}
    
	public String getMeName()
    {
        return _meName;
    }
	
    public Timestamp getDBCurrentTimestamp()
	{
		return _dbCurrentTimestamp;
	}
	
    public String getMeStatus()
	{   
		return _meStatus;
    }
	
    public void setMeStatus(String meStatus)
    {   
    		_meStatus = meStatus;
    }

    public String toString()
    {
        StringBuffer reply = new StringBuffer("MELockOwner[ME_UUID=");
        reply.append(_meUUID);
        reply.append(", INC_UUID=");
        reply.append(_incUUID);
        reply.append(", VERSION=");
        reply.append(_version);
        reply.append(", MIGRATION_VERSION=");
        reply.append(_migrationVersion);
        reply.append(",LAST_TIMESTAMP="); //R000382
        reply.append(_dbLastTimestamp); //R000382
        reply.append(", ME_NAME=");
        reply.append(_meName);
        reply.append(", CURRENT_TIMESTAMP="); //R000382
        reply.append(_dbCurrentTimestamp); //R000382
        reply.append(", ME_STATUS="); //R000382
        reply.append(_meStatus); //R000382
        reply.append("]");

        return reply.toString();
    }
}
