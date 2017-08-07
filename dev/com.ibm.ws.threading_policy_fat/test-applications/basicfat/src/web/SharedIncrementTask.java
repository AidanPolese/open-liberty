package web;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Task that increments a counter which can be shared by multiple tasks to record how many have run.
 */
public class SharedIncrementTask implements Callable<Integer>, Runnable {
    private final AtomicInteger counter;

    public SharedIncrementTask() {
        counter = new AtomicInteger();
    }

    public SharedIncrementTask(AtomicInteger counter) {
        this.counter = counter;
    }

    @Override
    public Integer call() throws Exception {
        int count = counter.incrementAndGet();
        System.out.println("call " + toString() + " execution #" + count);
        return count;
    }

    public int count() {
        return counter.get();
    }

    @Override
    public void run() {
        int count = counter.incrementAndGet();
        System.out.println("run " + toString() + " execution #" + count);
    }
}
