package x.foo.jpos;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;

import java.lang.management.ManagementFactory;
import com.sun.management.ThreadMXBean;

public class AllocTrackingISORequestListener implements ISORequestListener {

    private final ISORequestListener delegate;
    private final ThreadMXBean threadMXBean;

    public AllocTrackingISORequestListener(ISORequestListener delegate) {
        this.delegate = delegate;
        threadMXBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
    }

    public boolean process(ISOSource source, ISOMsg m) {
        long id = Thread.currentThread().getId();
        long startBytes = threadMXBean.getThreadAllocatedBytes(id);
        try {
            return delegate.process(source, m);
        } finally {
            long endBytes = threadMXBean.getThreadAllocatedBytes(id);
            long allocated = endBytes - startBytes;
            System.out.println(allocated);
        }
    }
}
