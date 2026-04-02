package syntax;

import java.awt.Color;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.text.*;
import javax.swing.event.*;

/**
 * Syntax highlighter that changes text color (not background)
 * Works with JTextPane and StyledDocument
 */
public class JavaSyntaxHighlighterTextColor {
    
    private final JTextPane textPane;
    private final StyledDocument document;
    private final StyleContext styleContext;
    private final Timer highlightTimer;
    private boolean enabled = true;
    
    // Style names
    private static final String STYLE_KEYWORD = "keyword";
    private static final String STYLE_STRING = "string";
    private static final String STYLE_COMMENT = "comment";
    private static final String STYLE_NUMBER = "number";
    private static final String STYLE_ANNOTATION = "annotation";
    private static final String STYLE_DEFAULT = "default";
    
    // Colors for text
    private static final Color KEYWORD_COLOR = new Color(0, 0, 255);      // Blue
    private static final Color STRING_COLOR = new Color(0, 128, 0);       // Green
    private static final Color COMMENT_COLOR = new Color(128, 128, 128);  // Gray
    private static final Color NUMBER_COLOR = new Color(0, 0, 205);       // Dark Blue
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
     * Constructor - creates a syntax highlighter for a JTextPane
     * @param textPane The text pane to highlight
     */
    public JavaSyntaxHighlighterTextColor(JTextPane textPane) {
        this.textPane = textPane;
        this.document = textPane.getStyledDocument();
        this.styleContext = new StyleContext();
        this.highlightTimer = new Timer(300, e -> highlight());
        this.highlightTimer.setRepeats(false);
        
        setupStyles();
        
        if (textPane != null) {
            // Add document listener to trigger highlighting on text changes
            document.addDocumentListener(new DocumentListener() {
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
     * Set up the styles for different syntax elements
     */
    private void setupStyles() {
        // Keyword style - bold blue
        Style keywordStyle = styleContext.addStyle(STYLE_KEYWORD, null);
        StyleConstants.setForeground(keywordStyle, KEYWORD_COLOR);
        StyleConstants.setBold(keywordStyle, true);
        
        // String style - green
        Style stringStyle = styleContext.addStyle(STYLE_STRING, null);
        StyleConstants.setForeground(stringStyle, STRING_COLOR);
        
        // Comment style - gray italic
        Style commentStyle = styleContext.addStyle(STYLE_COMMENT, null);
        StyleConstants.setForeground(commentStyle, COMMENT_COLOR);
        StyleConstants.setItalic(commentStyle, true);
        
        // Number style - dark blue
        Style numberStyle = styleContext.addStyle(STYLE_NUMBER, null);
        StyleConstants.setForeground(numberStyle, NUMBER_COLOR);
        
        // Annotation style - dark gray
        Style annotationStyle = styleContext.addStyle(STYLE_ANNOTATION, null);
        StyleConstants.setForeground(annotationStyle, ANNOTATION_COLOR);
        
        // Default style - black
        Style defaultStyle = styleContext.addStyle(STYLE_DEFAULT, null);
        StyleConstants.setForeground(defaultStyle, textPane.getForeground());
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
        if (!enabled || textPane == null) return;
        
        SwingUtilities.invokeLater(() -> {
            try {
                String text = document.getText(0, document.getLength());
                
                Style defaultStyle = styleContext.getStyle(STYLE_DEFAULT);
                StyleConstants.setForeground(defaultStyle, textPane.getForeground());
                // Reset to default style first
                document.setCharacterAttributes(0, text.length(), 
                    styleContext.getStyle(STYLE_DEFAULT), true);
                
                
                // Apply different syntax highlighting
                highlightKeywords(text);
                highlightStrings(text);
                highlightComments(text);
                highlightNumbers(text);
                highlightAnnotations(text);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Highlight Java keywords
     */
    private void highlightKeywords(String text) {
        for (String keyword : KEYWORDS) {
            Pattern pattern = Pattern.compile("\\b" + keyword + "\\b");
            Matcher matcher = pattern.matcher(text);
            
            while (matcher.find()) {
                applyStyle(matcher.start(), matcher.end(), STYLE_KEYWORD);
            }
        }
    }
    
    /**
     * Highlight strings (text between quotes)
     */
    private void highlightStrings(String text) {
        Pattern pattern = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"");
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            applyStyle(matcher.start(), matcher.end(), STYLE_STRING);
        }
    }
    
    /**
     * Highlight single-line and multi-line comments
     */
    private void highlightComments(String text) {
        // Single line comments
        Pattern singleLinePattern = Pattern.compile("//.*$", Pattern.MULTILINE);
        Matcher singleLineMatcher = singleLinePattern.matcher(text);
        
        while (singleLineMatcher.find()) {
            applyStyle(singleLineMatcher.start(), singleLineMatcher.end(), STYLE_COMMENT);
        }
        
        // Multi-line comments
        Pattern multiLinePattern = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
        Matcher multiLineMatcher = multiLinePattern.matcher(text);
        
        while (multiLineMatcher.find()) {
            applyStyle(multiLineMatcher.start(), multiLineMatcher.end(), STYLE_COMMENT);
        }
    }
    
    /**
     * Highlight numbers (integers, decimals, hex)
     */
    private void highlightNumbers(String text) {
        // Integers and decimals
        Pattern numberPattern = Pattern.compile("\\b\\d+(\\.\\d+)?\\b");
        Matcher numberMatcher = numberPattern.matcher(text);
        
        while (numberMatcher.find()) {
            applyStyle(numberMatcher.start(), numberMatcher.end(), STYLE_NUMBER);
        }
        
        // Hex numbers
        Pattern hexPattern = Pattern.compile("\\b0[xX][0-9a-fA-F]+\\b");
        Matcher hexMatcher = hexPattern.matcher(text);
        
        while (hexMatcher.find()) {
            applyStyle(hexMatcher.start(), hexMatcher.end(), STYLE_NUMBER);
        }
    }
    
    /**
     * Highlight Java annotations (@Annotation)
     */
    private void highlightAnnotations(String text) {
        Pattern pattern = Pattern.compile("@\\w+");
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            applyStyle(matcher.start(), matcher.end(), STYLE_ANNOTATION);
        }
    }
    
    /**
     * Apply a style to a range of text
     */
    private void applyStyle(int start, int end, String styleName) {
        Style style = styleContext.getStyle(styleName);
        if (style != null) {
            document.setCharacterAttributes(start, end - start, style, false);
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
     * Disable syntax highlighting and reset to default style
     */
    public void disable() {
        enabled = false;
        try {
        	String text = document.getText(0, document.getLength());

        	Style defaultStyle = styleContext.getStyle(STYLE_DEFAULT);
        	StyleConstants.setForeground(defaultStyle, textPane.getForeground());

        	document.setCharacterAttributes(0, text.length(),
        	    defaultStyle, true);
        } catch (Exception e) {
            e.printStackTrace();
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
