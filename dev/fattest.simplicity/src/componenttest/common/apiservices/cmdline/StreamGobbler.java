package componenttest.common.apiservices.cmdline;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * When instances of this Thread object are run, all the character data that is
 * written to the input stream is written (and flushed) line by line to the
 * output stream.<br>
 * 
 * @author Tim Burns
 * 
 */
public class StreamGobbler extends Thread {

    protected static final String METHOD_RUN = "run";

    protected final InputStream input;
    protected final OutputStream redirect;
    protected final boolean async;

    private boolean joined;
    private boolean terminated;

    public static void main(String[] args) throws Exception {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(args);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        StreamGobbler sg = new StreamGobbler(p.getInputStream(), System.out, false);
        sg.start();
        p.waitFor();
        sg.doJoin();
        System.exit(p.exitValue());
    }

    /**
     * Constructs a new StreamGobbler.
     * 
     * @param input
     *            The stream of data you want to listen to
     * @param redirect
     *            The stream of data you want to notify about events on the
     *            input stream. If this argument is null, the underlying
     *            OutputStream is initialized to a new ByteArrayOutputStream,
     *            and events occurring on the input stream will be printed to
     *            trace but otherwise ignored.
     */
    public StreamGobbler(final InputStream input, final OutputStream redirect, boolean async) {
        this.input = input;
        if (redirect == null) {
            this.redirect = new ByteArrayOutputStream();
        } else {
            this.redirect = redirect;
        }
        this.async = async;
        if (!async) {
            setDaemon(true);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        PrintWriter writer = new PrintWriter(this.redirect, true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                        this.input));
        try {
            for (String line; (line = reader.readLine()) != null;) {
                synchronized (this) {
                    if (joined) {
                        // The main thread was notified that the process
                        // ended and has already given up waiting for
                        // output from the foreground process.
                        break;
                    }

                    writer.println(line);
                }
            }
        } catch (IOException ex) {
            throw new Error(ex);
        } finally {
            synchronized (this) {
                terminated = true;
                notifyAll();
            }
            if (async) {
                writer.close();
            }
        }
    }

    public void doJoin() throws InterruptedException {
        // Windows and Solaris don't disconnect background processes (start /b
        // or cmd & if output is not redirected) from the console of foreground
        // processes, so waiting until the end of output from the server script
        // means waiting until the server process itself ends.  We can't wait
        // that long, so we wait one second after .waitFor() ends.  Hopefully
        // this will be long enough to copy all the output from the script.

        synchronized (this) {
            long begin = System.nanoTime();
            long end = begin + TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS);
            long duration = end - begin;
            while (!terminated && duration > 0) {
                TimeUnit.NANOSECONDS.timedWait(this, duration);
                duration = end - System.nanoTime();
            }

            // If the thread didn't end after waiting for a second,
            // then assume it's stuck in a blocking read.  Oh well,
            // it's a daemon thread, so it'll go away eventually.  Let
            // it know that we gave up to avoid spurious output in case
            // it eventually wakes up.
            joined = true;
        }
    }
}
