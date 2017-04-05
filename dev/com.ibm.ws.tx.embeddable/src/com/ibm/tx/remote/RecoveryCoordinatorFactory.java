package com.ibm.tx.remote;

import java.io.Serializable;

public interface RecoveryCoordinatorFactory
{
    public RecoveryCoordinator getRecoveryCoordinator(Serializable recoveryCoordinatorInfo) throws RecoveryCoordinatorNotAvailableException;
}
