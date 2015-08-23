package x.foo.jmx;

import com.sun.tools.attach.VirtualMachine;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.lang.management.ThreadInfo;

public class ThreadAlloc {
    static final String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";
    public static void main(String[] args) throws Exception {
        long allocatedBytes = getAllocatedBytes(args[0], "simple-");
        System.out.println(allocatedBytes);
    }

    public static long getAllocatedBytes(String pid, String prefix) throws Exception {
        ObjectName threading = new ObjectName("java.lang:type=Threading");

        JMXServiceURL url = new JMXServiceURL(getConnectorAddress(pid));
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
        long[] ids = (long[]) mbsc.getAttribute(threading, "AllThreadIds");

        // java.lang.management.ThreadMXBean
        CompositeData[] threads = (CompositeData[]) mbsc.invoke(threading, "getThreadInfo", new Object[]{ids}, new String[]{"[J"});
        for (CompositeData thread : threads) {
            ThreadInfo info = ThreadInfo.from(thread);
            if (info.getThreadName().startsWith(prefix)) {
                return (Long) mbsc.invoke(threading, "getThreadAllocatedBytes", new Object[]{info.getThreadId()}, new String[]{"long"});
            }
        }
        return -1;
    }

    private static String getConnectorAddress(String arg) throws Exception {
        VirtualMachine vm = VirtualMachine.attach(arg);
        String connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
        // no connector address, so we start the JMX agent
        if (connectorAddress == null) {
            String agent = vm.getSystemProperties().getProperty("java.home") +
                    File.separator + "lib" + File.separator + "management-agent.jar";
            vm.loadAgent(agent);

            // agent is started, get the connector address
            connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
        }
        return connectorAddress;
    }

}
