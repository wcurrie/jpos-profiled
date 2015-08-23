package x.foo;

import org.jpos.core.SimpleConfiguration;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOServer;
import org.jpos.iso.ISOSource;
import org.jpos.iso.channel.XMLChannel;
import org.jpos.iso.packager.XMLPackager;
import org.jpos.util.ThreadPool;

import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.util.Date;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class Server {

    public static void main(String[] args) throws Exception {
        XMLChannel clientSide = new XMLChannel(new XMLPackager());
        // without a logger on the channel we don't see send and receive only session-start and -end
        clientSide.setLogger(Common.LOGGER, "server");

        String pid = getPid();
        System.out.println("PID " + pid);
        Files.write(Common.SERVER_PID, pid.getBytes(), CREATE, TRUNCATE_EXISTING);

        ISOServer server = new ISOServer(10000, clientSide, new ThreadPool(1, 2, "simple-server"));
        server.setConfiguration(new SimpleConfiguration());
        server.addISORequestListener(pongListener());
        server.setLogger(Common.LOGGER, "server");
        server.run();
    }

    private static String getPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.replaceAll("@.*", "");
    }

    private static ISORequestListener pongListener() {
        return new ISORequestListener() {
            public boolean process(ISOSource source, ISOMsg m) {
                ISOMsg response = (ISOMsg) m.clone();
                try {
                    response.setResponseMTI();
                    response.set(48, new Date().toString());
                    source.send(response);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        };
    }
}
