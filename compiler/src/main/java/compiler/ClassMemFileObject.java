package compiler;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class ClassMemFileObject extends SimpleJavaFileObject {
    private ByteArrayOutputStream os = new ByteArrayOutputStream();

    ClassMemFileObject(String className) {
        super(URI.create("mem:///" + className + Kind.CLASS.extension), Kind.CLASS);
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return os;
    }
}