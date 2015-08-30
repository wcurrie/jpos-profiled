package x.foo.tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/*
    From https://github.com/codecentric/allocation-tracker
 */

/**
 * Main class, which is notified by BCI inserted code when an object is constructed. This class keeps a
 * ConcurrentHashMap with class names as keys. This is "leaking" the class name by design, so that the class name string
 * is kept even when the class has been unloaded. For each class name the ConcurrentHashMap will store an AtmomicLong
 * instance.
 * <p/>
 * Compatibility: Java 6-7
 */
public class Tracker {
    /*
       * Default size is insufficient in almost all real world scenarios. Any number is a pure guess. 1000 is a good
       * starting point.
       *
       * Impacts memory usage.
       */
    static final int MAP_SIZE = 1000;

    /*
     * Default load factor of 0.75 should work fine.
     *
     * Impacts memory usage. Low values impact cpu usage.
     */
    static final float LOAD_FACTOR = 0.75f;

    /*
     * Default concurrency level of 16 threads is probably sufficient in most real world deployments. Note that the
     * setting is for updating threads only, thus is concerned only when a tracked class is instantiated the first time.
     *
     * Impacts memory and cpu usage.
     */
    static final int CONCURRENCY_LEVEL = 16;

    static final int DEFAULT_AMOUNT = 100;

    private ConcurrentHashMap<String, AtomicLong> counts = new ConcurrentHashMap<String, AtomicLong>(MAP_SIZE,
            LOAD_FACTOR, CONCURRENCY_LEVEL);

    private static ThreadLocal<Tracker> local = new ThreadLocal<Tracker>() {
        @Override
        protected Tracker initialValue() {
            return new Tracker();
        }
    };

    /**
     * Call back invoked by BCI inserted code when a class is instantiated. The class name must be an interned/constant
     * value to avoid leaking!
     *
     * @param className name of the class that has just been instantiated.
     */
    public void constructed(String className) {
        AtomicLong atomicLong = counts.get(className);
        // for most cases the long should exist already.
        if (atomicLong == null) {
            atomicLong = new AtomicLong();
            AtomicLong oldValue = counts.putIfAbsent(className, atomicLong);
            if (oldValue != null) {
                // if the put returned an existing value that one is used.
                atomicLong = oldValue;
            }
        }
        atomicLong.incrementAndGet();
    }

    /**
     * Clears recorded data and starts recording.
     */
    public static void restart() {
        Tracker tracker = current();
        if (!tracker.counts.isEmpty()) {
            System.out.println(tracker.buildTopList(100));
        }
        tracker.counts.clear();
    }

    public static Tracker current() {
        return local.get();
    }

    /**
     * Builds a human readable list of class names and instantiation counts.
     * <p/>
     * Note: this method will create garbage while building and sorting the top list. The amount of garbage created is
     * dictated by the amount of classes tracked, not by the amount requested.
     *
     * @param amount controls how many results are included in the top list. If <= 0 will default to DEFAULT_AMOUNT.
     * @return a newline separated String containing class names and invocation counts.
     */
    public String buildTopList(final int amount) {
        Set<Entry<String, AtomicLong>> entrySet = counts.entrySet();
        ArrayList<ClassCounter> cc = new ArrayList<ClassCounter>(entrySet.size());

        for (Entry<String, AtomicLong> entry : entrySet) {
            cc.add(new ClassCounter(entry.getKey(), entry.getValue().longValue()));
        }
        Collections.sort(cc);
        StringBuilder sb = new StringBuilder();
        int max = Math.min(amount <= 0 ? DEFAULT_AMOUNT : amount, cc.size());
        for (int i = 0; i < max; i++) {
            sb.append(cc.get(i).toString());
            sb.append('\n');
        }
        return sb.toString();
    }
}
