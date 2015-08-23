package x.foo;

import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;

import java.io.File;
import java.nio.file.Path;

public class Common {
    public static final Path SERVER_PID = new File("server.pid").toPath();
    public static final Logger LOGGER = new Logger();
    static {
        LOGGER.addListener(new SimpleLogListener());
    }
}
