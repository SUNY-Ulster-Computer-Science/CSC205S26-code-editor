package compiler;

import javax.tools.Diagnostic;

/**
 * Represents a compilation error with location information
 */

public class CompilationError {
    private long lineNumber;
    private long columnNumber;
    private String message;
    private Diagnostic.Kind kind;
    
    // Getters and setters
    public long getLineNumber() { return lineNumber; }
    public void setLineNumber(long lineNumber) { this.lineNumber = lineNumber; }
    public long getColumnNumber() { return columnNumber; }
    public void setColumnNumber(long columnNumber) { this.columnNumber = columnNumber; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Diagnostic.Kind getKind() { return kind; }
    public void setKind(Diagnostic.Kind kind) { this.kind = kind; }
    
    @Override
    public String toString() {
        return String.format("Line %d, Col %d: %s", lineNumber, columnNumber, message);
    }
}
