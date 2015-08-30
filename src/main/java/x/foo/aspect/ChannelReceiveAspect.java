package x.foo.aspect;

import com.sun.management.ThreadMXBean;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import x.foo.tracker.Tracker;

import java.lang.management.ManagementFactory;

@Aspect
public class ChannelReceiveAspect {

    private final ThreadMXBean threadMXBean;
    private final ThreadLocal<Long> lastAllocation = new ThreadLocal<Long>();

    public ChannelReceiveAspect() {
        threadMXBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
    }

    @Before("call(org.jpos.iso.ISOMsg org.jpos.iso.ServerChannel.receive(..))")
    public void beforeReceive() {
        System.out.println("about to receive");
        Tracker.restart();
        Thread thread = Thread.currentThread();
        long allocatedBytes = threadMXBean.getThreadAllocatedBytes(thread.getId());
        Long last = lastAllocation.get();
        long lastBytes = last == null ? 0 : last;
        System.out.println(allocatedBytes + " " + (allocatedBytes - lastBytes));
        lastAllocation.set(allocatedBytes);
    }
}
