/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package test.shared;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.ibm.ws.kernel.boot.internal.commands.JavaDumpAction;
import com.ibm.ws.kernel.boot.internal.commands.JavaDumper;

/**
 *
 */
public class DumpTimerRule implements TestRule {

    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final long timeoutMillis;
    private final File outputDir;
    private volatile boolean complete;

    public DumpTimerRule(long timeoutMillis, File outputDir) {
        this.timeoutMillis = timeoutMillis;
        this.outputDir = outputDir;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.junit.rules.TestRule#apply(org.junit.runners.model.Statement, org.junit.runner.Description)
     */
    @Override
    public Statement apply(Statement statement, Description arg1) {
        return new DumpTimerStatement(statement);
    }

    private class DumpTimerStatement extends Statement {
        private final Statement statement;

        private DumpTimerStatement(Statement statement) {
            this.statement = statement;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.junit.runners.model.Statement#evaluate()
         */
        @Override
        public void evaluate() throws Throwable {
            DumpThreads dumper = new DumpThreads();
            ScheduledFuture<?> future = scheduler.schedule(dumper, timeoutMillis, TimeUnit.MILLISECONDS);
            try {
                statement.evaluate();
            } finally {
                complete = true;
                future.cancel(false);
            }

        }

    }

    private class DumpThreads implements Runnable {

        @Override
        public void run() {
            if (!complete) {
                JavaDumper.getInstance().dump(JavaDumpAction.THREAD, outputDir);
            }

        }

    }
}
