/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* ORIGINS: 27                                                                */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70(C) COPYRIGHT International Business Machines Corp. 2010  */
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                              */
/*                                                                            */
/* DESCRIPTION:                                                               */
/*                                                                            */
/* Change History:                                                            */
/*                                                                            */
/* Date      Programmer  Defect         Description                           */
/* --------  ----------  ------         -----------                           */
/* 10-02-19  mallam      642260         Create                                */
/*                                                                            */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Interface: SharedServerLeaseLog
//------------------------------------------------------------------------------
/**
 * <p>
 * The SharedServerLeaseLog interface provides methods for accessing shared
 * information on server leases.
 * </p>
 * 
 */
public interface SharedServerLeaseLog
{
    public void updateServerLease(String recoveryIdentity, String recoveryGroup, boolean isServerStartup) throws Exception;

    public void deleteServerLease(String recoveryIdentity) throws Exception;

    /**
     * @param recoveryIdentityToRecover
     * @param myRecoveryIdentity
     * @throws Exception
     */
    public boolean claimPeerLeaseForRecovery(String recoveryIdentityToRecover, String myRecoveryIdentity, LeaseInfo leaseInfo) throws Exception;

    /**
     * @param peerLeaseTable
     * @param recoveryGroup
     * @throws Exception
     */
    void getLeasesForPeers(final PeerLeaseTable peerLeaseTable, String recoveryGroup) throws Exception;

    public boolean lockLocalLease(String recoveryIdentity);

    public boolean releaseLocalLease(String recoveryIdentity) throws Exception;

    public boolean lockPeerLease(String recoveryIdentity);

    public boolean releasePeerLease(String recoveryIdentity) throws Exception;

    public void setPeerRecoveryLeaseTimeout(int leaseTimeout);
}
