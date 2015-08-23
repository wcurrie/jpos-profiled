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
        String serverPid = "38296";

        long before = ThreadAlloc.getAllocatedBytes(serverPid, "simple-");

        XMLChannel channel = new XMLChannel(host, port, new XMLPackager());
        channel.setLogger(Common.LOGGER, "server");
        channel.connect();
        channel.send(Files.readAllBytes(Ping.PING_PATH));
        channel.receive();

        long after = ThreadAlloc.getAllocatedBytes(serverPid, "simple-");
        System.out.printf("%d: %d -> %d", (after - before), before, after);
    }
}
