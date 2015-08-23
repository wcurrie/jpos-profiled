package x.foo;

import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;

public class Common {
    public static final Logger LOGGER = new Logger();
    static {
        LOGGER.addListener(new SimpleLogListener());
    }
}
