package x.foo;

import org.jpos.iso.channel.XMLChannel;
import org.jpos.iso.packager.XMLPackager;
import x.foo.jmx.ThreadAlloc;
import x.foo.msg.Ping;

import java.nio.file.Files;

public class Client {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 10000;
        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }
        String serverPid = new String(Files.readAllBytes(Common.SERVER_PID));

        long beforeConnect = ThreadAlloc.getAllocatedBytes(serverPid, "simple-");
        XMLChannel channel = new XMLChannel(host, port, new XMLPackager());
        channel.setLogger(Common.logger("client"), "server");
        channel.connect();
        logAllocation(serverPid, 0, beforeConnect);

        for (int i = 0; i < 5; i++) {
            long before = ThreadAlloc.getAllocatedBytes(serverPid, "simple-");
            channel.send(Files.readAllBytes(Ping.PING_PATH));
            channel.receive();
            logAllocation(serverPid, i, before);
        }
    }

    private static void logAllocation(String serverPid, int i, long before) throws Exception {
        long after = ThreadAlloc.getAllocatedBytes(serverPid, "simple-");
        System.out.printf("%d. %d: %d -> %d\n", i, (after - before), before, after);
    }
}
