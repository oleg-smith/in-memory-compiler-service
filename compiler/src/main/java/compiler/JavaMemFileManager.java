package compiler;

import javax.tools.*;
import java.io.IOException;

public class JavaMemFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    public JavaMemFileManager() {
        super(ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null));
    }

    @Override
    public JavaFileObject getJavaFileForOutput(
            Location location,
            String className,
            JavaFileObject.Kind kind,
            FileObject sibling)
            throws IOException {

        if ((StandardLocation.CLASS_OUTPUT == location) && (JavaFileObject.Kind.CLASS == kind)) {
            return new ClassMemFileObject(className);
        } else {
            throw new RuntimeException("Only class files supported");
        }
    }
}