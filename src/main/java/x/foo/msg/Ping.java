package x.foo.msg;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.XMLPackager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class Ping {

    public static final Path PING_PATH = new File("src/main/resources/ping.xml").toPath();

    public static void main(String[] args) throws Exception {
        XMLPackager packager = new XMLPackager();
        byte[] bytes = packager.pack(build());
        Files.write(PING_PATH, bytes, CREATE_NEW, TRUNCATE_EXISTING);
    }

    public static ISOMsg build() throws ISOException {
        ISOMsg m = new ISOMsg("0800");
        m.set(70, "300");
        m.set(48, new Date().toString());
        return m;
    }
}
