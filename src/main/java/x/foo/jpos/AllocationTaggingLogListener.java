package x.foo.jpos;

import com.sun.management.ThreadMXBean;
import org.jpos.util.LogEvent;
import org.jpos.util.LogListener;

import java.lang.management.ManagementFactory;

public class AllocationTaggingLogListener implements LogListener {
    private final ThreadMXBean threadMXBean;

    public AllocationTaggingLogListener() {
        threadMXBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
    }

    public LogEvent log(LogEvent ev) {
        long allocatedBytes = threadMXBean.getThreadAllocatedBytes(Thread.currentThread().getId());
        ev.addMessage("allocated", String.valueOf(allocatedBytes));
        return ev;
    }
}
