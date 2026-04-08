package compiler;

import java.util.List;
/**
 * Stores compilation results
 */
public class CompilationResult {
    private boolean success;
    private Class<?> compiledClass;
    private List<CompilationError> errors;
    private String error;
    
    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public Class<?> getCompiledClass() { return compiledClass; }
    public void setCompiledClass(Class<?> compiledClass) { this.compiledClass = compiledClass; }
    public List<CompilationError> getErrors() { return errors; }
    public void setErrors(List<CompilationError> errors) { this.errors = errors; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}