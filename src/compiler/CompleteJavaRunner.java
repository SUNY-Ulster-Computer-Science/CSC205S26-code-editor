package compiler;

import java.io.*;
import java.nio.file.*;

public class CompleteJavaRunner {
    
    public static RunResult compileAndRun(String className, String sourceCode, String[] args) {
        RunResult result = new RunResult();
        
        try {
            // Create a temporary directory for compilation
            Path tempDir = Files.createTempDirectory("java_compiler_");
            File sourceFile = tempDir.resolve(className + ".java").toFile();
            
            // Write source code to file
            try (FileWriter fw = new FileWriter(sourceFile)) {
                fw.write(sourceCode);
            }
            
            // Compile the code
            ProcessBuilder compilePb = new ProcessBuilder("javac", sourceFile.getName());
            compilePb.directory(tempDir.toFile());
            compilePb.redirectErrorStream(true);
            
            Process compileProcess = compilePb.start();
            String compileOutput = readStream(compileProcess.getInputStream());
            int compileExit = compileProcess.waitFor();
            
            if (compileExit != 0) {
                result.setError("Compilation failed:\n" + compileOutput);
                cleanup(tempDir);
                return result;
            }
            
            // Check if .class file was created
            File classFile = tempDir.resolve(className + ".class").toFile();
            if (!classFile.exists()) {
                result.setError("Compilation succeeded but .class file not found");
                cleanup(tempDir);
                return result;
            }
            
            // Run the compiled code
            ProcessBuilder runPb = new ProcessBuilder("java", className);
            runPb.directory(tempDir.toFile());
            runPb.redirectErrorStream(true);
            
            Process runProcess = runPb.start();
            String runOutput = readStream(runProcess.getInputStream());
            int runExit = runProcess.waitFor();
            
            if (runExit == 0) {
                result.setSuccess(true);
                result.setOutput(runOutput);
            } else {
                result.setError("Runtime error:\n" + runOutput);
            }
            
            // Cleanup
            cleanup(tempDir);
            
        } catch (Exception e) {
            result.setError("System error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    public static CompilationResult compileOnly(String className, String sourceCode) {
        CompilationResult result = new CompilationResult();
        
        try {
            Path tempDir = Files.createTempDirectory("java_compiler_");
            File sourceFile = tempDir.resolve(className + ".java").toFile();
            
            try (FileWriter fw = new FileWriter(sourceFile)) {
                fw.write(sourceCode);
            }
            
            ProcessBuilder compilePb = new ProcessBuilder("javac", sourceFile.getName());
            compilePb.directory(tempDir.toFile());
            compilePb.redirectErrorStream(true);
            
            Process compileProcess = compilePb.start();
            String compileOutput = readStream(compileProcess.getInputStream());
            int compileExit = compileProcess.waitFor();
            
            if (compileExit == 0) {
                result.setSuccess(true);
            } else {
                result.setSuccess(false);
                CompilationError error = new CompilationError();
                error.setMessage(compileOutput);
                result.setErrors(java.util.Arrays.asList(error));
            }
            
            cleanup(tempDir);
            
        } catch (Exception e) {
            result.setError(e.getMessage());
        }
        
        return result;
    }
    
    private static String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
    
    private static void cleanup(Path tempDir) {
        try {
            Files.walk(tempDir)
                .sorted(java.util.Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        } catch (IOException e) {
            System.err.println("Cleanup error: " + e.getMessage());
        }
    }
}