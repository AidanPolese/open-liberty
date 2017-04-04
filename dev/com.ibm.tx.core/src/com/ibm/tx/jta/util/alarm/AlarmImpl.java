//
// COMPONENT_NAME: WAS.transactions
//
// ORIGINS: 27
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2007, 2009
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
//
// Date      Programmer    Defect   Description
// --------  ----------    ------   -----------
// 07/08/16  awilkins      459938   Purge executor to clear cancelled alarms
// 07/08/29  johawkes      461798   Minor perfomance improvement
// 09/06/02  mallam        596067   package move

package com.ibm.tx.jta.util.alarm;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;

import com.ibm.tx.util.alarm.Alarm;

public class AlarmImpl implements Alarm
{
	private ScheduledFuture _future;
	private ThreadPoolExecutor _executor;
	
	public AlarmImpl(ScheduledFuture future, ThreadPoolExecutor executor)
	{
		_future = future;
		_executor = executor;
	}

	public boolean cancel()
	{
		if (_future.cancel(false))
		{
			_executor.purge();

            return true;
        }

		return false;
	}
}
