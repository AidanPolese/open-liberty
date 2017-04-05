package com.ibm.tx.remote;

public class RecoveryCoordinatorNotAvailableException extends Exception {

    /**
     * @param e
     */
    public RecoveryCoordinatorNotAvailableException(Exception e) {
        this.initCause(e);
    }

    /**
     * 
     */
    public RecoveryCoordinatorNotAvailableException() {
        // TODO Auto-generated constructor stub
    }

    /**  */
    private static final long serialVersionUID = -3373441739854545004L;
}