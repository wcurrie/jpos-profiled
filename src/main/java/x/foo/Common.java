package x.foo;

import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;
import x.foo.jpos.AllocationTaggingLogListener;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Path;

public class Common {
    public static final Path SERVER_PID = new File("server.pid").toPath();

    public static Logger logger(String fileName) {
        try {
            Logger logger = new Logger();
            logger.addListener(new AllocationTaggingLogListener());
            logger.addListener(new SimpleLogListener(new PrintStream(fileName + ".log")));
            return logger;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
