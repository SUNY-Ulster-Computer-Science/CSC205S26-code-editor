package compiler;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class JavaCompilerAPI {
    
    /**
     * Compiles Java source code from a string and returns the compiled class
     */
    public static CompilationResult compile(String className, String sourceCode) {
        CompilationResult result = new CompilationResult();
        
        // 1. Get the system compiler
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            result.setError("No Java compiler found. Please run with a JDK, not a JRE.");
            return result;
        }
        
        // 2. Set up diagnostic collector to capture errors
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        
        // 3. Create a file manager
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        
        // 4. Create a custom JavaFileObject from your source code string
        JavaFileObject sourceFile = new InMemoryJavaFileObject(className, sourceCode);
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(sourceFile);
        
        // 5. Set up output directory for compiled classes (optional - can compile to memory)
        List<String> options = new ArrayList<>();
        // options.add("-d"); options.add("./build"); // Uncomment to save .class files to disk
        
        // 6. Create compilation task
        JavaCompiler.CompilationTask task = compiler.getTask(
            null,           // Writer for compiler messages (null = System.err)
            fileManager,    // File manager
            diagnostics,    // Diagnostic listener
            options,        // Compiler options
            null,           // Annotation processing (null = none)
            compilationUnits // Source files to compile
        );
        
        // 7. Run the compilation
        boolean success = task.call();
        
        // 8. Process results
        if (success) {
            result.setSuccess(true);
            result.setCompiledClass(loadCompiledClass(className, fileManager));
        } else {
            result.setSuccess(false);
            result.setErrors(extractErrors(diagnostics));
        }
        
        // 9. Clean up
        try {
            fileManager.close();
        } catch (IOException e) {
            result.setError("Error closing file manager: " + e.getMessage());
        }
        
        return result;
    }
    
    private static List<CompilationError> extractErrors(DiagnosticCollector<JavaFileObject> diagnostics) {
        List<CompilationError> errors = new ArrayList<>();
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            CompilationError error = new CompilationError();
            error.setLineNumber(diagnostic.getLineNumber());
            error.setColumnNumber(diagnostic.getColumnNumber());
            error.setMessage(diagnostic.getMessage(null));
            error.setKind(diagnostic.getKind());
            errors.add(error);
        }
        return errors;
    }
    
    private static Class<?> loadCompiledClass(String className, StandardJavaFileManager fileManager) {
        // This is a simplified version - you'll need a custom ClassLoader
        // See the MemoryClassLoader implementation below
        return null;
    }
}





