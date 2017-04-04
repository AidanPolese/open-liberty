// 1.4, 7/3/08
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

public class Result {

    public Object data;               // cache id or data
    public int returnCode;            // return code
    public Exception diskException;   // exception 
    public int numDelete;             // nunber of deletes
    public int cause;                 // cause of invalidation
    public int source;                // source of invalidation
    public int dataSize;              // data size
    public int totalHashcode;         // total hashcode   // LI4337-17
    public boolean bExist;            // exist?
    public boolean bComplete;         // complete?
    public boolean bFromDepIdTemplateInvalidation;  // from depId and template invalidation
    public boolean bMore;             // more?    // LI4337-17
    public long numExplicitDeleted;
    public long numScanDeleted;
    public long numGCDeleted;
    public long deletedSize;

	public Result() {
        reset();
	}

    public void reset() {
        this.data = null;
        this.returnCode = HTODDynacache.NO_EXCEPTION;
        this.diskException = null;
        this.numDelete = 0;
        this.cause = 0;
        this.source = 0;
        this.dataSize = 0;
        this.totalHashcode = 0;    // LI4337-17
        this.bExist = !HTODDynacache.EXIST;
        this.bComplete = false;
        this.bFromDepIdTemplateInvalidation = false;
        this.bMore = false;    // LI4337-17
        this.numExplicitDeleted = 0;
        this.numScanDeleted = 0;
        this.numGCDeleted = 0;
        this.deletedSize = 0;
        
    }

    public void copy(Result other) {
        this.data = other.data;
        this.returnCode = other.returnCode;
        this.diskException = other.diskException;
        this.numDelete = other.numDelete;
        this.cause = other.cause;
        this.source = other.source;
        this.dataSize = other.dataSize;
        this.totalHashcode = other.totalHashcode;    // LI4337-17
        this.bExist = other.bExist;
        this.bComplete = other.bComplete;
        this.bFromDepIdTemplateInvalidation = other.bFromDepIdTemplateInvalidation;
        this.bMore = other.bMore;    // LI4337-17
        this.numExplicitDeleted = other.numExplicitDeleted;
        this.numScanDeleted = other.numScanDeleted;
        this.numGCDeleted = other.numGCDeleted;
        this.deletedSize = other.deletedSize;
    }
}
