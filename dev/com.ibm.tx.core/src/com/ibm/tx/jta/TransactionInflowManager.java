package com.ibm.tx.jta;
//
// COMPONENT_NAME: WAS.transactions
//
// ORIGINS: 27
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// %Z% %I% %W% %G% %U% [%H% %T%]
//
// DESCRIPTION:
//
// Change History:
//
// Date      Programmer    Defect   Description
// --------  ----------    ------   -----------
// 07/08/01  johawkes      451213.1 Creation

import javax.resource.spi.XATerminator;
import javax.resource.spi.work.ExecutionContext;
import javax.resource.spi.work.WorkCompletedException;

public interface TransactionInflowManager
{
    public void associate(ExecutionContext ec, String inflowCoordinatorName) throws WorkCompletedException;
    
    public void dissociate();
    
    public XATerminator getXATerminator(String inflowCoordinatorName);
}