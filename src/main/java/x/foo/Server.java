package x.foo;

import org.jpos.core.SimpleConfiguration;
import org.jpos.iso.*;
import org.jpos.iso.channel.XMLChannel;
import org.jpos.iso.packager.XMLPackager;
import org.jpos.util.ThreadPool;

import java.util.Date;

public class Server {

    public static void main(String[] args) throws Exception {
        XMLChannel clientSide = new XMLChannel(new XMLPackager());
        // without a logger on the channel we don't see send and receive only session-start and -end
        clientSide.setLogger(Common.LOGGER, "server");

        ISOServer server = new ISOServer(10000, clientSide, new ThreadPool(1, 2, "simple-server"));
        server.setConfiguration(new SimpleConfiguration());
        server.addISORequestListener(pongListener());
        server.setLogger(Common.LOGGER, "server");
        server.run();
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