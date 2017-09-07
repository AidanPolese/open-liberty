package com.ibm.ws.microprofile.metrics.impl;

import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.Snapshot;

/**
 * A metric which calculates the distribution of a value.
 *
 * @see <a href="http://www.johndcook.com/standard_deviation.html">Accurately computing running
 *      variance</a>
 */
public class HistogramImpl implements Histogram {
    private final Reservoir reservoir;
    private final LongAdderAdapter count;

    /**
     * Creates a new {@link HistogramImpl} with the given reservoir.
     *
     * @param reservoir the reservoir to create a histogram from
     */
    public HistogramImpl(Reservoir reservoir) {
        this.reservoir = reservoir;
        this.count = LongAdderProxy.create();
    }

    /**
     * Adds a recorded value.
     *
     * @param value the length of the value
     */
    public void update(int value) {
        update((long) value);
    }

    /**
     * Adds a recorded value.
     *
     * @param value the length of the value
     */
    public void update(long value) {
        count.increment();
        reservoir.update(value);
    }

    /**
     * Returns the number of values recorded.
     *
     * @return the number of values recorded
     */
    @Override
    public long getCount() {
        return count.sum();
    }

    @Override
    public Snapshot getSnapshot() {
        return reservoir.getSnapshot();
    }
}
