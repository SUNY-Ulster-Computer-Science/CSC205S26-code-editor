package syntax;

import java.awt.Color;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.text.*;
import javax.swing.event.*;

/**
 * Syntax highlighter for Java code that works with JTextArea
 * Uses the Highlighter interface to color code Java syntax
 */
public class JavaSyntaxHighlighter {
    
    private final JTextArea textArea;
    private final Timer highlightTimer;
    private boolean enabled = true;
    
    // Colors for different syntax elements
    private static final Color KEYWORD_COLOR = new Color(0, 0, 255);      // Blue
    private static final Color STRING_COLOR = new Color(0, 128, 0);       // Green
    private static final Color COMMENT_COLOR = new Color(128, 128, 128);  // Gray
    private static final Color NUMBER_COLOR = new Color(255, 0, 0);       // Red
    private static final Color ANNOTATION_COLOR = new Color(100, 100, 100); // Dark Gray
    
    // Java keywords
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
        "class", "const", "continue", "default", "do", "double", "else", "enum",
        "extends", "final", "finally", "float", "for", "goto", "if", "implements",
        "import", "instanceof", "int", "interface", "long", "native", "new",
        "package", "private", "protected", "public", "return", "short", "static",
        "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while", "true", "false", "null"
    ));
    
    /**
     * Constructor - creates a syntax highlighter for a JTextArea
     * @param textArea The text area to highlight
     */
    public JavaSyntaxHighlighter(JTextArea textArea) {
        this.textArea = textArea;
        this.highlightTimer = new Timer(300, e -> highlight());
        this.highlightTimer.setRepeats(false);
        
        if (textArea != null) {
            // Add document listener to trigger highlighting on text changes
            textArea.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { scheduleHighlight(); }
                @Override
                public void removeUpdate(DocumentEvent e) { scheduleHighlight(); }
                @Override
                public void changedUpdate(DocumentEvent e) { scheduleHighlight(); }
            });
            
            // Initial highlight
            highlight();
        }
    }
    
    /**
     * Schedule highlighting after a short delay (for performance)
     */
    private void scheduleHighlight() {
        if (enabled) {
            if (highlightTimer.isRunning()) {
                highlightTimer.restart();
            } else {
                highlightTimer.start();
            }
        }
    }
    
    /**
     * Perform the syntax highlighting
     */
    private void highlight() {
        if (!enabled || textArea == null) return;
        
        SwingUtilities.invokeLater(() -> {
            try {
                String text = textArea.getText();
                Highlighter highlighter = textArea.getHighlighter();
                
                // Remove all existing highlights
                highlighter.removeAllHighlights();
                
                // Highlight different syntax elements
                highlightKeywords(text, highlighter);
                highlightStrings(text, highlighter);
                highlightComments(text, highlighter);
                highlightNumbers(text, highlighter);
                highlightAnnotations(text, highlighter);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Highlight Java keywords
     */
    private void highlightKeywords(String text, Highlighter highlighter) {
        for (String keyword : KEYWORDS) {
            Pattern pattern = Pattern.compile("\\b" + keyword + "\\b");
            Matcher matcher = pattern.matcher(text);
            
            while (matcher.find()) {
                try {
                    highlighter.addHighlight(matcher.start(), matcher.end(),
                        new DefaultHighlighter.DefaultHighlightPainter(KEYWORD_COLOR));
                } catch (BadLocationException e) {
                    // Ignore
                }
            }
        }
    }
    
    /**
     * Highlight strings (text between quotes)
     */
    private void highlightStrings(String text, Highlighter highlighter) {
        // Match strings with escaped quotes
        Pattern pattern = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"");
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            try {
                highlighter.addHighlight(matcher.start(), matcher.end(),
                    new DefaultHighlighter.DefaultHighlightPainter(STRING_COLOR));
            } catch (BadLocationException e) {
                // Ignore
            }
        }
    }
    
    /**
     * Highlight single-line and multi-line comments
     */
    private void highlightComments(String text, Highlighter highlighter) {
        // Single line comments
        Pattern singleLinePattern = Pattern.compile("//.*$", Pattern.MULTILINE);
        Matcher singleLineMatcher = singleLinePattern.matcher(text);
        
        while (singleLineMatcher.find()) {
            try {
                highlighter.addHighlight(singleLineMatcher.start(), singleLineMatcher.end(),
                    new DefaultHighlighter.DefaultHighlightPainter(COMMENT_COLOR));
            } catch (BadLocationException e) {
                // Ignore
            }
        }
        
        // Multi-line comments
        Pattern multiLinePattern = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
        Matcher multiLineMatcher = multiLinePattern.matcher(text);
        
        while (multiLineMatcher.find()) {
            try {
                highlighter.addHighlight(multiLineMatcher.start(), multiLineMatcher.end(),
                    new DefaultHighlighter.DefaultHighlightPainter(COMMENT_COLOR));
            } catch (BadLocationException e) {
                // Ignore
            }
        }
    }
    
    /**
     * Highlight numbers (integers, decimals, hex)
     */
    private void highlightNumbers(String text, Highlighter highlighter) {
        // Integers and decimals
        Pattern numberPattern = Pattern.compile("\\b\\d+(\\.\\d+)?\\b");
        Matcher numberMatcher = numberPattern.matcher(text);
        
        while (numberMatcher.find()) {
            try {
                highlighter.addHighlight(numberMatcher.start(), numberMatcher.end(),
                    new DefaultHighlighter.DefaultHighlightPainter(NUMBER_COLOR));
            } catch (BadLocationException e) {
                // Ignore
            }
        }
        
        // Hex numbers
        Pattern hexPattern = Pattern.compile("\\b0[xX][0-9a-fA-F]+\\b");
        Matcher hexMatcher = hexPattern.matcher(text);
        
        while (hexMatcher.find()) {
            try {
                highlighter.addHighlight(hexMatcher.start(), hexMatcher.end(),
                    new DefaultHighlighter.DefaultHighlightPainter(NUMBER_COLOR));
            } catch (BadLocationException e) {
                // Ignore
            }
        }
    }
    
    /**
     * Highlight Java annotations (@Annotation)
     */
    private void highlightAnnotations(String text, Highlighter highlighter) {
        Pattern pattern = Pattern.compile("@\\w+");
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            try {
                highlighter.addHighlight(matcher.start(), matcher.end(),
                    new DefaultHighlighter.DefaultHighlightPainter(ANNOTATION_COLOR));
            } catch (BadLocationException e) {
                // Ignore
            }
        }
    }
    
    /**
     * Enable syntax highlighting
     */
    public void enable() {
        enabled = true;
        highlight();
    }
    
    /**
     * Disable syntax highlighting and clear all highlights
     */
    public void disable() {
        enabled = false;
        if (textArea != null) {
            textArea.getHighlighter().removeAllHighlights();
        }
    }
    
    /**
     * Toggle syntax highlighting on/off
     */
    public void toggle() {
        if (enabled) {
            disable();
        } else {
            enable();
        }
    }
    
    /**
     * Check if highlighting is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
}
