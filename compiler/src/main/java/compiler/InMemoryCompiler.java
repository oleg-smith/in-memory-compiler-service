package compiler;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import java.net.URI;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class InMemoryCompiler {

    private static final String COMPILATION_SUCCESSFULL = "COMPILATION SUCCESSFULL";
    private static final String COMPILATION_FAILED = "COMPILATION FAILED";

    private static final Pattern CLASS_NAME_SEARCH_PATTERN =
            Pattern.compile("class[\\s]+([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*[\\s]+\\{");

    private JavaMemFileManager memoryFileManager = new JavaMemFileManager();

    public String compile(String source) {

        if (!StringUtils.hasText(source)) {
            return "You have no code to compile";
        }

        String clsName = tryExtractClassName(source);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        JavaFileObject file = new JavaSourceFromString(clsName, source);

        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);

        CompilationTask task = compiler.getTask(null, memoryFileManager, diagnostics, null, null, compilationUnits);

        boolean success = task.call();

        StringBuilder result = new StringBuilder(success ? COMPILATION_SUCCESSFULL : COMPILATION_FAILED);
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            result.append("\r\n").append(diagnostic.toString());
        }
        return result.toString();
    }

    private static String tryExtractClassName(String source) {

        Matcher matcher = CLASS_NAME_SEARCH_PATTERN.matcher(source);
        if (matcher.find()) {
            String group = matcher.group();
            String clsName = group.substring(5, group.length() - 1).trim();
            return clsName;
        }
        return "";
    }
}

class JavaSourceFromString extends SimpleJavaFileObject {
    final String code;

    JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
